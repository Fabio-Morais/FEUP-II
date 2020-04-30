package main;

import java.io.IOException;

import db.DataBase;
import db.Descarga;
import db.Producao;
import opc.OpcClient;
import test.Teste;
import udp.ServerUdp;
import udp.estatistica.Estatistica;

public class Main {

	public static void main(String[] args) {

		//testaDB();
		//System.out.println("Enviou para DB");


		
		Teste teste = new Teste();
		teste.testar();
	}

	public static void exportaHtml() throws IOException {
		Estatistica estatistica = new Estatistica();
		estatistica.exportaFicheiros(false);

	}

	public static void testaOpc() {
		OpcClient opc = OpcClient.getInstance();
		opc.connect();
		System.out.println("começa a ler:");

	}

	public static void testaUdp() {
		ServerUdp server = ServerUdp.getInstance();
		server.run();

		/*
		 * ClientUdp clientUdp = new ClientUdp("127.0.0.1"); clientUdp.sendEcho("ola");
		 */
	}

	public static void testaDB() {
		DataBase db = DataBase.getInstance();

		db.executeQuery("DELETE FROM fabrica.ordem");

		db.insereProducao(new Producao("10", "P2", "P3", 30,500));
		db.insereProducao(new Producao("11", "P1", "P5", 20,400));
		db.insereProducao(new Producao("12", "P5", "P5", 10,800));
		db.insereProducao(new Producao("120", "P5", "P5", 10,800));
		db.insereProducao(new Producao("121", "P5", "P5", 10,800));
		db.insereProducao(new Producao("122", "P5", "P5", 10,800));
		db.insereProducao(new Producao("123", "P5", "P5", 10,800));
		db.insereProducao(new Producao("124", "P5", "P5", 10,800));
		db.insereProducao(new Producao("125", "P5", "P5", 10,800));
		db.insereDescarga(new Descarga("13", "P2", "P3", 10));
		db.insereDescarga(new Descarga("14", "P2", "P5", 30));
		db.insereDescarga(new Descarga("15", "P2", "P7", 50));
	


		/*for (int i = 0; i < 5; i++) {
			int tempo = new Random().nextInt(8 + 1) + 1;
			 db.insereZonaDescarga(new ZonaDescarga("MA", "P"+tempo));
			 db.insereZonaDescarga(new ZonaDescarga("MB", "P"+tempo));

		}*/
/*
		for (int i = 0; i < 5; i++) {
			int tempo = new Random().nextInt(8 + 1) + 1;
			 db.insereMaquina(new Maquina("MB", "P"+ tempo, (tempo*2)+i*2));

		}*/
	}

}
