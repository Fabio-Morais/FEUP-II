package test;

import java.awt.EventQueue;

import db.Ordem;
import fabrica.Fabrica;
import fabrica.Ordens;
import gui.Gui;

public class Teste {

	public Teste() {
		// TODO Auto-generated constructor stub
	}
	public void enviaSoUma() {
		/*Ordens ordem1 = new Ordens("1", 500, Ordem.localDate(), 500, fabrica);
		ordem1.setPecasPendentes(2);
		ordem1.setTransform(ordem1.new Transform("P1","P2"));//maquina A
		 ordem1.executaOrdem();
		ControlaPlc p = new ControlaPlc();
		while(ordem1.getPecasPendentes()>0) {
			System.out.println(ordem1.getPecasPendentes());
			p.runOrder(ordem1);
			ordem1.pecaParaProducao();
		}
		System.out.println("saiu do 1º ciclo");
		while(ordem1.getPecasProduzidas()!=ordem1.getQuantidade()) {
			System.out.println(ordem1.getPecasProduzidas()+" - "+ordem1.getQuantidade());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("saiu do 2º ciclo");
		ordem1.terminaOrdem();*/
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

		System.out.println("ESPERAR 4s");
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("PRONTO:");

		Fabrica fabrica = Fabrica.getInstance();
		//command 1
		Ordens ordem1 = new Ordens("1", 300, Ordem.localDate(), 300, fabrica);
		ordem1.setPecasPendentes(30);
		ordem1.setTransform(ordem1.new Transform("P2","P6"));//maquina A

		
		//command 2
		Ordens ordem2 = new Ordens("2", 300,Ordem.localDate(), 300, fabrica);
		ordem2.setPecasPendentes(7);
		ordem2.setTransform(ordem2.new Transform("P3","P5"));//maquina B
		
		Ordens ordem3 = new Ordens("3", 300,Ordem.localDate(), 300, fabrica);
		ordem3.setPecasPendentes(10);
		ordem3.setTransform(ordem3.new Transform("P7", "P9"));//maquina B
		
		
		//command 3
		Ordens ordem4 = new Ordens("4", 300,Ordem.localDate(), 300, fabrica);
		ordem4.setPecasPendentes(7);
		ordem4.setTransform(ordem4.new Transform("P4", "P8"));
		
		Ordens ordem5 = new Ordens("5", -1,Ordem.localDate(), -1, fabrica);
		ordem5.setPecasPendentes(8);
		ordem5.setUnload(ordem5.new Unload("P9","PM2"));

		Ordens ordem6 = new Ordens("6", -1,Ordem.localDate(), -1, fabrica);
		ordem6.setPecasPendentes(6);
		ordem6.setUnload(ordem6.new Unload("P6", "PM3"));
		
		
		//command 4
		Ordens ordem7 = new Ordens("7", 900,Ordem.localDate(), 900, fabrica);
		ordem7.setPecasPendentes(20);
		ordem7.setTransform(ordem7.new Transform("P1", "P9"));
		
		Ordens ordem8 = new Ordens("101", 900,Ordem.localDate(), 900, fabrica);
		ordem8.setPecasPendentes(6);
		ordem8.setTransform(ordem8.new Transform("P4", "P5"));


		fabrica.atualizaHeap();

		fabrica.gereOrdens();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("***COMEÇA***");
		
		/**COMMAND 1*/
		fabrica.addToHeap(ordem1);
		
		/**COMMAND 2*/
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fabrica.addToHeap(ordem2);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fabrica.addToHeap(ordem3);
		
		
		/**COMMAND 3*/
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fabrica.addToHeap(ordem4);
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fabrica.addToHeap(ordem5);
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fabrica.addToHeap(ordem6);
		
		/**COMMAND 3*/
		try {
			Thread.sleep(80000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fabrica.addToHeap(ordem7);
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fabrica.addToHeap(ordem8);
	
	}

}
