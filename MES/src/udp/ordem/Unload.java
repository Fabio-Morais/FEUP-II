package udp.ordem;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import db.DataBase;
import db.Descarga;
import db.Producao;

public class Unload {
	private NamedNodeMap node;
	private String numeroOrdem;
	private String type;
	private String destination;
	private String quantity;
	private DataBase db;

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
		db.insereDescarga(new Descarga(numeroOrdem, type, destination, Integer.valueOf(quantity)));
	}
	
	public void debug() {
		System.out.println("Type: " + node.getNamedItem("Type").getNodeValue());
		System.out.println("Destination: " + node.getNamedItem("Destination").getNodeValue());
		System.out.println("Quantity : " + node.getNamedItem("Quantity").getNodeValue());
		System.out.println();
	}
}
