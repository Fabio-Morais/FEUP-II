package udp.ordem;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Ordem {
	private NodeList nList;
	private Element eElement;

	public Ordem(NodeList nList) {
		this.nList = nList;
	}

	public void criaOrdem() {
		/* Isto serve para as orden */
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				this.eElement = (Element) nNode;
				//eElement.getAttribute("Number") //numero da ordem
				
				
				if (eElement.getElementsByTagName("Transform").getLength() > 0) {
					new Transform(eElement);

				} else if (eElement.getElementsByTagName("Unload").getLength() > 0) {
					new Unload(eElement);
				}
			}
		}
	}
}
