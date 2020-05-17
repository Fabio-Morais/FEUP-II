package fabrica;

public class OrdensThread extends Thread {
	private Ordens ordem;
	private ControlaPlc controlaPlc;
	/**
	 * option = 0 -> descarga <br>
	 * option = 1 -> carga
	 */
	private int option;
	/**true se for uma ordem pendente, false se ja tiver sido executada anteriormente*/
	private boolean pendente;

	public OrdensThread(Ordens ordem, ControlaPlc controlaPlc, boolean pendente) {
		super();
		this.ordem = ordem;
		this.controlaPlc = controlaPlc;
		this.option = (ordem.getTransform() == null) ? 0 : 1;
		this.pendente = pendente;
	}

	private void selectRunOrder() {
		try {
			GeneralSemaphore.getSem5().acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (option == 1) {
			controlaPlc.runOrder(this.ordem);
		} else if (option == 0) {
			controlaPlc.runOrdemDescarga(this.ordem);
		}
		GeneralSemaphore.getSem5().release();
		
	}

	@Override
	public void run() {
		if(this.pendente)
			this.ordem.executaOrdem();
		/* Envia ordens */
		while (this.ordem.getPecasPendentes() > 0) {

			if (executaOrdem()) {

				try {
					GereOrdensThread.sem.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				/** se for descarga envia 4 simultaneas, se for transformaçao é 3*/
				int numeroPecas = (option ==0) ? 4 : 3; 
				for(int i=0; i<numeroPecas; i++) {
					if(this.ordem.getPecasPendentes()<=0)
						break;
					selectRunOrder();
					this.ordem.pecaParaProducao();
				}


				GereOrdensThread.sem.release();
			}

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		System.out.println("A SAIRR.... numero de ordem: "+ this.ordem.getNumeroOrdem());
		/* Espera para terminar ordem */
		while (this.ordem.getQuantidade() != this.ordem.getPecasProduzidas()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("*******SAIU DA ORDEM (ordensThread)******** numero de ordem:"+ this.ordem.getNumeroOrdem());
		this.ordem.terminaOrdem();
		resetMaquinaSelect();
		GereOrdensThread.decrementNumberOfThreads();// para permitir entrar mais
		GereOrdensThread.setVoltaInicio(true);
		return;
	}

	private void resetMaquinaSelect() {
		for (int i = 0; i < ordem.getReceita(0).size(); i += 3) {
			if (ordem.getReceita(0).get(i).equals("A")) {
				for (int j = 0; j < 3; j++) {
					GereOrdensThread.setmALivreSeleciona(true, j);
				}
			} else if (ordem.getReceita(0).get(i).equals("B")) {
				for (int j = 0; j < 3; j++) {
					GereOrdensThread.setmBLivreSeleciona(true, j);
				}
			} else if (ordem.getReceita(0).get(i).equals("C")) {
				for (int j = 0; j < 3; j++) {
					GereOrdensThread.setmCLivreSeleciona(true, j);
				}
			}
		}

	}

	public boolean executaOrdem() {
		if (this.option == 0) {
			return true;
		}
		boolean[] aux = { false, false, false };
		long[] auxTempo = GereOrdensThread.getTempoMC();


		float smallest=0;
		if (auxTempo[0] <= auxTempo[1] && auxTempo[0] <= auxTempo[2]) {
		    smallest = auxTempo[0]/1000;
		} else if (auxTempo[1] <= auxTempo[2] && auxTempo[1] <= auxTempo[0]) {
		    smallest = auxTempo[1]/1000;
		} else {
		    smallest = auxTempo[2]/1000;
		}
		String receita = ordem.getReceita((int)smallest).get(0);// nao serve para A->B, pois so vai buscar o primeiro
		/*System.out.println("smallest: "+ (int)smallest);
		System.out.println("receita: "+ receita);*/
		if (receita.equals("A")) {
			aux = GereOrdensThread.getmALivre();
		} else if (receita.equals("B")) {
			aux = GereOrdensThread.getmBLivre();
		} else if (receita.equals("C")) {
			aux = GereOrdensThread.getmCLivre();
		}

		return (aux[0] || aux[1] || aux[2]);

	}

}
