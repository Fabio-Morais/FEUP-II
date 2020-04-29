package test;

import java.awt.EventQueue;

import db.DataBase;
import fabrica.Fabrica;
import fabrica.Ordens;
import gui.Gui;
import udp.ServerUdp;

public class Teste {

	public Teste() {
		// TODO Auto-generated constructor stub
	}
	
	public void testar() {
		ServerUdp udp = ServerUdp.getInstance();
		udp.start();
		
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

		fabrica.gereOrdens();
		
		
		
		
		/*
		Scanner myObj = new Scanner(System.in); // Create a Scanner object


		
		while (true) {
			String numeroOrdem = myObj.nextLine(); // Read user input
			System.out.println("Entra? " + numeroOrdem.equals("1"));
			System.out.println("Vazia? " + fabrica.getHeapOrdemPendente().isEmpty());
			if (numeroOrdem.equals("a")) {
				if (!fabrica.getHeapOrdemPendente().isEmpty()) {
					System.out.println(fabrica.getCopyHeapOrdemPendente().peek());
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
		}*/
	}

}
