package main;

import java.awt.EventQueue;
import java.io.IOException;

import db.DataBase;
import db.Descarga;
import db.Maquina;
import db.Ordem;
import db.Producao;
import db.ZonaDescarga;
import fabrica.Fabrica;
import fabrica.Ordens;
import gui.Gui;
import opc.OpcClient;
import udp.ClientUdp;
import udp.ServerUdp;
import udp.estatistica.Estatistica;
import udp.estatistica.EstatisticasMaquina;
import udp.estatistica.ListaOrdens;
import udp.estatistica.PecasDescarregadas;

import org.apache.commons.io.FileUtils;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {

	public static void main(String[] args) {

		/*EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Fabrica fabrica = Fabrica.getInstance();
		fabrica.atualizaHeap();*/
		
		OpcClient opc = OpcClient.getInstance();
		System.out.println(opc.connect());
		

	/*
		fabrica.getPlant().printMap();

		OpcClient opc = OpcClient.getInstance();
		System.out.println("vai buscar os dados para atualizar");

		while (true) {


			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("------------------");
			fabrica.getPlant().printMap();
			System.out.println("------------------");
		}*/
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

		db.insereProducao(new Producao("4231", "P2", "P3", 20, 100));
		for (int i = 0; i < 5; i++) {
			int r = new Random().nextInt(1000);
			int tempo = new Random().nextInt(500 + 1) + 300;
			// db.insereProducao(new Producao(""+r, "P2", "P3", (i+5)*2, tempo));
			// db.insereDescarga(new Descarga(""+r, "P2", "P3", tempo));
		}
		// db.executaOrdemProducao("542");
		// db.terminaOrdemProducao("423");

		for (int i = 0; i < 5; i++) {
			int tempo = new Random().nextInt(8 + 1) + 1;
			// db.insereZonaDescarga(new ZonaDescarga("MA", "P"+tempo));
			// db.insereZonaDescarga(new ZonaDescarga("MB", "P"+tempo));

		}

		for (int i = 0; i < 5; i++) {
			int tempo = new Random().nextInt(8 + 1) + 1;
			// db.insereMaquina(new Maquina("MB", "P"+ tempo, (tempo*2)+i*2));

		}
	}

}
