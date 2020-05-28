package udp.ordem;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import db.DataBase;
import db.Descarga;
import db.Ordem;
import fabrica.Fabrica;
import fabrica.Ordens;

public class Unload {
	private NamedNodeMap node;
	private String numeroOrdem;
	private int numeroOrdemInt;
	private String type;
	private String destination;
	private String quantity;
	private DataBase db;
	private Fabrica fabrica = Fabrica.getInstance();

	public Unload(Element eElement) {
		this.db = DataBase.getInstance();
		
		this.numeroOrdem = eElement.getAttribute("Number");
		this.numeroOrdemInt = Integer.valueOf(numeroOrdem);
		this.node = eElement.getElementsByTagName("Unload").item(0).getAttributes();
		this.type=node.getNamedItem("Type").getNodeValue();
		this.destination=""+node.getNamedItem("Destination").getNodeValue().charAt(1);
		this.destination = "PM"+this.destination;
		this.quantity=node.getNamedItem("Quantity").getNodeValue();
		//debug();
		insereDb();
	}
	
	public void insereDb() {
		/*so adiciona na heap caso adicione na DB com exito*/
		Ordens ordem = new Ordens(""+numeroOrdemInt, 0, Ordem.localDate(), -1, fabrica);
		ordem.setPecasPendentes(Integer.valueOf(quantity));
		ordem.setUnload(ordem.new Unload(this.type,  this.destination));
		System.out.println(ordem);
		fabrica.addToHeap(ordem);

	}
	
	public void debug() {
		System.out.println("Type: " + node.getNamedItem("Type").getNodeValue());
		System.out.println("Destination: " + node.getNamedItem("Destination").getNodeValue());
		System.out.println("Quantity : " + node.getNamedItem("Quantity").getNodeValue());
		System.out.println();
	}
}
