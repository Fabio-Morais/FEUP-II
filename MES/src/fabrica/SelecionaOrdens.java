package fabrica;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

public class SelecionaOrdens extends Thread {
	private static SelecionaOrdens instance = null;
	private Fabrica fabrica;
	private Semaphore sem;

	private SelecionaOrdens(Fabrica fabrica) {
		this.sem = GeneralSemaphore.getSem();
		this.fabrica = fabrica;
	}

	@Override
	public void run() {
		ControlaPlc controlaPlc = new ControlaPlc();

		int auxPre = -1;
		/* seleciona as ordens que podem entrar em paralelo */
		while (true) {
			int aux = GereOrdensThread.getNumberOfThreads();

			if (aux != auxPre) {
				System.out.println("Numero de threads " + aux);
				auxPre = aux;
			}

			PriorityQueue<Ordens> heapOrdemPendente = fabrica.getCopyHeapOrdemPendente();

			if (!heapOrdemPendente.isEmpty() && GereOrdensThread.getNumberOfThreads() < 5) {
				while (!heapOrdemPendente.isEmpty()) {
					Ordens ordem = heapOrdemPendente.poll();
					boolean ok = false;
					if(GereOrdensThread.isVoltaInicio()){
						GereOrdensThread.setVoltaInicio(false);
						break;
					}else {
						ok = chooseOrder(ordem);
					}
					
					if (ok) {
						System.out.println(" executa ordem :" + ordem);
						OrdensThread x =new OrdensThread(ordem, controlaPlc);// inicio thread
						x.setName("Thread "+ordem.getNumeroOrdem());
						x.start();
						GereOrdensThread.incrementNumberOfThreads();
						break;
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
			/*System.out.println();
			System.out.println(Arrays.toString(GereOrdensThread.getTempoMA())+"\t"+Arrays.toString(GereOrdensThread.getmALivreSeleciona()));
			System.out.println(Arrays.toString(GereOrdensThread.getTempoMB())+"\t"+Arrays.toString(GereOrdensThread.getmBLivreSeleciona()));
			System.out.println(Arrays.toString(GereOrdensThread.getTempoMC())+"\t"+Arrays.toString(GereOrdensThread.getmCLivreSeleciona()));
			System.out.println();*/
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
/**Se a ordem que chega quiser ir para a maquina X e esta tiver livre entao retorna OK (executa ordem)
 * Se ordem tiver que ir a 2 maquinas e so uma delas estiver livre, entao nao executa
 * @param lista - se lista tiver no indice 0 o D é descarga, caso contrario é carga
 * */
	private boolean chooseOrder(Ordens ordem) {
		boolean ok = false;
		List<String> lista = ordem.getReceita(0);
		List<String> lista2= ordem.getReceita(1000);
		/*Se lista tiver um D entao é uma descarga*/
		if(lista.get(0).equals("D")) {
			return true;
		}

		List<String> select = new ArrayList<>();
		for (int i = 0; i < lista.size(); i += 3) {
			String x = lista.get(i);
			if (x.equals("A") && (GereOrdensThread.getmALivreSeleciona()[0] || GereOrdensThread.getmALivreSeleciona()[1] || GereOrdensThread.getmALivreSeleciona()[2])) {
				ok = true;
				select.add("A");
			} else if (x.equals("B") && (GereOrdensThread.getmBLivreSeleciona()[0] || GereOrdensThread.getmBLivreSeleciona()[1] || GereOrdensThread.getmBLivreSeleciona()[2])) {
				ok = true;
				select.add("B");
			} else if (x.equals("C") && (GereOrdensThread.getmCLivreSeleciona()[0] || GereOrdensThread.getmCLivreSeleciona()[1] || GereOrdensThread.getmCLivreSeleciona()[2])) {
				ok = true;
				select.add("C");
			}else {
				ok= false;
			}
		}
		/**Se for maior que 3 entao pode usar mais que uma maquina, ex: (usar todas da C e usar a A)*/
		if(ordem.getPecasPendentes() > 3) {
			if(!lista.equals(lista2)) {
			for (int i = 0; i < lista2.size(); i += 3) {
				String x = lista2.get(i);
				if (x.equals("A") && (GereOrdensThread.getmALivreSeleciona()[0] || GereOrdensThread.getmALivreSeleciona()[1] || GereOrdensThread.getmALivreSeleciona()[2])) {
					ok = true;
					select.add("A");
				} else if (x.equals("B") && (GereOrdensThread.getmBLivreSeleciona()[0] || GereOrdensThread.getmBLivreSeleciona()[1] || GereOrdensThread.getmBLivreSeleciona()[2])) {
					ok = true;
					select.add("B");
				} else if (x.equals("C") && (GereOrdensThread.getmCLivreSeleciona()[0] || GereOrdensThread.getmCLivreSeleciona()[1] || GereOrdensThread.getmCLivreSeleciona()[2])) {
					ok = true;
					select.add("C");
				}else {
					ok= false;
				}
			}
		}
		}
		if(select.size()>0)
			System.out.println("***"+select);
		if(ok)
			selectList(select);
		return ok;
	}

	private void selectList(List<String> select) {
		System.out.println("entrou no select: "+select);
		for(String maquina : select) {
			if(maquina.equals("A")) {
				for(int i=0; i<3; i++) {
					GereOrdensThread.setmALivreSeleciona(false, i);
				}
			}else if(maquina.equals("B")) {
				for(int i=0; i<3; i++) {
					GereOrdensThread.setmBLivreSeleciona(false, i);
				}
			}else if(maquina.equals("C")) {
				for(int i=0; i<3; i++) {
					GereOrdensThread.setmCLivreSeleciona(false, i);
				}
			}
		}
	}

	public static SelecionaOrdens getInstance(Fabrica fabrica) {
		if (instance == null)
			instance = new SelecionaOrdens(fabrica);
		return instance;
	}
}
