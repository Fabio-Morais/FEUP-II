package xml;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class XmlUnload {
	private NamedNodeMap node;
	private String type;
	private String destination;
	private String quantity;

	public XmlUnload(Element eElement) {
		this.node = eElement.getElementsByTagName("Unload").item(0).getAttributes();
		this.type=node.getNamedItem("Type").getNodeValue();
		this.destination=node.getNamedItem("Destination").getNodeValue();
		this.quantity=node.getNamedItem("Quantity").getNodeValue();
		debug();
	}
	public void debug() {
		System.out.println("Type: " + node.getNamedItem("Type").getNodeValue());
		System.out.println("Destination: " + node.getNamedItem("Destination").getNodeValue());
		System.out.println("Quantity : " + node.getNamedItem("Quantity").getNodeValue());
		System.out.println();
	}
}
