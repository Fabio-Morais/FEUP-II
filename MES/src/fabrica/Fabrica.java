package fabrica;

import java.sql.ResultSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

import db.DataBase;
import db.Ordem;

public class Fabrica {
	private static Fabrica instance = null;
	private PriorityQueue<Ordens> heapOrdemPendente;
	private HashMap<String, Ordens> heapOrdemExecucao;
	private DataBase db;
	private Plant plant;
	private ControlaPlc controlaPlc;
	private AtualizaOrdensEspera atualizaOrdensEspera;
	private Semaphore sem;

	private Fabrica() {
		 this.db = DataBase.getInstance();
		// this.plant = new Plant();
		// this.controlaPlc = new ControlaPlc();
		criaHeap();
		sem = HeapSemaphore.getSem();
		 sincronizaOrdens();
	}

	/** Inicializa a classe SINGLETON */
	public static Fabrica getInstance() {
		if (instance == null)
			instance = new Fabrica();
		return instance;
	}

	/** Adiciona ordens à heap pendente */
	public void addToHeap(Ordens ordens) {
		if(!heapOrdemPendente.contains(ordens))
			heapOrdemPendente.add(ordens);
	}

	public void atualizaHeap() {

		this.atualizaOrdensEspera = new AtualizaOrdensEspera();
		atualizaOrdensEspera.start();

	}

	/** Cria as heaps e o comparator para a heap das ordens pendentes */
	private void criaHeap() {
		Comparator<Ordens> result = new Comparator<Ordens>() {

			@Override
			public int compare(Ordens arg0, Ordens arg1) {
				Integer x = arg0.getPrioridade();
				Integer y = arg1.getPrioridade();
				return x.compareTo(y);
			}
		};

		this.heapOrdemPendente = new PriorityQueue<>(result);
		this.heapOrdemExecucao = new HashMap<>();

	}

	/**
	 * executa uma ordem
	 * 
	 * @param ordem - Ordem a executar
	 */
	public void executaOrdem(Ordens ordem) {
		try {
			sem.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		heapOrdemExecucao.put(ordem.getNumeroOrdem(), ordem);
		sem.release();
		try {
			db.executaOrdemProducao(ordem.getNumeroOrdem());
		} catch (Exception e) {

		}
	}

	public void terminaOrdem(String numeroOrdem) {
		if(db.terminaOrdemProducao(numeroOrdem)) {
			try {
				sem.acquire();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			heapOrdemExecucao.remove(numeroOrdem);
			sem.release();

		}
	}
	/** Coloca todas as ordens nas respetivas heaps */
 	public void sincronizaOrdens() {
		ResultSet prod = db.selectProducao();
		ResultSet desc = db.selectDescarga();

		try {
			while (desc.next()) {
				if (desc.getString("estadoOrdem").equals("0")) {
					heapOrdemPendente.add(new Ordens(desc.getString("numeroOrdem"), 0,
							Ordem.converteData2(desc.getString("horaentradaordem")), -1));// ordem imediata
				}else if(desc.getString("estadoOrdem").equals("1")) {
					heapOrdemExecucao.put(desc.getString("numeroOrdem"),new Ordens(desc.getString("numeroOrdem"), -1,
							Ordem.converteData2(desc.getString("horaentradaordem")), -1));// ordem imediata
				}
			}
			while (prod.next()) {

				if (prod.getString("estadoOrdem").equals("0")) {
					heapOrdemPendente
							.add(new Ordens(prod.getString("numeroOrdem"), Integer.valueOf(prod.getString("atrasoMaximo")),
									Ordem.converteData2(prod.getString("horaentradaordem")),
									Integer.valueOf(prod.getString("atrasomaximo"))));
				
				}else if(prod.getString("estadoOrdem").equals("1")) {
					heapOrdemExecucao
					.put(prod.getString("numeroOrdem"),new Ordens(prod.getString("numeroOrdem"), Integer.valueOf(prod.getString("atrasoMaximo")),
							Ordem.converteData2(prod.getString("horaentradaordem")),
							Integer.valueOf(prod.getString("atrasomaximo"))));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * FALTA METER AS ORDENS EM EXECUÇAO
		 */

	}

	/** Retorna uma nova heap, que é uma copia da original */

	/** Retorna uma nova heap, que é uma copia da original */
	public PriorityQueue<Ordens> getCopyHeapOrdemPendente() {
		PriorityQueue<Ordens> copy = new PriorityQueue<>(heapOrdemPendente);
		return copy;
	}

	/** Retorna a heap original */
	public PriorityQueue<Ordens> getHeapOrdemPendente() {
		return heapOrdemPendente;

	}

	/** Retorna uma nova heap, que é uma copia da original */
	public HashMap<String, Ordens> getCopyHeapOrdemExecucao() {
		HashMap<String, Ordens> copy = new HashMap<>(heapOrdemExecucao);
		return copy;
	}

	/** Retorna a heap original */
	public HashMap<String, Ordens> getHeapOrdemExecucao() {
		return heapOrdemExecucao;
	}

	/**
	 * Atualiza a heap das ordens pendentes
	 * 
	 * @param heapOrdemPendente - heap que vai substituir a original
	 */
	public void setHeapOrdemPendente(PriorityQueue<Ordens> heapOrdemPendente) {
		this.heapOrdemPendente = heapOrdemPendente;
	}

	/** Imprime todas as heaps */
	public void imprimeHeap() {

		PriorityQueue<Ordens> aux = getCopyHeapOrdemPendente();

		int size = aux.size();
		System.out.println("\n\nORDENS PENDENTES");
		for (int i = 0; i < size; i++) {
			System.out.println(
					"numero Ordem: " + aux.peek().getNumeroOrdem() + "  Prioridade: " + aux.poll().getPrioridade());
		}
		System.out.println("----------------------------");
		HashMap<String, Ordens> aux2 = getHeapOrdemExecucao();
		System.out.println("ORDENS EM EXECUÇAO");
		for (Map.Entry<String, Ordens> entry : aux2.entrySet()) {
			String key = entry.getKey();
			Ordens value = entry.getValue();
			System.out
					.println("numero Ordem: " + value.getNumeroOrdem() + "  Prioridade: " + aux.poll().getPrioridade());

		}

	}

	/** retorna a classe Plant */
	public Plant getPlant() {
		return plant;
	}

	/** retorna a classe ControlaPlc */
	public ControlaPlc getControlaPlc() {
		return controlaPlc;
	}

}
