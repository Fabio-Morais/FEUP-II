package xml;

public class XmlRequestStore {

	public XmlRequestStore() {
		sendXml();
	}
	/**
	 * Cria XML relativamente � existencia de pe�as no armazem e envia
	 */
	public void sendXml() {
		String xml = "<Current_Stores>\r\n";
		String px = "P0";
		int quant = 0;

		/* Precisa de ir � DB ver as pe�as que tem */
		for (int i = 0; i < 9; i++) {
			px = px.substring(0, 1) + "" + (i + 1);
			xml += "<WorkPiece type=�" + px + "� quantity=�" + (quant++) * 2 + "�/>\r\n";
		}
		xml += "</Current_Stores>";
		System.out.println(xml);
	}
}
