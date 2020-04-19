package main;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Scanner;

import db.DataBase;
import db.Descarga;
import db.Producao;
import fabrica.Fabrica;
import fabrica.Ordens;
import gui.Gui;
import opc.OpcClient;
import udp.ServerUdp;
import udp.estatistica.Estatistica;

public class Main {

	public static void main(String[] args) {

		testaDB();
		System.out.println("Enviou para DB");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("Abriu gui");
		DataBase db = DataBase.getInstance();

		System.out.println("ESPERAR 4s");
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("PRONTO:");
		Ordens ordem1 = new Ordens("123", 50);
		Ordens ordem2 = new Ordens("124", 20);
		Ordens ordem3 = new Ordens("125", 100);
		Ordens ordem4 = new Ordens("126", 200);
		Ordens ordem5 = new Ordens("127", -1);


		Fabrica fabrica = Fabrica.getInstance();
		fabrica.atualizaHeap();
		

		Scanner myObj = new Scanner(System.in); // Create a Scanner object

		while (true) {
			String numeroOrdem = myObj.nextLine(); // Read user input
			System.out.println("Entra? " + numeroOrdem.equals("1"));
			System.out.println("Vazia? " + fabrica.getHeapOrdemPendente().isEmpty());
			if (numeroOrdem.equals("a")) {
				if (!fabrica.getHeapOrdemPendente().isEmpty()) {
					fabrica.getHeapOrdemPendente().poll().executaOrdem();

				}
			} else if(numeroOrdem.equals("s")){
				fabrica.addToHeap(ordem1);
				fabrica.addToHeap(ordem2);
				fabrica.addToHeap(ordem3);
				fabrica.addToHeap(ordem4);
				fabrica.addToHeap(ordem5);
			}else if(numeroOrdem.charAt(0)=='e'){
				 fabrica.getHeapOrdemExecucao().get(numeroOrdem.substring(2, numeroOrdem.length())).terminaOrdem();
			}else if(numeroOrdem.charAt(0)=='1'){
				 fabrica.getHeapOrdemExecucao().get(numeroOrdem.substring(2, numeroOrdem.length())).pecaParaProducao();
			}else if(numeroOrdem.charAt(0)=='2'){
				 fabrica.getHeapOrdemExecucao().get(numeroOrdem.substring(2, numeroOrdem.length())).pecasProduzidas();
			}else {
				Ordens ordem = fabrica.getHeapOrdemExecucao().get(numeroOrdem);
				if (ordem != null) {
					ordem.setPecasEmProducao(3 + ordem.getPecasEmProducao());
					ordem.setPecasPendentes(2 + ordem.getPecasPendentes());
					ordem.setPecasProduzidas(1 + ordem.getPecasProduzidas());
				}
			}
		}

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
