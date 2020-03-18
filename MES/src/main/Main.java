package main;



import db.DataBase;
import db.Maquina;
import udp.ClientUdp;
import udp.ServerUdp;

public class Main {

	public static void main(String[] args) {
		/*ServerUdp server = ServerUdp.getInstance();
		server.run();
		
		ClientUdp clientUdp = new ClientUdp("127.0.0.1");
		clientUdp.sendEcho("ola");*/
		
		DataBase db = DataBase.getInstance();
		db.insereMaquina(new Maquina("MA", "P1", 10));
	
	}

}
