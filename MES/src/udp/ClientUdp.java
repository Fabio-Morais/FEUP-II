
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
	private DatagramPacket packet;

	public ClientUdp(String address, DatagramPacket packet) {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.packet = packet;
		try {
			this.address = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void sendEcho(String msg) {
		byte[] buf = msg.getBytes();
		//socket.getPort()
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, this.packet.getPort());

		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket.close();
	}


}