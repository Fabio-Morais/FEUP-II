package xml;

public class XmlRequestStore {

	public XmlRequestStore() {
		sendXml();
	}
	/**
	 * Cria XML relativamente à existencia de peças no armazem e envia
	 */
	public void sendXml() {
		String xml = "<Current_Stores>\r\n";
		String px = "P0";
		int quant = 0;

		/* Precisa de ir à DB ver as peças que tem */
		for (int i = 0; i < 9; i++) {
			px = px.substring(0, 1) + "" + (i + 1);
			xml += "<WorkPiece type=”" + px + "” quantity=”" + (quant++) * 2 + "”/>\r\n";
		}
		xml += "</Current_Stores>";
		System.out.println(xml);
	}
}
