package xml;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class XmlTransform {
	private NamedNodeMap node;
	private String from;
	private String to;
	private String quantity;
	private String maxDelay;
	
	public XmlTransform(Element eElement) {
		this.node = eElement.getElementsByTagName("Transform").item(0).getAttributes();
		this.from=node.getNamedItem("From").getNodeValue();
		this.to=node.getNamedItem("To").getNodeValue();
		this.quantity=node.getNamedItem("Quantity").getNodeValue();
		this.maxDelay=node.getNamedItem("MaxDelay").getNodeValue();
		debug();

	}
	public void debug() {
		System.out.println("From : " + node.getNamedItem("From").getNodeValue());
		System.out.println("To : " + node.getNamedItem("To").getNodeValue());
		System.out.println("Quantity : " + node.getNamedItem("Quantity").getNodeValue());
		System.out.println("MaxDelay : " + node.getNamedItem("MaxDelay").getNodeValue());
		System.out.println();
	}
}
