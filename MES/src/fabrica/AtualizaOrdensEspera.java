package fabrica;

import java.util.Comparator;
import java.util.PriorityQueue;

import db.Ordem;

public class AtualizaOrdensEspera extends Thread {
	Fabrica fabrica;
	public AtualizaOrdensEspera() {
		this.fabrica= Fabrica.getInstance();

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
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
