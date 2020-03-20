package main;



import db.DataBase;
import db.Descarga;
import db.Maquina;
import db.Producao;
import db.ZonaDescarga;
import opc.OpcClient;
import udp.ClientUdp;
import udp.ServerUdp;

public class Main {

	public static void main(String[] args) {
		//testaUdp();
		
		//testaDB();
		
		testaOpc();
	}
	public static void testaOpc() {
		OpcClient opc= new OpcClient();
		opc.makeConnection();
		System.out.println("começa a ler:");
		opc.setValue("teste", (short) 2); // INT = SHORT em java
		opc.getValue("teste");
	}
	
	public static void testaUdp() {
		ServerUdp server = ServerUdp.getInstance();
		server.run();
		
		ClientUdp clientUdp = new ClientUdp("127.0.0.1");
		clientUdp.sendEcho("ola");
	}
	
	public static void testaDB() {
		DataBase db = DataBase.getInstance();
		db.insereProducao(new Producao("111", "P2", "P3", 56, 5));
		db.executaOrdemProducao("111");		
		db.terminaOrdemProducao("111");
		
		db.insereDescarga(new Descarga("132", "P2", "CM2", 10));
		db.executaOrdemDescarga("132");
		db.terminaOrdemDescarga("132");
	}

}
