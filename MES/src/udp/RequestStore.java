package udp;

import java.net.DatagramPacket;

import opc.OpcClient;

public class RequestStore {
	private String address;
	private  DatagramPacket packet;
	public RequestStore(String address,  DatagramPacket packet) {
		this.address=address;
		this.packet = packet;
		sendXml();
	}
	/**
	 * Cria XML relativamente à existencia de peças no armazem e envia
	 */
	protected void sendXml() {
		OpcClient opcClient = OpcClient.getInstance();
		short[] values = opcClient.getValue("SFS","Stock");
		if(values.length != 9)
			return;
		
		String xml = "<?xml version=\"1.0\"?>\r\n" + 
				"<Current_Stores>\r\n";
		String px = "P0";


		for (int i = 0; i < 9; i++) {
			px = px.substring(0, 1) + "" + (i + 1);
			xml += "<WorkPiece type=\"" + px + "\" quantity=\"" + values[i] + "\"/>\r\n";
		}
		xml += "</Current_Stores>";

		ClientUdp clientUdp = new ClientUdp(address, packet);
		clientUdp.sendEcho(xml);
	}
}