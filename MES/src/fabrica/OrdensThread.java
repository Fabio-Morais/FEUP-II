package fabrica;

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

	public OrdensThread(Ordens ordem, ControlaPlc controlaPlc, boolean pendente) {
		super();
		this.ordem = ordem;
		this.controlaPlc = controlaPlc;
		this.option = (ordem.getTransform() == null) ? 0 : 1;
		this.pendente = pendente;
		this.aExecutar = true;

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

	private boolean executaOrdem(int limite) {

		boolean[] aux = { false, false, false };
		long[] auxTempo = GereOrdensThread.getTempoMC();

		float smallest = 0;
		if (auxTempo[0] <= auxTempo[1] && auxTempo[0] <= auxTempo[2]) {
			smallest = auxTempo[0] / 1000;
		} else if (auxTempo[1] <= auxTempo[2] && auxTempo[1] <= auxTempo[0]) {
			smallest = auxTempo[1] / 1000;
		} else {
			smallest = auxTempo[2] / 1000;
		}
		String receita = ordem.getReceita((int) smallest).get(0);// nao serve para A->B, pois so vai buscar o primeiro

		if (receita.equals("A")) {
			aux = GereOrdensThread.getmALivre();
		} else if (receita.equals("B")) {
			aux = GereOrdensThread.getmBLivre();
		} else if (receita.equals("C")) {
			aux = GereOrdensThread.getmCLivre();
		}
		if (this.option == 0 && GereOrdensThread.isMaquinasOcupadas() && limite > 0) {
			return true;
		} else if (this.option == 0) {
			return false;
		}
		return (aux[0] || aux[1] || aux[2]);

	}

	@Override
	public void run() {
		if (this.pendente)
			this.ordem.executaOrdem();
		/* Envia ordens */
		while (this.ordem.getPecasPendentes() > 0) {
			int limite = 1;

			if (aExecutar) {
				try {
					GereOrdensThread.sem.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (executaOrdem(limite) && this.ordem.getPecasPendentes() > 0) {
					if(selectRunOrder()) {
						this.ordem.pecaParaProducao();
						limite--;
					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}

				GereOrdensThread.sem.release();
			}

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		System.out.println("*******SAIU DA ORDEM (ordensThread)******** numero de ordem:" + this.ordem.getNumeroOrdem());
		this.ordem.terminaOrdem();
		GereOrdensThread.decrementNumberOfThreads();// para permitir entrar mais
		return;
	}

	private void resetMaquinaSelect() {
		for (int i = 0; i < ordem.getReceita(0).size(); i += 3) {
			if (ordem.getReceita(0).get(i).equals("A")) {
				for (int j = 0; j < 3; j++) {
					GereOrdensThread.setmALivreSeleciona("", j);
				}
			} else if (ordem.getReceita(0).get(i).equals("B")) {
				for (int j = 0; j < 3; j++) {
					GereOrdensThread.setmBLivreSeleciona("", j);
				}
			} else if (ordem.getReceita(0).get(i).equals("C")) {
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

}
