package fabrica;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.PriorityQueue;

import db.DataBase;
import opc.OpcClient;

public class Fabrica {
	private static Fabrica instance=null;
	private final int sizeOfPath=31;
	private PriorityQueue<Ordens> heapOrdemPendente;
	private DataBase db;
	private Plant plant;
	
	private Fabrica() {
		//this.db = DataBase.getInstance();
		this.plant = new Plant();
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
	public void sendPath() {

		OpcClient opcClient = OpcClient.getInstance();
		short[] x = new short[sizeOfPath];
		x[0]=1;
		x[1]=2;
		x[2]=2;
		x[3]=2;
		x[4]=1;
		x[5]=2;
		x[6]=2;
		x[7]=2;
		x[8]=2;
		x[9]=2;
		x[10]=1;	
		
		short[] y = new short[sizeOfPath];
		y[0]=1;
		y[1]=1;
		y[2]=2;
		y[3]=3;
		y[4]=3;
		y[5]=3;
		y[6]=4;
		y[7]=5;
		y[8]=6;
		y[9]=7;
		y[10]=7;

		opcClient.setValue("Fabrica", "pecateste.pathX", x);
		opcClient.setValue("Fabrica", "pecateste.pathY", y);
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
