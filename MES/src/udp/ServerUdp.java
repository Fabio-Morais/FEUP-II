package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerUdp extends Thread {
	private static ServerUdp instance=null;

	private final int port = 54321;
	private DatagramSocket socket;
	private byte[] buf = new byte[1020];

	private ServerUdp() {
		super("udpThread");
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {

		while (true) {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			

			try {
				socket.receive(packet);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			String sentence = new String(packet.getData());
			String realMessage = sentence.substring(0, packet.getLength());
			
			Message xml;
		
			try {
				xml = new Message(InetAddress.getLocalHost().getHostAddress());
				xml.read(realMessage);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ServerUdp getInstance() {
		if(instance == null)
			instance = new ServerUdp();
		return instance;
	}
}
