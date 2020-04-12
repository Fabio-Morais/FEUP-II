package fabrica;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.PriorityQueue;

import db.DataBase;
import db.Ordem;
import opc.OpcClient;

public class Fabrica {
	private static Fabrica instance=null;
	private PriorityQueue<Ordens> heapOrdemPendente;
	private DataBase db;
	private Plant plant;
	private ControlaPlc controlaPlc;
	private AtualizaOrdensEspera atualizaOrdensEspera;


	
	private Fabrica() {
		this.db = DataBase.getInstance();
		this.plant = new Plant();
		this.controlaPlc = new ControlaPlc();
		criaHeap();
		sincronizaOrdens();
	}
	
	public static Fabrica getInstance() {
		if(instance == null)
			instance = new Fabrica();
		return instance;
	}
	public void addToHeap(Ordens ordens) {
		heapOrdemPendente.add(ordens);
	}
	
	public void atualizaHeap() {
		this.atualizaOrdensEspera = new AtualizaOrdensEspera();
		atualizaOrdensEspera.start();
		
	}

	/**Cria a heap e o comparator para essa heap*/
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
	}
	
	public void sincronizaOrdens() {
		ResultSet prod = db.selectProducao();

		ResultSet desc = db.selectDescarga();

		try {
			while(desc.next()) {
				heapOrdemPendente.add(new Ordens(desc.getString("numeroOrdem"), -1, Ordem.converteData2(desc.getString("horaentradaordem")),0));//ordem imediata
			}

			while(prod.next()) {
				heapOrdemPendente.add(new Ordens(prod.getString("numeroOrdem"), Integer.valueOf(prod.getString("atrasoMaximo")), 
						Ordem.converteData2(prod.getString("horaentradaordem")),Integer.valueOf(prod.getString("atrasomaximo")) ));
				/*int atraso = Integer.valueOf(prod.getString("atrasomaximo"));
				String date= Ordem.converteData2(prod.getString("horaentradaordem"));
				heapOrdemPendente.add(new Ordens(prod.getString("numeroOrdem"), Ordem.calculaTempoRestante(date, atraso), 
						date,atraso ));*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		heapOrdemPendente.add(new Ordens("111111111", 100, Ordem.localDate(), 120));
		heapOrdemPendente.add(new Ordens("32123", 50, Ordem.localDate(), 50));

        
	}
	
	
	/**Retorna uma nova heap, que é uma copia da original*/
	public PriorityQueue<Ordens> getCopyHeapOrdemPendente() {
		PriorityQueue<Ordens> copy = new PriorityQueue<>(heapOrdemPendente);
		return copy;
	}
	
	/**Retorna a heap original*/
	public PriorityQueue<Ordens> getHeapOrdemPendente() {
		return heapOrdemPendente;
	}
	
	public void setHeapOrdemPendente(PriorityQueue<Ordens> heapOrdemPendente) {
		this.heapOrdemPendente = heapOrdemPendente;
	}
	public void imprimeHeap() {
		
		PriorityQueue<Ordens> aux = getCopyHeapOrdemPendente();
		
		int size= aux.size();
		
		for(int i=0; i<size; i++) {
			System.out.println("numero Ordem: "+ aux.peek().getNumeroOrdem() +"  Prioridade: "+aux.poll().getPrioridade());
		}

	}
	public Plant getPlant() {
		return plant;
	}
	
}
