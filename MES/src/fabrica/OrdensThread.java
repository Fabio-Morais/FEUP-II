package fabrica;

import java.util.ArrayList;
import java.util.List;

import opc.OpcClient;

public class OrdensThread extends Thread {
	private Ordens ordem;
	private ControlaPlc controlaPlc;
	/**
	 * option = 0 -> descarga <br>
	 * option = 1 -> carga
	 */
	private int option;
	/**
	 * true se for uma ordem pendente, false se ja tiver sido executada
	 * anteriormente
	 */
	private boolean pendente;
	/** true se estiver a executar, false se estiver parada */
	private boolean aExecutar;
	private String[] maquinasAUsar;
	private OpcClient opc;

	public OrdensThread(Ordens ordem, ControlaPlc controlaPlc, boolean pendente) {
		super();
		this.ordem = ordem;
		this.controlaPlc = controlaPlc;
		this.option = (ordem.getTransform() == null) ? 0 : 1;
		this.pendente = pendente;
		this.aExecutar = true;
		this.maquinasAUsar = new String[3];
		this.maquinasAUsar[0] = "";
		this.maquinasAUsar[1] = "";
		this.maquinasAUsar[2] = "";
		this.opc = OpcClient.getInstance();

	}

	private boolean selectRunOrder() {
		boolean returnValue = true;
		try {
			GeneralSemaphore.getSem5().acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (option == 1) {
			returnValue = controlaPlc.runOrder(this.ordem);
		} else if (option == 0) {
			controlaPlc.runOrdemDescarga(this.ordem);
		}
		System.out.println("\tenvia plc, ordem: " + ordem.getNumeroOrdem());
		GeneralSemaphore.getSem5().release();
		return returnValue;
	}

	private void reservaMaquinas() {
		if (!this.ordem.isSpeedMode()) {
			return;
		}
		if (GereOrdensThread.getmALivreSeleciona()[0].equals("") && !this.maquinasAUsar[0].equals("A")) {
			this.maquinasAUsar[0] = "A";
			for (int i = 0; i < 3; i++)
				GereOrdensThread.setmALivreSeleciona("S" + this.ordem.getNumeroOrdem(), i);
		} else if (GereOrdensThread.getmBLivreSeleciona()[0].equals("") && !this.maquinasAUsar[0].equals("B")) {
			this.maquinasAUsar[1] = "B";
			for (int i = 0; i < 3; i++)
				GereOrdensThread.setmBLivreSeleciona("S" + this.ordem.getNumeroOrdem(), i);
		} else if (GereOrdensThread.getmCLivreSeleciona()[0].equals("") && !this.maquinasAUsar[0].equals("C")) {
			this.maquinasAUsar[2] = "C";
			for (int i = 0; i < 3; i++)
				GereOrdensThread.setmCLivreSeleciona("S" + this.ordem.getNumeroOrdem(), i);
		}
	}

	private boolean manyOrders(List<String> rect) {
		List<String> maquinas = new ArrayList<>();
		boolean[] maqA = GereOrdensThread.getmALivre();
		boolean[] maqB = GereOrdensThread.getmBLivre();
		long[] tempoB = GereOrdensThread.getTempoMB();
		long[] tempoC = GereOrdensThread.getTempoMC();
		for (int i = 0; i < rect.size(); i += 3) {
			maquinas.add(rect.get(i));
		}
		/* PODE ENCHER AQUI */
		if (maquinas.size() == 3) {
			return (maqA[0] || maqA[1] || maqA[2]);
		} else if (maquinas.size() == 2) {
			if (maquinas.get(0).equals("A")) {
				return ((maqA[0] && tempoB[0] < 3600)
						|| (maqA[0] && (tempoB[0] >= (Long.valueOf(rect.get(4)) * 1000 - 20))))
						|| ((maqA[1] && tempoB[1] < 4500)
								|| (maqA[1] && (tempoB[1] >= (Long.valueOf(rect.get(4)) * 1000 - 20))))
						|| ((maqA[2] && tempoB[2] < 4900)
								|| (maqA[2] && (tempoB[2] >= (Long.valueOf(rect.get(4)) * 1000 - 20))));
			} else if (maquinas.get(0).equals("B")) {

				return ((maqB[0] && tempoC[0] < 4100)
						|| (maqB[0] && (tempoC[0] >= (Long.valueOf(rect.get(4)) * 1000 - 20))))
						|| ((maqB[1] && tempoC[1] < 4500)
								|| (maqB[1] && (tempoC[1] >= (Long.valueOf(rect.get(4)) * 1000 - 20))))
						|| ((maqB[2] && tempoC[2] < 4900)
								|| (maqB[2] && (tempoC[2] >= (Long.valueOf(rect.get(4)) * 1000 - 20))));

			}
		}
		return false;
	}

	private boolean speed() {
		for (int i = 0; i < 23; i += 11) {

			int c = (i == 22) ? 15 : 0;
			int x = (i == 22) ? 15 : i;
			String receita = ordem.getReceita(x, c).get(0);
			boolean[] aux = { false, false, false };
			if (receita.equals("A") && this.maquinasAUsar[0].equals("A")) {
				aux = GereOrdensThread.getmALivre();
			} else if (receita.equals("B") && this.ordem.getPecasPendentes() > 2 && this.maquinasAUsar[1].equals("B")) {
				aux = GereOrdensThread.getmBLivre();
			} else if (receita.equals("C") && this.maquinasAUsar[2].equals("C")) {
				aux = GereOrdensThread.getmCLivre();
			}
			if (aux[0]) {
				System.out.println(receita + "->" + aux[0]);
				return true;
			}
		}

		return false;
	}

	private boolean executaOrdem(int limite) {
		if(limite <= 0)
			return false;
		/* SERVE PARA NAO DEIXAR SAIR PE�AS SEM STOCK */
		if (this.ordem.getTransform() != null) {
			Ordens.Transform x = this.ordem.getTransform();
			short posPeca = Short.valueOf(x.getFrom().substring(1, 2));
			short stock = Stock.getPecaStock((short) (posPeca - 1));
			if (stock <= 0) {
				return false;
			}
		} else if (this.ordem.getUnload() != null) {
			Ordens.Unload x = this.ordem.getUnload();
			short posPeca = Short.valueOf(x.getType().substring(1, 2));
			short stock = Stock.getPecaStock((short) (posPeca - 1));
			if (stock <= 0) {
				return false;
			}
		}

		boolean[] aux = { false, false, false };
		boolean[] espera = { false, false, false };
		short[] countMaq = { 0, 0, 0 };
		long[] auxTempo = GereOrdensThread.getTempoMC();

		float smallest = 0;
		if (auxTempo[0] <= auxTempo[1] && auxTempo[0] <= auxTempo[2]) {
			smallest = auxTempo[0] / 1000;
		} else if (auxTempo[1] <= auxTempo[2] && auxTempo[1] <= auxTempo[0]) {
			smallest = auxTempo[1] / 1000;
		} else {
			smallest = auxTempo[2] / 1000;
		}
		String receita = ordem.getReceita((int) smallest, 0).get(0);// nao serve para A->B, pois so vai buscar o
																	// primeiro
		GereOrdensThread.setmALivre(opc.getValueBool("SFS", "mA1Livre"), 0);
		GereOrdensThread.setmALivre(opc.getValueBool("SFS", "mA2Livre"), 1);
		GereOrdensThread.setmALivre(opc.getValueBool("SFS", "mA3Livre"), 2);
		GereOrdensThread.setmBLivre(opc.getValueBool("SFS", "mB1Livre"), 0);
		GereOrdensThread.setmBLivre(opc.getValueBool("SFS", "mB2Livre"), 1);
		GereOrdensThread.setmBLivre(opc.getValueBool("SFS", "mB3Livre"), 2);
		GereOrdensThread.setmCLivre(opc.getValueBool("SFS", "mC1Livre"), 0);
		GereOrdensThread.setmCLivre(opc.getValueBool("SFS", "mC2Livre"), 1);
		GereOrdensThread.setmCLivre(opc.getValueBool("SFS", "mC3Livre"), 2);
		if (this.ordem.isSpeedMode()) {
			return speed();

		} else {
			if (receita.equals("A")) {
				aux = GereOrdensThread.getmALivre();
				espera = GereOrdensThread.getmAEspera();
				countMaq[0] = opc.getValue("SFS", "mB1Cont")[0];
				countMaq[1] = opc.getValue("SFS", "mB2Cont")[0];
				countMaq[2] = opc.getValue("SFS", "mB3Cont")[0];
			} else if (receita.equals("B")) {
				aux = GereOrdensThread.getmBLivre();
				espera = GereOrdensThread.getmBEspera();
				countMaq[0] = opc.getValue("SFS", "mC1Cont")[0];
				countMaq[1] = opc.getValue("SFS", "mC2Cont")[0];
				countMaq[2] = opc.getValue("SFS", "mC3Cont")[0];
			} else if (receita.equals("C")) {
				aux = GereOrdensThread.getmCLivre();
				espera = GereOrdensThread.getmCEspera();
			}
		}
		/*Descarga */
		if (this.option == 0 && limite > 2) {
			return GereOrdensThread.isMaquinasOcupadas();
		} else if (this.option == 0) {
			return false;
		}
		/* � o modo cr7, ex: c1->c2->c3 */
		if (this.ordem.isSpeedMode()) {
			return aux[0];
		}

		List<String> rect = ordem.getReceita((int) smallest, 0);
		/* Faz em 2 ou 3 maquinas diferentes */
		if (rect.size() > 3 && !rect.get(0).equals(rect.get(3))) {
			return manyOrders(rect);
		}
		
		return (aux[0] || (aux[1]) || (aux[2]));
		// so manda descarga quando < x tempo
	}


	@Override
	public void run() {
		for (String x : maquinasAUsar)
			System.out.println("maquinas a usar: " + x);
		if (this.pendente)
			this.ordem.executaOrdem();

		/* Envia ordens */
		while (this.ordem.getPecasPendentes() > 0) {
			int limite = 3;
			/*System.out.println(Arrays.toString(GereOrdensThread.getmALivreSeleciona()));
			System.out.println(Arrays.toString(GereOrdensThread.getmBLivreSeleciona()));
			System.out.println(Arrays.toString(GereOrdensThread.getmCLivreSeleciona()));*/
			if (aExecutar) {
				reservaMaquinas();// Se estiver a usar a maquina A e a B estiver livre coloca tambem a usar
				try {
					GereOrdensThread.sem.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (executaOrdem(limite) && this.ordem.getPecasPendentes() > 0) {
					if (selectRunOrder()) {
						if (this.ordem.getUnload() != null) {
							try {
								GereOrdensThread.sem2.acquire();
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
						this.ordem.pecaParaProducao();
						System.out.println(ordem.getNumeroOrdem() + " -> "+ limite);
						limite--;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

				GereOrdensThread.sem.release();
			}
			if (this.ordem.getUnload() != null) {

				try {
					Thread.sleep(4500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				GereOrdensThread.sem2.release();

			} else {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		System.out.println("A SAIRR.... numero de ordem: " + this.ordem.getNumeroOrdem());
		resetMaquinaSelect();
		GereOrdensThread.setVoltaInicio(true);
		/* Espera para terminar ordem */
		while (this.ordem.getQuantidade() != this.ordem.getPecasProduzidas()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out
				.println("*******SAIU DA ORDEM (ordensThread)******** numero de ordem:" + this.ordem.getNumeroOrdem());
		this.ordem.terminaOrdem();
		GereOrdensThread.decrementNumberOfThreads();// para permitir entrar mais
		return;
	}

	private void resetMaquinaSelect() {
		for (int i = 0; i < ordem.getReceita(0, 0).size(); i += 3) {
			if (ordem.getReceita(0, 0).get(i).equals("A")) {
				for (int j = 0; j < 3; j++) {
					GereOrdensThread.setmALivreSeleciona("", j);
				}
			} else if (ordem.getReceita(0, 0).get(i).equals("B")) {
				for (int j = 0; j < 3; j++) {
					GereOrdensThread.setmBLivreSeleciona("", j);
				}
			} else if (ordem.getReceita(0, 0).get(i).equals("C")) {
				for (int j = 0; j < 3; j++) {
					GereOrdensThread.setmCLivreSeleciona("", j);
				}
			}
		}

	}

	public Ordens getOrdem() {
		return ordem;
	}

	public void setaExecutar(boolean aExecutar) {
		this.aExecutar = aExecutar;
	}

	public boolean isaExecutar() {
		return aExecutar;
	}

	@Override
	public String toString() {
		return "OrdensThread [ordem=" + ordem.getNumeroOrdem() + ", aExecutar=" + aExecutar + "]";
	}

	public String[] getMaquinasAUsar() {
		return maquinasAUsar;
	}

	public void setMaquinaAUsar(String maquinasAUsar) {
		if (maquinasAUsar.equals("A")) {
			this.maquinasAUsar[0] = maquinasAUsar;
			if (this.ordem.isSpeedMode()) {
				for(int i = 0; i < 3; i++)
					GereOrdensThread.setmALivreSeleciona("S"+ordem.getNumeroOrdem(), i);
			}
		} else if (maquinasAUsar.equals("B")) {
			this.maquinasAUsar[1] = maquinasAUsar;
			if (this.ordem.isSpeedMode()) {
				for(int i = 0; i < 3; i++)
					GereOrdensThread.setmBLivreSeleciona("S"+ordem.getNumeroOrdem(), i);
			}
		} else if (maquinasAUsar.equals("C")) {
			this.maquinasAUsar[2] = maquinasAUsar;
			if (this.ordem.isSpeedMode()) {
				for(int i = 0; i < 3; i++)
					GereOrdensThread.setmCLivreSeleciona("S"+ordem.getNumeroOrdem(), i);
			}
		}
	}

	public void removeMaquinaAUsar(String maquinasAUsar) {
		if (maquinasAUsar.equals("A")) {
			this.maquinasAUsar[0] = "";
		} else if (maquinasAUsar.equals("B")) {
			this.maquinasAUsar[1] = "";

		} else if (maquinasAUsar.equals("C")) {
			this.maquinasAUsar[2] = "";
		}
	}
}
