package udp;

public class RequestStore {
	private String address;
	public RequestStore(String address) {
		this.address=address;
		sendXml();
	}
	/**
	 * Cria XML relativamente � existencia de pe�as no armazem e envia
	 */
	protected void sendXml() {
		String xml = "<?xml version=\"1.0\"?>\r\n" + 
				"<Current_Stores>\r\n";
		String px = "P0";
		int quant = 0;

		/* Precisa de ir � DB ver as pe�as que tem */
		for (int i = 0; i < 9; i++) {
			px = px.substring(0, 1) + "" + (i + 1);
			xml += "<WorkPiece type=\"" + px + "\" quantity=\"" + (quant++) * 2 + "\"/>\r\n";
		}
		xml += "</Current_Stores>";
		System.out.println(xml);
		
		ClientUdp clientUdp = new ClientUdp(address);
		clientUdp.sendEcho(xml);
		clientUdp.close();
	}
}
