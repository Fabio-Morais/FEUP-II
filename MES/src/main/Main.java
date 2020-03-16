package main;



import udp.ClientUdp;
import udp.ServerUdp;

public class Main {

	public static void main(String[] args) {
		ServerUdp server = ServerUdp.getInstance();
		server.run();
		
		ClientUdp clientUdp = new ClientUdp("127.0.0.1");
		clientUdp.sendEcho("ola");
	
	}

}
