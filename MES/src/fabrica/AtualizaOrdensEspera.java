 package fabrica;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

import db.Ordem;

public class AtualizaOrdensEspera extends Thread {
	Fabrica fabrica;
	Semaphore sem;
	public AtualizaOrdensEspera() {
		this.fabrica= Fabrica.getInstance();
		this.sem = GeneralSemaphore.getSem();
	}

	/**Atualiza a Heap com o tempo que falta, ((Tempo entrada de ordem + atraso maximo) - hora atual)
	 * vai atualizando a sua prioridade ao longo do tempo, pois o tempo vai passando*/
	public void run() {
		Comparator<Ordens> result = new Comparator<Ordens>() {
		      
			@Override
			public int compare(Ordens arg0, Ordens arg1) {
				Integer x = arg0.getPrioridade();
				Integer y = arg1.getPrioridade();
	            return x.compareTo(y);
			}
	    };
	    
		while(true) {
			try {
				sem.acquire();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			PriorityQueue<Ordens> heap = fabrica.getHeapOrdemPendente();
			PriorityQueue<Ordens> aux =  new PriorityQueue<>(result);
			int size= heap.size();
			
			for(int i=0; i<size; i++) {
				Ordens x = heap.poll();
				int prioridade =  (int) Ordem.calculaTempoRestante(x.getDataInicio(), x.getAtrasoMaximo());
				x.setPrioridade(prioridade);
				aux.add(x);
			}
			
			fabrica.setHeapOrdemPendente(aux);
			sem.release();

			HashMap<String, Ordens> aux2 = fabrica.getCopyHeapOrdemExecucao();
			for (Map.Entry<String, Ordens> entry : aux2.entrySet()) {
				Ordens ordem = entry.getValue();
				int prioridade =  (int) Ordem.calculaTempoRestante(ordem.getDataInicio(), ordem.getAtrasoMaximo());
				ordem.setPrioridade(prioridade);
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			
			
		}
		
	}
}
