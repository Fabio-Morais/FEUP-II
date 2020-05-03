package fabrica;

public class OrdensThread extends Thread {
	private Ordens ordem;
	private ControlaPlc controlaPlc;
	/**
	 * option = 0 -> descarga <br>
	 * option = 1 -> carga
	 */
	private int option;

	public OrdensThread(Ordens ordem, ControlaPlc controlaPlc) {
		super();
		this.ordem = ordem;
		this.controlaPlc = controlaPlc;
		this.option = (ordem.getTransform() == null) ? 0 : 1;
	}

	private void selectRunOrder() {
		if (option == 1) {
			System.out.println("corre carga");
			controlaPlc.runOrder(this.ordem);
		} else if (option == 0) {
			System.out.println("corre descarga");
			controlaPlc.runOrdemDescarga(this.ordem);
		}
	}

	@Override
	public void run() {
		this.ordem.executaOrdem();
		/* Envia ordens */
		while (this.ordem.getPecasPendentes() > 0) {

			if (executaOrdem()) {

				try {
					GereOrdensThread.sem.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				selectRunOrder();
				this.ordem.pecaParaProducao();
				GereOrdensThread.sem.release();
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		System.out.println("A SAIRR....");
		/* Espera para terminar ordem */
		while (this.ordem.getQuantidade() != this.ordem.getPecasProduzidas()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("*******SAIU DA ORDEM (ordensThread)********++");
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
		String receita = ordem.getReceita(0).get(0);

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
