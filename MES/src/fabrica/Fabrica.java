package fabrica;

import java.sql.ResultSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

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

	private Fabrica() {
		this.db = DataBase.getInstance();
		// this.plant = new Plant();
		// this.controlaPlc = new ControlaPlc();
		criaHeap();
		sincronizaOrdens();
	}

	/** Inicializa a classe SINGLETON */
	public static Fabrica getInstance() {
		if (instance == null)
			instance = new Fabrica();
		return instance;
	}

	/** Adiciona ordens � heap pendente */
	public void addToHeap(Ordens ordens) {
		if (!heapOrdemPendente.contains(ordens))
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

	/** Coloca todas as ordens nas respetivas heaps */
	public void sincronizaOrdens() {
		ResultSet prod = db.selectProducao();
		ResultSet desc = db.selectDescarga();

		try {
			while (desc.next()) {
				Ordens ordem = new Ordens(desc.getString("numeroOrdem"), 0,
						Ordem.converteData2(desc.getString("horaentradaordem")), -1, this);
				ordem.setPecasPendentes(Integer.valueOf(desc.getString("pecaspendentes")));
				ordem.setPecasEmProducao(Integer.valueOf(desc.getString("pecasproducao")));
				ordem.setPecasProduzidas(Integer.valueOf(desc.getString("pecasproduzidas")));
				ordem.setPrioridade(Integer.valueOf(desc.getString("folgaexecucao")));
				ordem.setUnload(ordem.new Unload(desc.getString("pecaDescarga"), desc.getString("destino")));
				if (desc.getString("estadoOrdem").equals("0")) {
					heapOrdemPendente.add(ordem);// ordem imediata
				} else if (desc.getString("estadoOrdem").equals("1")) {
					heapOrdemExecucao.put(ordem.getNumeroOrdem(),ordem);// ordem imediata
				}
			}
			while (prod.next()) {
				Ordens ordem = new Ordens(prod.getString("numeroOrdem"),
						Integer.valueOf(prod.getString("atrasoMaximo")),
						Ordem.converteData2(prod.getString("horaentradaordem")),
						Integer.valueOf(prod.getString("atrasomaximo")), this);
				ordem.setPecasPendentes(Integer.valueOf(prod.getString("pecaspendentes")));
				ordem.setPecasEmProducao(Integer.valueOf(prod.getString("pecasproducao")));
				ordem.setPecasProduzidas(Integer.valueOf(prod.getString("pecasproduzidas")));
				ordem.setPrioridade(ordem.calculaPrioridade());
				ordem.setTransform(ordem.new Transform(prod.getString("pecaorigem"),prod.getString("pecafinal")));
				if (prod.getString("estadoOrdem").equals("0")) {
					heapOrdemPendente.add(ordem);
				} else if (prod.getString("estadoOrdem").equals("1")) {
					heapOrdemExecucao.put(prod.getString("numeroOrdem"), ordem);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * FALTA METER AS ORDENS EM EXECU�AO
		 */

	}
	

	/** Retorna uma nova heap, que � uma copia da original */
	public PriorityQueue<Ordens> getCopyHeapOrdemPendente() {
		PriorityQueue<Ordens> copy = new PriorityQueue<>(heapOrdemPendente);
		return copy;
	}

	/** Retorna a heap original */
	public PriorityQueue<Ordens> getHeapOrdemPendente() {
		return heapOrdemPendente;

	}

	/** Retorna uma nova heap, que � uma copia da original */
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
			System.out.println(aux.poll());
		}
		System.out.println("----------------------------");
		HashMap<String, Ordens> aux2 = getCopyHeapOrdemExecucao();
		System.out.println("ORDENS EM EXECU�AO");
		for (Map.Entry<String, Ordens> entry : aux2.entrySet()) {
			String key = entry.getKey();
			Ordens value = entry.getValue();
			System.out.println(value);

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
