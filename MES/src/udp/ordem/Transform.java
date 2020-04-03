package udp.ordem;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import db.DataBase;
import db.Ordem;
import db.Producao;
import fabrica.Fabrica;
import fabrica.Ordens;

public class Transform {
	private NamedNodeMap node;
	private String numeroOrdem;
	private String pecaOrigem;
	private String pecaFinal;
	private String quantidadeProduzir;
	private String atrasoMaximo;
	private DataBase db;
	private Fabrica fabrica = Fabrica.getInstance();
	
	public Transform(Element eElement) {
		this.node = eElement.getElementsByTagName("Transform").item(0).getAttributes();
		this.numeroOrdem = eElement.getAttribute("Number");
		this.pecaOrigem=node.getNamedItem("From").getNodeValue();
		this.pecaFinal=node.getNamedItem("To").getNodeValue();
		this.quantidadeProduzir=node.getNamedItem("Quantity").getNodeValue();
		this.atrasoMaximo=node.getNamedItem("MaxDelay").getNodeValue();
		this.db = DataBase.getInstance();
		//debug();
		insereDb();
	}
	public void insereDb() {
		System.out.println(numeroOrdem);
		/*so adiciona na heap caso adicione na DB com exito*/
		if(db.insereProducao(new Producao(numeroOrdem, pecaOrigem, pecaFinal, Integer.valueOf(quantidadeProduzir), Integer.valueOf(atrasoMaximo))))
			fabrica.addToHeap(new Ordens(numeroOrdem, Integer.valueOf(atrasoMaximo),  Ordem.localDate(), Integer.valueOf(atrasoMaximo)));
	}
	
	public void debug() {
		System.out.println("From : " + node.getNamedItem("From").getNodeValue());
		System.out.println("To : " + node.getNamedItem("To").getNodeValue());
		System.out.println("Quantity : " + node.getNamedItem("Quantity").getNodeValue());
		System.out.println("MaxDelay : " + node.getNamedItem("MaxDelay").getNodeValue());
		System.out.println();
	}
}
