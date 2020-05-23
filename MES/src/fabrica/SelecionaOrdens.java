package fabrica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

public class SelecionaOrdens extends Thread {
	private static SelecionaOrdens instance = null;
	private Fabrica fabrica;
	private Semaphore sem;
	private ArrayList<OrdensThread> ordensEmExecucao;
	private ControlaPlc controlaPlc;

	private SelecionaOrdens(Fabrica fabrica) {
		this.sem = GeneralSemaphore.getSem();
		this.fabrica = fabrica;
		this.ordensEmExecucao = new ArrayList<>();
		this.controlaPlc = new ControlaPlc();
	}

	@Override
	public void run() {
		// SINCRONIZA TUDO PRIMEIRO
		this.sincronizaOrdens();

		int auxPre = -1;
		/* seleciona as ordens que podem entrar em paralelo */
		while (true) {
			int aux = GereOrdensThread.getNumberOfThreads();

			if (aux != auxPre) {
				System.out.println("Numero de threads " + aux);
				auxPre = aux;
			}

			PriorityQueue<Ordens> heapOrdemPendente = fabrica.getCopyHeapOrdemPendente();
			if (GereOrdensThread.isVoltaInicio()) {
				GereOrdensThread.setVoltaInicio(false);
				executaOrdensEspera();

			}
			maquinasOcupadas();
			// Nao permite mais de 5 ordens ao mesmo temo
			if (!heapOrdemPendente.isEmpty() && GereOrdensThread.getNumberOfThreads() < 6) {
				while (!heapOrdemPendente.isEmpty()) {
					maquinasOcupadas();
					Ordens ordem = heapOrdemPendente.poll();
					boolean ok = false;
					String troca = "";

					if (GereOrdensThread.isVoltaInicio()) {
						GereOrdensThread.setVoltaInicio(false);
						executaOrdensEspera();
						break;
					} else {
						ok = chooseOrder(ordem);
						troca = trocaOrdem(ordem);
					}

					if (ok) {
						executaOrdem(ordem);
						break;
					} else if (!troca.equals("")) {
						trocaOrdem(ordem, troca);
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					/*System.out.println("--------------------");
					System.out.println(Arrays.toString(GereOrdensThread.getmALivreSeleciona()));
					System.out.println(Arrays.toString(GereOrdensThread.getmBLivreSeleciona()));
					System.out.println(Arrays.toString(GereOrdensThread.getmCLivreSeleciona()));
					System.out.println("--------------------");*/
				}

			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void maquinasOcupadas() {
		long[] auxTempo = GereOrdensThread.getTempoMC();
		boolean bool = true;
		float smallest = 0;
		if (auxTempo[0] <= auxTempo[1] && auxTempo[0] <= auxTempo[2]) {
			smallest = auxTempo[0] / 1000;
		} else if (auxTempo[1] <= auxTempo[2] && auxTempo[1] <= auxTempo[0]) {
			smallest = auxTempo[1] / 1000;
		} else {
			smallest = auxTempo[2] / 1000;
		}
		for (int i = 0; i < ordensEmExecucao.size(); i++) {
			String x = ordensEmExecucao.get(i).getOrdem().getReceita((int) smallest,0).get(0);
			if (!x.equals("D")) {
				if (x.equals("A") && !(GereOrdensThread.getmALivre()[0] && GereOrdensThread.getmALivre()[1]
						&& GereOrdensThread.getmALivre()[2])) {
					bool &= true;
				} else if (x.equals("B") && !(GereOrdensThread.getmBLivre()[0] && GereOrdensThread.getmBLivre()[1]
						&& GereOrdensThread.getmBLivre()[2])) {
					bool &= true;
				} else if (x.equals("C") && !(GereOrdensThread.getmCLivre()[0] && GereOrdensThread.getmCLivre()[1]
						&& GereOrdensThread.getmCLivre()[2])) {
					bool &= true;
				} else {
					bool &= false;
				}
			}
		}
		if (ordensEmExecucao.size() > 0)
			GereOrdensThread.setMaquinasOcupadas(bool);
		else
			GereOrdensThread.setMaquinasOcupadas(false);

		//System.out.println("--> Maquinas ocupadas: " + GereOrdensThread.isMaquinasOcupadas());
	}

	/**
	 * Se a ordem que chega quiser ir para a maquina X e esta tiver livre entao
	 * retorna OK (executa ordem) Se ordem tiver que ir a 2 maquinas e so uma delas
	 * estiver livre, entao nao executa
	 * 
	 * @param lista - se lista tiver no indice 0 o D é descarga, caso contrario é
	 *              carga
	 */
	private boolean chooseOrder(Ordens ordem) {
		boolean ok = false;
		List<String> lista = ordem.getReceita(0,0);
		List<String> lista2 = ordem.getReceita(1000,0);
		/* Se lista tiver um D entao é uma descarga */
		if (lista.get(0).equals("D")) {
			return true;
		}

		List<String> select = new ArrayList<>();
		ok = valida(lista, select);
		/**
		 * Se for maior que 3 entao pode usar mais que uma maquina, ex: (usar todas da C
		 * e usar a A)
		 */
		if (ordem.getPecasPendentes() > 3 && !lista.equals(lista2)) {
			ok = valida(lista2, select);
		}

		if (ok)
			selectList(select, ordem.getNumeroOrdem());
		return ok;
	}

	private boolean valida(List<String> lista2, List<String> select) {
		boolean ok = false;
		for (int i = 0; i < lista2.size(); i += 3) {
			String x = lista2.get(i);
			if (x.equals("A") && (GereOrdensThread.mALivreSeleciona())) {
				ok = true;
				select.add("A");
			} else if (x.equals("B") && (GereOrdensThread.mBLivreSeleciona())) {
				ok = true;
				select.add("B");
			} else if (x.equals("C") && (GereOrdensThread.mCLivreSeleciona())) {
				ok = true;
				select.add("C");
			} else {
				ok = false;
			}
		}
		return ok;
	}

	private void selectList(List<String> select, String numeroOrdem) {
		System.out.println("entrou no select: " + select);
		for (String maquina : select) {
			if (maquina.equals("A")) {
				for (int i = 0; i < 3; i++) {
					GereOrdensThread.setmALivreSeleciona(numeroOrdem, i);
				}
			} else if (maquina.equals("B")) {
				for (int i = 0; i < 3; i++) {
					GereOrdensThread.setmBLivreSeleciona(numeroOrdem, i);
				}
			} else if (maquina.equals("C")) {
				for (int i = 0; i < 3; i++) {
					GereOrdensThread.setmCLivreSeleciona(numeroOrdem, i);
				}
			}
		}
	}

	private void sincronizaOrdens() {
		try {
			GeneralSemaphore.getSem4().acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		HashMap<String, Ordens> aux = fabrica.getCopyHeapOrdemExecucao();
		for (Map.Entry<String, Ordens> entry : aux.entrySet()) {
			Ordens value = entry.getValue();
			if (chooseOrder(value)) {
				OrdensThread x = new OrdensThread(value, controlaPlc, false);// inicio thread
				x.setName("Thread " + value.getNumeroOrdem());
				x.start();
				GereOrdensThread.incrementNumberOfThreads();
				ordensEmExecucao.add(x);
			}
		}
		GeneralSemaphore.getSem4().release();
	}

	public static SelecionaOrdens getInstance(Fabrica fabrica) {
		if (instance == null)
			instance = new SelecionaOrdens(fabrica);
		return instance;
	}
	private boolean isSpeedMode(Ordens ordem) {
		List<String> aux = ordem.getReceita(0,0);
		if(aux.size()/3 < 3)
			return false;
		for(int i=0; i< aux.size(); i+=3) {
			String pre=aux.get(0);
			String preFerra= aux.get(1);
			if(pre.equals(aux.get(i))) {
				if(!preFerra.equals(aux.get(+1))) {
					return false;
				}
			}else
				return false;
		}
		
		return true;
	}
	private void executaOrdem(Ordens ordem) {
		OrdensThread x = new OrdensThread(ordem, controlaPlc, ordem.pendente());// inicio thread
		
		if(isSpeedMode(ordem)) {
			ordem.setSpeedMode(true);
		}
		x.setName("Thread " + ordem.getNumeroOrdem());
		x.start();
		x.setaExecutar(true);
		System.out.println("executou ordem : " + ordem.getNumeroOrdem());
		GereOrdensThread.incrementNumberOfThreads();
		ordensEmExecucao.add(x);
	}

	private String trocaOrdem(Ordens ordem) {
		List<String> lista = ordem.getReceita(0,0);
		/* Se lista tiver um D entao é uma descarga */
		if (lista.get(0).equals("D")) {
			return "";
		}

		for (int i = 0; i < lista.size(); i += 3) {
			String x = lista.get(i);
			if (x.equals("A")) {
				return GereOrdensThread.getmALivreSeleciona()[0];
			} else if (x.equals("B")) {
				return GereOrdensThread.getmBLivreSeleciona()[0];
			} else if (x.equals("C")) {
				return GereOrdensThread.getmCLivreSeleciona()[0];
			}
		}

		return "";
	}

	private void trocaOrdem(Ordens ordemAExecutar, String ordemATrocar) {
		for (int i = 0; i < ordensEmExecucao.size(); i++) {
			Ordens ordem = ordensEmExecucao.get(i).getOrdem();

			if (ordem.getNumeroOrdem().equals(ordemATrocar)) {
				if ((ordem.getPrioridade() - ordemAExecutar.getPrioridade()) < 50) {
					break;
				}
				ordensEmExecucao.get(i).setaExecutar(false);
				executaOrdem(ordemAExecutar);
				//System.out.println("*********troca ordem: " + ordemATrocar + " por ordem: " + ordem.getNumeroOrdem());

				break;
			}
		}
	}

	private void executaOrdensEspera() {
		System.out.println(ordensEmExecucao);
		for (int i = 0; i < ordensEmExecucao.size(); i++) {
			Ordens ordem = ordensEmExecucao.get(i).getOrdem();
			System.out.println(ordem.getNumeroOrdem() + " -> " + ordensEmExecucao.get(i).isaExecutar());
			if (ordem.getPecasPendentes() <= 0) {
				System.out.println("removeu " + ordensEmExecucao.get(i));
				ordensEmExecucao.remove(i);
			} else if (!ordensEmExecucao.get(i).isaExecutar() && chooseOrder(ordem)) {
				ordensEmExecucao.get(i).setaExecutar(true);
			}

		}
	}
}
