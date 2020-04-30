package fabrica;

public class OrdensThread extends Thread {
	private Ordens ordem;
	private ControlaPlc controlaPlc;

	public OrdensThread(Ordens ordem, ControlaPlc controlaPlc) {
		super();
		this.ordem = ordem;
		this.controlaPlc = controlaPlc;
	}

	@Override
	public void run() {
		this.ordem.executaOrdem();
		/*Envia ordens*/
		while(ordem.getPecasPendentes() > 0) {

			/*se faltar 5 segundos para sair uma peça da maquina envia ordem
			 * <5 && ocupado || livre
			 * */
			if(executaOrdem()) {
				try {
					GereOrdensThread.sem.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("corre ordem");
				controlaPlc.runOrder(ordem);
				ordem.pecaParaProducao();
				GereOrdensThread.sem.release();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("A SAIRR....");
		/*Espera para terminar ordem*/
		while(ordem.getQuantidade() != ordem.getPecasProduzidas()) {
			System.out.println(ordem.getQuantidade() + " - "+ ordem.getPecasProduzidas());
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("*******SAIU********++");
		ordem.terminaOrdem();
		return;
	}
	public long[] selectTimer() {
		if(ordem.getReceita(0).equals("A")) {
			return GereOrdensThread.getTempoMA();

		}else if(ordem.getReceita(0).equals("B")) {
			return GereOrdensThread.getTempoMB();

		} else if(ordem.getReceita(0).equals("C")) {
			return GereOrdensThread.getTempoMC();

		}
		return GereOrdensThread.getTempoMA();
	}
	
	public boolean executaOrdem() {
		boolean[] aux = {false,false,false};
		String receita = ordem.getReceita(0).get(0);
		if(receita.equals("A")){
			aux= GereOrdensThread.getmALivre();
			//System.out.println("AAAAAAAAAAA");
		}else if(receita.equals("B")){
			aux= GereOrdensThread.getmBLivre();
			System.out.println("BBBBBBBBB");
		}else if(receita.equals("C")){
			aux= GereOrdensThread.getmCLivre();
			System.out.println("CCCCCCCCCC");
		}
		//System.out.println(aux[0] +" - "+ aux[1]  +" - "+ aux[2]);
			// || selectTimer()[0] < 5000 || selectTimer()[1] < 5000 || selectTimer()[2] < 5000
		return (aux[0] || aux[1] || aux[2]);
		
	}
}
