package fabrica;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.PriorityQueue;

import db.DataBase;
import opc.OpcClient;

public class Fabrica {
	private static Fabrica instance=null;
	private PriorityQueue<Ordens> heapOrdemPendente;
	private DataBase db;
	private Plant plant;
	private ControlaPlc controlaPlc;
	


	
	private Fabrica() {
		//this.db = DataBase.getInstance();
		this.plant = new Plant();
		this.controlaPlc = new ControlaPlc();
		//criaHeap();
		//sincronizaOrdens();
	}
	public static Fabrica getInstance() {
		if(instance == null)
			instance = new Fabrica();
		return instance;
	}
	public void addToHeap(Ordens ordens) {
		heapOrdemPendente.add(ordens);
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
				heapOrdemPendente.add(new Ordens(desc.getString("numeroOrdem"), -1));//ordem imediata
			}
			while(prod.next()) {
				heapOrdemPendente.add(new Ordens(prod.getString("numeroOrdem"), Integer.valueOf(prod.getString("atrasoMaximo"))));
			}
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		
		
        
	}
	
	public void imprimeHeap() {
		Comparator<Ordens> result = new Comparator<Ordens>() {
		      
			@Override
			public int compare(Ordens arg0, Ordens arg1) {
				Integer x = arg0.getPrioridade();
				Integer y = arg1.getPrioridade();
	            return x.compareTo(y);
			}
	    };

	    PriorityQueue<Ordens> aux = new PriorityQueue<>(result);
		
		int size= heapOrdemPendente.size();
		
		
		for(int i=0; i<size; i++) {
			aux.add(new Ordens(heapOrdemPendente.peek().getNumeroOrdem(), heapOrdemPendente.peek().getPrioridade()));
			System.out.println("numero Ordem: "+ heapOrdemPendente.peek().getNumeroOrdem() +"  Prioridade: "+heapOrdemPendente.poll().getPrioridade());
		}
		/*coloca na heap de novo*/
		for(int i=0; i<size; i++) {
			heapOrdemPendente.add(new Ordens(aux.peek().getNumeroOrdem(), aux.poll().getPrioridade()));
		}
	}
	public Plant getPlant() {
		return plant;
	}
	
}
