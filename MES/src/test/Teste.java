package test;

import java.awt.EventQueue;

import db.DataBase;
import db.Ordem;
import fabrica.Fabrica;
import fabrica.Ordens;
import gui.Gui;

public class Teste {

	public Teste() {
		// TODO Auto-generated constructor stub
	}
	
	public void testar() {
		/*ServerUdp udp = ServerUdp.getInstance();
		udp.start();*/
		
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

		Fabrica fabrica = Fabrica.getInstance();
		Ordens ordem1 = new Ordens("1", 500, Ordem.localDate(), 500, fabrica);
		ordem1.setPecasPendentes(5);
		ordem1.setTransform(ordem1.new Transform("P1","P4"));//maquina A
		Ordens ordem2 = new Ordens("2", 600,Ordem.localDate(), 600, fabrica);
		ordem2.setPecasPendentes(5);
		ordem2.setTransform(ordem2.new Transform("P1","P3"));//maquina B
		Ordens ordem3 = new Ordens("3", 700,Ordem.localDate(), 700, fabrica);
		ordem3.setPecasPendentes(5);
		ordem3.setTransform(ordem3.new Transform("P1","P2"));//maquina A
		Ordens ordem4 = new Ordens("4", 800,Ordem.localDate(), 800, fabrica);
		ordem4.setPecasPendentes(5);
		ordem4.setTransform(ordem4.new Transform("P1","P4"));//maquina C


		fabrica.atualizaHeap();

		fabrica.gereOrdens();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("***COMEÇA***");
		fabrica.addToHeap(ordem1);
/*
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fabrica.addToHeap(ordem2);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fabrica.addToHeap(ordem3);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fabrica.addToHeap(ordem4);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
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
