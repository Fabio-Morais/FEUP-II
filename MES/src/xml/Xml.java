package xml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.io.File;
import java.io.StringReader;

public class Xml {
	
	
	public void read() {
		try {
			String xmlString="<ORDERS>\r\n" + 
					"<Order Number=\"002\">\r\n" + 
					"<Transform From=\"P3\" To=\"P5\" Quantity=\"7\" MaxDelay=\"300\"/>\r\n" + 
					"</Order>\r\n" + 
					"<Order Number=\"003\">\r\n" + 
					"<Transform From=\"P7\" To=\"P9\" Quantity=\"10\" MaxDelay=\"300\"/>\r\n" + 
					"</Order>\r\n" + 
					"</ORDERS>\r\n";
			File fXmlFile = new File("command2.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			//Document doc = dBuilder.parse(fXmlFile);
			Document doc = dBuilder.parse(new InputSource(new StringReader(xmlString)));
			
			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("Order");

			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				System.out.println("\nElement :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					//ORDER
					System.out.println("number: " + eElement.getAttribute("Number"));
					//TRANSFORM
					NamedNodeMap node= eElement.getElementsByTagName("Transform").item(0).getAttributes();// vai buscar os atributos de TRANSFORM
					
					System.out.println("From : " + node.getNamedItem("From").getNodeValue());
					System.out.println("To : " + node.getNamedItem("To").getNodeValue());
					System.out.println("Quantity : " + node.getNamedItem("Quantity").getNodeValue());
					System.out.println("MaxDelay : " + node.getNamedItem("MaxDelay").getNodeValue());


				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Cria XML relativamente à existencia de peças no armazem
	 */
	public void existenciaPeca() {
		String xml = "<Current_Stores>\r\n";
		String px = "P0";
		int quant= 0;
		
		/*Precisa de ir à DB ver as peças que tem*/
		for(int i=0; i<9; i++) {
			px=px.substring(0, 1)+""+(i+1);
			xml += "<WorkPiece type=”" + px + "” quantity=”"+(quant++)*2+"”/>\r\n";
		}
		xml += "</Current_Stores>";
		System.out.println(xml);
	}



}
