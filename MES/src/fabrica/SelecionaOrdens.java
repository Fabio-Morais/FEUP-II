package fabrica;

import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

public class SelecionaOrdens extends Thread {
	private static SelecionaOrdens instance = null;
	private Fabrica fabrica;
	private Semaphore sem;
	boolean[] mALivre = {true, true, true};
	boolean[] mBLivre = {true, true, true};
	boolean[] mCLivre = {true, true, true};
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
					boolean ok = chooseOrder(lista);

					if (ok) {
						System.out.println("ordem :" + ordem);
						new OrdensThread(ordem, controlaPlc).start();// inicio thread
						GereOrdensThread.incrementNumberOfThreads();
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
		
		for (int i = 0; i < lista.size(); i += 3) {
			String x = lista.get(i);
			if (x.equals("A") && (mALivre[0] || mALivre[1] || mALivre[2])) {
				System.out.println("x : " + x);
				ok = true;
				mALivre[0]=false;
				mALivre[1]=false;
				mALivre[2]=false;
			} else if (x.equals("B") && (mBLivre[0] || mBLivre[1] || mBLivre[2])) {
				System.out.println("x : " + x);
				ok = true;
				mBLivre[0]=false;
				mBLivre[1]=false;
				mBLivre[2]=false;
			} else if (x.equals("C") && (mCLivre[0] || mCLivre[1] || mCLivre[2])) {
				System.out.println("x : " + x);
				ok = true;
				mCLivre[0]=false;
				mCLivre[1]=false;
				mCLivre[2]=false;
			}else {
				ok= false;
			}
		}
		return ok;
	}

	public static SelecionaOrdens getInstance(Fabrica fabrica) {
		if (instance == null)
			instance = new SelecionaOrdens(fabrica);
		return instance;
	}
}
