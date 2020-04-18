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
		this.sem = HeapSemaphore.getSem();
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
			PriorityQueue<Ordens> heap = fabrica.getHeapOrdemPendente();
			PriorityQueue<Ordens> aux =  new PriorityQueue<>(result);
			int size= heap.size();
			try {
				sem.acquire();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			for(int i=0; i<size; i++) {
				Ordens x = heap.poll();
				String numeroOrdem = x.getNumeroOrdem();
				String date = x.getDataInicio();
				int prioridade =  (int) Ordem.calculaTempoRestante(x.getDataInicio(), x.getAtrasoMaximo());
				int atrasoMax = x.getAtrasoMaximo();
				
				aux.add(new Ordens(numeroOrdem,(atrasoMax== 0 ? -1 : prioridade), 
						date, atrasoMax));
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
