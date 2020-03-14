package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import xml.Xml;

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

		while (true) {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			

			try {
				socket.receive(packet);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			String sentence = new String(packet.getData());
			String realMessage = sentence.substring(0, packet.getLength());
			
			Xml xml = new Xml();
			xml.read(realMessage);
			
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
