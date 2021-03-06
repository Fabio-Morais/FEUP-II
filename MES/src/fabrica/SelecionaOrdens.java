package fabrica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class SelecionaOrdens extends Thread {
	private static SelecionaOrdens instance = null;
	private Fabrica fabrica;
	private ArrayList<OrdensThread> ordensEmExecucao;
	private ControlaPlc controlaPlc;

	private SelecionaOrdens(Fabrica fabrica) {
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
				auxPre = aux;
			}

			PriorityQueue<Ordens> heapOrdemPendente = fabrica.getCopyHeapOrdemPendente();
			if (GereOrdensThread.isVoltaInicio()) {
				GereOrdensThread.setVoltaInicio(false);
				executaOrdensEspera();

			}
			maquinasOcupadas();
			// Nao permite mais de 5 ordens ao mesmo temo
			if (!heapOrdemPendente.isEmpty() && GereOrdensThread.getNumberOfThreads() < 8) {
				while (!heapOrdemPendente.isEmpty()) {
					maquinasOcupadas();
					Ordens ordem = heapOrdemPendente.poll();
					List<String> ok = null;
					String troca = "";

					if (GereOrdensThread.isVoltaInicio()) {
						GereOrdensThread.setVoltaInicio(false);
						executaOrdensEspera();
						break;
					} else {
						ok = chooseOrder(ordem);
						if (ok.isEmpty())
							troca = trocaOrdem(ordem);
					}
					/** Se tiver prioridade parecida entao mete em paralelo */
					if (ok != null && !ok.isEmpty()) {
						executaOrdem(ordem, ok);
						break;
					} else if (!troca.equals("")) {
						trocaOrdem(ordem, troca);
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void maquinasOcupadas() {
		boolean bool = true;
	
		for (int i = 0; i < ordensEmExecucao.size(); i++) {
			String x = ordensEmExecucao.get(i).getOrdem().getReceita((int) 0, 0).get(0);
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

	}

	private boolean speed(Ordens ordem, boolean ok, List<String> select) {

		boolean returnValue = ok;
		List<String> lista = ordem.getReceita(11, 0);
		List<String> lista2 = ordem.getReceita(15, 15);

		returnValue |= valida(ordem, lista, select);
		returnValue |= valida(ordem, lista2, select);

		return returnValue;
	}

	/**
	 * Se a ordem que chega quiser ir para a maquina X e esta tiver livre entao
	 * retorna OK (executa ordem) Se ordem tiver que ir a 2 maquinas e so uma delas
	 * estiver livre, entao nao executa
	 * 
	 * @param lista - se lista tiver no indice 0 o D � descarga, caso contrario �
	 *              carga
	 */
	private List<String> chooseOrder(Ordens ordem) {
		boolean ok = false;
		List<String> lista = ordem.getReceita(0, 0);
		List<String> lista2 = ordem.getReceita(11, 0);
		if (isSpeedMode(ordem)) {
			ordem.setSpeedMode(true);
			GereOrdensThread.setP1P9(true);
		}
		/* Se lista tiver um D entao � uma descarga */
		if (lista.get(0).equals("D")) {
			List<String> x = new ArrayList<>();
			x.add("D");
			return x;
		}
		List<String> select = new ArrayList<>();
		ok = valida(ordem, lista, select);
		/**
		 * Se for maior que 3 entao pode usar mais que uma maquina, ex: (usar todas da C
		 * e usar a A)
		 */
		if (!lista.equals(lista2)) {
			ok = speed(ordem, ok, select);
		}

		if (ok)
			selectList(select, ordem.getNumeroOrdem());

		return select;
	}

	private boolean valida(Ordens ordem, List<String> lista2, List<String> select) {
		boolean ok = false;
		if (ordem.isSpeedMode()) {
			for (int i = 0; i < lista2.size(); i += 3) {
				String x = lista2.get(i);
				if (x.equals("A") && (GereOrdensThread.mALivreSeleciona()) && GereOrdensThread.getmALivre()[0]) {
					ok = true;
					select.add("A");
				} else if (x.equals("B") && (GereOrdensThread.mBLivreSeleciona()) && GereOrdensThread.getmBLivre()[0]) {
					ok = true;
					select.add("B");
				} else if (x.equals("C") && (GereOrdensThread.mCLivreSeleciona()) && GereOrdensThread.getmCLivre()[0]) {
					ok = true;
					select.add("C");
				} else {
					ok = false;
				}
			}
		} else {
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

		}

		/*
		 * Caso especial, em que vai so para 1 maquina e est� na fabrica 1 ordem
		 * ESPECIAL a correr em 3 maquinas
		 */
		if (lista2.size() < 4) {
			int count = 1;
			String pre = GereOrdensThread.getmALivreSeleciona()[0];
			String mB = GereOrdensThread.getmBLivreSeleciona()[0];
			String mC = GereOrdensThread.getmCLivreSeleciona()[0];
			if (pre.equals(mB) && mB.length() > 1 && mB.charAt(0) == 'S') {
				pre = GereOrdensThread.getmBLivreSeleciona()[0];
				count++;
			}
			if (pre.equals(mC) && mC.length() > 1 && mC.charAt(0) == 'S') {
				count++;

			}
			/* METER NA MAQUINA QUE TEM O S */
			if (count > 1) {
				if (lista2.get(0).equals("A") && GereOrdensThread.getmALivreSeleciona()[0].length() > 1
						&& (GereOrdensThread.getmALivreSeleciona()[0].charAt(0) == 'S')) {
					ok = true;
					select.add("A");
					String numero = GereOrdensThread.getmALivreSeleciona()[0];
					for (int i = 0; i < ordensEmExecucao.size(); i++) {
						OrdensThread ordemExe = ordensEmExecucao.get(i);
						if (ordemExe.getOrdem().getNumeroOrdem().equals(numero.substring(1, numero.length()))) {
							ordemExe.removeMaquinaAUsar("A");
						}
					}
				} else if (lista2.get(0).equals("B") && GereOrdensThread.getmBLivreSeleciona()[0].length() > 1
						&& (GereOrdensThread.getmBLivreSeleciona()[0].charAt(0) == 'S')) {
					ok = true;
					select.add("B");
					String numero = GereOrdensThread.getmALivreSeleciona()[0];
					for (int i = 0; i < ordensEmExecucao.size(); i++) {
						OrdensThread ordemExe = ordensEmExecucao.get(i);
						if (ordemExe.getOrdem().getNumeroOrdem().equals(numero.substring(1, numero.length()))) {
							ordemExe.removeMaquinaAUsar("B");
						}
					}
				} else if (lista2.get(0).equals("C") && GereOrdensThread.getmCLivreSeleciona()[0].length() > 1
						&& (GereOrdensThread.getmCLivreSeleciona()[0].charAt(0) == 'S')) {
					ok = true;
					select.add("C");
					String numero = GereOrdensThread.getmALivreSeleciona()[0];
					for (int i = 0; i < ordensEmExecucao.size(); i++) {
						OrdensThread ordemExe = ordensEmExecucao.get(i);
						if (ordemExe.getOrdem().getNumeroOrdem().equals(numero.substring(1, numero.length()))) {
							ordemExe.removeMaquinaAUsar("C");
						}
					}
				}
			}

		}
		return ok;
	}

	private void selectList(List<String> select, String numeroOrdem) {
		for (String maquina : select) {
			if (maquina.equals("A")) {
				for (int i = 0; i < 3; i++) {
					GereOrdensThread.setmALivreSeleciona("X" + numeroOrdem, i);
				}
			} else if (maquina.equals("B")) {
				for (int i = 0; i < 3; i++) {
					GereOrdensThread.setmBLivreSeleciona("X" + numeroOrdem, i);
				}
			} else if (maquina.equals("C")) {
				for (int i = 0; i < 3; i++) {
					GereOrdensThread.setmCLivreSeleciona("X" + numeroOrdem, i);
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
			if (chooseOrder(value).size() > 0) {
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
		List<String> aux = ordem.getReceita(0, 0);
		if (aux.size() / 3 < 3)
			return false;
		for (int i = 0; i < aux.size(); i += 3) {
			String pre = aux.get(0);
			String preFerra = aux.get(1);
			if (pre.equals(aux.get(i))) {
				if (!preFerra.equals(aux.get(+1))) {
					return false;
				}
			} else
				return false;
		}

		return true;
	}

	private void executaOrdem(Ordens ordem, List<String> ok) {
		OrdensThread x = new OrdensThread(ordem, controlaPlc, ordem.pendente());// inicio thread

		x.setName("Thread " + ordem.getNumeroOrdem());
		x.start();
		x.setaExecutar(true);

		for (String maquinasAUsar : ok) {
			x.setMaquinaAUsar(maquinasAUsar);
		}

		GereOrdensThread.incrementNumberOfThreads();
		ordensEmExecucao.add(x);
	}

	private String trocaOrdem(Ordens ordem) {
		List<String> lista = ordem.getReceita(0, 0);
		/* Se lista tiver um D entao � uma descarga */
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
			/* vai � heap e tira a ordem que vamos trocar */
			if (ordem.getNumeroOrdem().equals(ordemATrocar.substring(1, ordemATrocar.length()))) {
				if (((ordem.getPrioridade() - ordemAExecutar.getPrioridade()) < 50) || ordemAExecutar.isSpeedMode()) {
					break;
				}
				System.out.println("trocou ->"+ordemATrocar +" por: "+ordemAExecutar);
				if (ordensEmExecucao.get(i).getOrdem().isSpeedMode()) {
					ordensEmExecucao.get(i).removeMaquinaAUsar(ordemAExecutar.getReceita(0, 0).get(0));
				} else {
					ordensEmExecucao.get(i).setaExecutar(false);
					List<String> auxiliar = ordensEmExecucao.get(i).getOrdem().getReceita(0, 0);
					for(int z = 0; z < auxiliar.size(); z+=3) {
						String maq = ordensEmExecucao.get(i).getOrdem().getReceita(0, 0).get(z);
						if(maq.equals("A")) {
							GereOrdensThread.setmALivreSeleciona("", 0);
						}else if(maq.equals("B")) {
							GereOrdensThread.setmBLivreSeleciona("", 0);

						}else if(maq.equals("B")) {
							GereOrdensThread.setmCLivreSeleciona("", 0);

						}
					}
						
				}
				List<String> ok = new ArrayList<>();
				ok.add("X");
				executaOrdem(ordemAExecutar, ok);

				break;
			}
		}
	}

	private void executaOrdensEspera() {
		for (int i = 0; i < ordensEmExecucao.size(); i++) {
			Ordens ordem = ordensEmExecucao.get(i).getOrdem();
			if (ordem.getPecasPendentes() <= 0) {
				ordensEmExecucao.remove(i);
			} else if (!ordensEmExecucao.get(i).isaExecutar() && chooseOrder(ordem).size() > 0) {
				ordensEmExecucao.get(i).setaExecutar(true);
			}

		}
	}
}
