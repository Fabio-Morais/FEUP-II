package udp;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fabrica.Fabrica;
import opc.OpcClient;
import udp.estatistica.Estatistica;
import udp.ordem.Ordem;

public class Message {
	private String address;

	private Fabrica fabrica = Fabrica.getInstance();
	public Message(String address) throws Exception {
		super();
		if(address ==null || address.isEmpty())
			throw new Exception();
		
		this.address=address;
	}

	/**Lê o xml
	 * @param message -  Mensagem a ser lida
	 * @return true - Se leu um formato XML correto<br> false - caso contrario
	 * */
	protected boolean read(String message) {
		String xmlString = message;

		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(xmlString)));
			doc.getDocumentElement().normalize();
			
			/*ORDENS*/
			NodeList nList = doc.getElementsByTagName("Order");
			Ordem ordem = new Ordem(nList);
			if(nList.getLength() > 0) {
				Node nNode = nList.item(0);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					ordem.criaOrdem();
				}
			}
			
			/*REQUEST*/
			nList = doc.getElementsByTagName("Request_Stores");
			if (nList.getLength() > 0) {
				Node nNode = nList.item(0);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					new RequestStore(address);
				}
			}
			
			/*ESTATISTICA*/
			nList = doc.getElementsByTagName("Request_Stats");
			if (nList.getLength() > 0) {
				Node nNode = nList.item(0);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					new Estatistica();
				}
			}
			
			/*Peças na fabrica*/
			nList = doc.getElementsByTagName("Request_Pieces");
			if (nList.getLength() > 0) {
				Node nNode = nList.item(0);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					OpcClient opc = OpcClient.getInstance();
					short pecasSistema = opc.getValue("SFS","PecasSistema")[0];
					System.out.println(pecasSistema);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}


		return true;

	}

	/**
	 * Para efeitos de debug
	 * 
	 * @param fileName - Nome do ficheiro a ler
	 */
	public void debugRead(String fileName) throws ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);

		// optional, but recommended
		// read this -
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getElementsByTagName("Order");
		System.out.println("----------------------------");

		/* Isto serve para as orden */
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			System.out.println("Element :" + nNode.getNodeName());

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				// numero da ordem
				System.out.println("number: " + eElement.getAttribute("Number"));
				if (eElement.getElementsByTagName("Transform").getLength() > 0) {
					System.out.println("Tipo-TRANSFORM:");
					// ORDER
					// TRANSFORM
					NamedNodeMap node = eElement.getElementsByTagName("Transform").item(0).getAttributes();// vai
																											// buscar
																											// os
																											// atributos
																											// de
																											// TRANSFORM

					System.out.println("From : " + node.getNamedItem("From").getNodeValue());
					System.out.println("To : " + node.getNamedItem("To").getNodeValue());
					System.out.println("Quantity : " + node.getNamedItem("Quantity").getNodeValue());
					System.out.println("MaxDelay : " + node.getNamedItem("MaxDelay").getNodeValue());
					System.out.println();

				} else if (eElement.getElementsByTagName("Unload").getLength() > 0) {
					System.out.println("Tipo-Unload:");

					NamedNodeMap node = eElement.getElementsByTagName("Unload").item(0).getAttributes();// vai
																										// buscar
					System.out.println("Type: " + node.getNamedItem("Type").getNodeValue());
					System.out.println("Destination: " + node.getNamedItem("Destination").getNodeValue());
					System.out.println("Quantity : " + node.getNamedItem("Quantity").getNodeValue());
					System.out.println();
				}
			}
		}
		/* Isto serve para os requests */
		nList = doc.getElementsByTagName("Request_Stores");
		Node nNode = nList.item(0);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			System.out.println("request");
		}
	}

	

}
