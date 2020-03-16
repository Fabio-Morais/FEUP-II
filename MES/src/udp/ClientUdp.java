package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientUdp extends Thread {
	private final int port = 54321;

	private DatagramSocket socket;
	private InetAddress address;
	private byte[] buf;

	public ClientUdp(String address) {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.address = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void sendEcho(String msg) {
		buf = msg.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*packet = new DatagramPacket(buf, buf.length);

		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("vai enviar");

		String received = new String(packet.getData(), 0, packet.getLength());
		return received;*/
	}

	public void close() {
		socket.close();
	}
}
