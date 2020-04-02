package udp.ordem;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import db.DataBase;
import db.Descarga;
import db.Ordem;
import db.Producao;
import fabrica.Fabrica;
import fabrica.Ordens;

public class Unload {
	private NamedNodeMap node;
	private String numeroOrdem;
	private String type;
	private String destination;
	private String quantity;
	private DataBase db;
	private Fabrica fabrica = Fabrica.getInstance();

	public Unload(Element eElement) {
		this.db = DataBase.getInstance();
		
		this.numeroOrdem = eElement.getAttribute("Number");
		this.node = eElement.getElementsByTagName("Unload").item(0).getAttributes();
		this.type=node.getNamedItem("Type").getNodeValue();
		this.destination=node.getNamedItem("Destination").getNodeValue();
		this.quantity=node.getNamedItem("Quantity").getNodeValue();
		//debug();
		insereDb();
	}
	
	public void insereDb() {
		/*so adiciona na heap caso adicione na DB com exito*/
		if(db.insereDescarga(new Descarga(numeroOrdem, type, destination, Integer.valueOf(quantity))))
			fabrica.addToHeap(new Ordens(numeroOrdem, -1, Ordem.localDate(), 0));

	}
	
	public void debug() {
		System.out.println("Type: " + node.getNamedItem("Type").getNodeValue());
		System.out.println("Destination: " + node.getNamedItem("Destination").getNodeValue());
		System.out.println("Quantity : " + node.getNamedItem("Quantity").getNodeValue());
		System.out.println();
	}
}
