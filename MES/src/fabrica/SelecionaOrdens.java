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
			try {
				sem.acquire();// bloqueia a mutex
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			PriorityQueue<Ordens> heapOrdemPendente = fabrica.getCopyHeapOrdemPendente();

			if (!heapOrdemPendente.isEmpty() && GereOrdensThread.getNumberOfThreads() < 3) {
				while (!heapOrdemPendente.isEmpty()) {
					Ordens ordem = heapOrdemPendente.poll();
					List<String> lista = ordem.getReceita(0);
					System.out.println(lista);
					boolean ok = chooseOrder(lista);
					if (ok) {
						System.out.println("ordem :" + ordem);
						new OrdensThread(ordem, controlaPlc).start();// inicio thread
						GereOrdensThread.incrementNumberOfThreads();
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
			sem.release();// liberta a mutex

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
/**Se a ordem que chega quiser ir para a maquina X e esta tiver livre entao retorna OK (executa ordem)
 * Se ordem tiver que ir a 2 maquinas e so uma delas estiver livre, entao nao executa*/
	private boolean chooseOrder(List<String> lista) {
		boolean ok = false;
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
		if(ok)
			selectList(select);
		return ok;
	}

	private void selectList(List<String> select) {
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
