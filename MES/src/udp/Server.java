package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server extends Thread {
	private final int port = 54321;
	private DatagramSocket socket;
	private boolean running;
	private byte[] buf = new byte[1020];

	public Server() {
		super("udpThread");
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		running = true;

		while (running) {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			

			try {
				socket.receive(packet);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			String sentence = new String(packet.getData());
			String realMessage = sentence.substring(0, packet.getLength());
			System.out.println("RECEIVED: " + realMessage);
			
			InetAddress address = packet.getAddress();
			
			String received = new String(packet.getData(), 0, packet.getLength());

			if (realMessage.equals("end")) {
				running = false;
				continue;
			}
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		socket.close();
	}
}
