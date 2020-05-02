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
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		System.out.println("A SAIRR....");
		/*Espera para terminar ordem*/
		while(ordem.getQuantidade() != ordem.getPecasProduzidas()) {
			System.out.println(ordem.getQuantidade() + " - "+ ordem.getPecasProduzidas());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("*******SAIU********++");
		ordem.terminaOrdem();
		return;
	}
	public boolean selectTimer(String receita, boolean[] aux) {
		if(receita.equals("A")) {
			return  GereOrdensThread.getTempoMA()[0] < 3500 && !GereOrdensThread.getmALivreOpc()[0] ||  GereOrdensThread.getTempoMA()[0] < 3500 && !GereOrdensThread.getmALivreOpc()[1] || 
					GereOrdensThread.getTempoMA()[0] < 5000 && !GereOrdensThread.getmALivreOpc()[2];

		}else if(receita.equals("B")) {

				return  (GereOrdensThread.getTempoMB()[0] < 2500 &&  !GereOrdensThread.getmBLivreOpc()[0] && !GereOrdensThread.getmBLivre()[0]) 
						||  (GereOrdensThread.getTempoMB()[1] < 2500 &&  !GereOrdensThread.getmBLivreOpc()[1] && !GereOrdensThread.getmBLivre()[1])
						||  (GereOrdensThread.getTempoMB()[2] < 2500 && !GereOrdensThread.getmBLivreOpc()[2] && !GereOrdensThread.getmBLivre()[2]) ;

		} else if(receita.equals("C")) {
				return  GereOrdensThread.getTempoMC()[0] < 7000 &&  !GereOrdensThread.getmCLivreOpc()[0] ||  GereOrdensThread.getTempoMC()[0] < 7000 &&  !GereOrdensThread.getmCLivreOpc()[1] 
						||  GereOrdensThread.getTempoMC()[0] < 7500 && !GereOrdensThread.getmCLivreOpc()[2];
		}
		 return true;
	}
	
	public boolean executaOrdem() {
		boolean[] aux = {false,false,false};
		String receita = ordem.getReceita(0).get(0);
		if(receita.equals("A")){
			aux= GereOrdensThread.getmALivre();
		}else if(receita.equals("B")){
			aux= GereOrdensThread.getmBLivre();
		}else if(receita.equals("C")){
			aux= GereOrdensThread.getmCLivre();
		}
		System.out.println(GereOrdensThread.getmBLivre()[0] +" - "+ GereOrdensThread.getmBLivre()[1]  +" - "+ GereOrdensThread.getmBLivre()[2]);
		System.out.println(GereOrdensThread.getTempoMB()[0] +" - "+ GereOrdensThread.getTempoMB()[1]  +" - "+ GereOrdensThread.getTempoMB()[2]);
		System.out.println(aux[0] || aux[1] || aux[2] || selectTimer(receita, aux));
		System.out.println("------------------------");
			// || selectTimer()[0] < 5000 || selectTimer()[1] < 5000 || selectTimer()[2] < 5000
		return (aux[0] || aux[1] || aux[2] || selectTimer(receita, aux));
		
	}
	
	/*		System.out.println("----------------------");
		System.out.println(GereOrdensThread.getmALivre()[0] +" - "+ GereOrdensThread.getmALivre()[1]  +" - "+ GereOrdensThread.getmALivre()[2]);
		System.out.println(GereOrdensThread.getmBLivre()[0] +" - "+ GereOrdensThread.getmBLivre()[1]  +" - "+ GereOrdensThread.getmBLivre()[2]);
		System.out.println(GereOrdensThread.getmCLivre()[0] +" - "+ GereOrdensThread.getmCLivre()[1]  +" - "+ GereOrdensThread.getmCLivre()[2]);
		System.out.println("----------------------");
		System.out.println(GereOrdensThread.getTempoMA()[0] +" - "+ GereOrdensThread.getTempoMA()[1]  +" - "+ GereOrdensThread.getTempoMA()[2]);
		System.out.println(GereOrdensThread.getTempoMB()[0] +" - "+ GereOrdensThread.getTempoMB()[1]  +" - "+ GereOrdensThread.getTempoMB()[2]);
		System.out.println(GereOrdensThread.getTempoMC()[0] +" - "+ GereOrdensThread.getTempoMC()[1]  +" - "+ GereOrdensThread.getTempoMC()[2]);*/
}
