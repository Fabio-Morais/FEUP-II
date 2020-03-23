package main;




import java.io.IOException;


import db.DataBase;
import db.Descarga;
import db.Maquina;
import db.Ordem;
import db.Producao;
import db.ZonaDescarga;
import opc.OpcClient;
import udp.ClientUdp;
import udp.ServerUdp;
import udp.estatistica.Estatistica;
import udp.estatistica.EstatisticasMaquina;
import udp.estatistica.ListaOrdens;
import udp.estatistica.PecasDescarregadas;

import org.apache.commons.io.FileUtils ;
import java.text.SimpleDateFormat;


public class Main {

	public static void main(String[] args) {
		//testaUdp();
		
		//testaDB();
		
		//testaOpc();
		//testaDB();

		ListaOrdens estatisticasMaquina = new ListaOrdens();
		try {
			estatisticasMaquina.exportaFicheiro();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void exportaHtml() throws IOException {
		Estatistica estatistica = new Estatistica();
		estatistica.exportaFicheiros();
		
	}
	public static void testaOpc() {
		OpcClient opc= OpcClient.getInstance();
		opc.connect();
		System.out.println("começa a ler:");
		opc.setValue("teste", (short) 2); // INT = SHORT em java
		opc.getValue("teste");
	}
	
	public static void testaUdp() {
		ServerUdp server = ServerUdp.getInstance();
		server.run();
		
		/*ClientUdp clientUdp = new ClientUdp("127.0.0.1");
		clientUdp.sendEcho("ola");*/
	}
	
	public static void testaDB() {
		DataBase db = DataBase.getInstance();
		db.insereProducao(new Producao("542", "P2", "P3", 56, 50));
		db.executaOrdemProducao("542");		
		//db.terminaOrdemProducao("423");
		
		/*db.insereDescarga(new Descarga("132", "P2", "CM2", 10));
		db.executaOrdemDescarga("132");
		db.terminaOrdemDescarga("132");*/
	}

}
