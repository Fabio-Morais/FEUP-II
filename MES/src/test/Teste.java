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

	}

	/** Ordens do prof */
	public void testar() {

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
		// command 1
		Ordens ordem1 = new Ordens("1", 300, Ordem.localDate(), 300, fabrica);
		ordem1.setPecasPendentes(30);
		ordem1.setTransform(ordem1.new Transform("P2", "P6"));// maquina A

		// command 2
		Ordens ordem2 = new Ordens("2", 300, Ordem.localDate(), 300, fabrica);
		ordem2.setPecasPendentes(7);
		ordem2.setTransform(ordem2.new Transform("P3", "P5"));// maquina B e C

		Ordens ordem3 = new Ordens("3", 300, Ordem.localDate(), 300, fabrica);
		ordem3.setPecasPendentes(10);
		ordem3.setTransform(ordem3.new Transform("P7", "P9"));// maquina B

		// command 3
		Ordens ordem4 = new Ordens("4", 300, Ordem.localDate(), 300, fabrica);
		ordem4.setPecasPendentes(7);
		ordem4.setTransform(ordem4.new Transform("P4", "P8"));//maquina C

		Ordens ordem5 = new Ordens("5", -1, Ordem.localDate(), -1, fabrica);
		ordem5.setPecasPendentes(8);
		ordem5.setUnload(ordem5.new Unload("P9", "PM2"));//descarga

		Ordens ordem6 = new Ordens("6", -1, Ordem.localDate(), -1, fabrica);
		ordem6.setPecasPendentes(6);
		ordem6.setUnload(ordem6.new Unload("P6", "PM3"));//descarga

		// command 4
		Ordens ordem7 = new Ordens("7", 900, Ordem.localDate(), 900, fabrica);
		ordem7.setPecasPendentes(20);
		ordem7.setTransform(ordem7.new Transform("P1", "P9"));//maquina A OU C OU B

		Ordens ordem8 = new Ordens("101", 900, Ordem.localDate(), 900, fabrica);
		ordem8.setPecasPendentes(6);
		ordem8.setTransform(ordem8.new Transform("P4", "P5"));//maquina C

		fabrica.atualizaHeap();
		fabrica.gereOrdens();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("***COMEÇA***");

		/** COMMAND 1 */
		fabrica.addToHeap(ordem1);

		/** COMMAND 2 */
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

		/** COMMAND 3 */
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

		/** COMMAND 3 */
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

	public void testar2() {

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
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("PRONTO:");
		Fabrica fabrica = Fabrica.getInstance();

		Ordens ordem1 = new Ordens("1", 300, Ordem.localDate(), 300, fabrica);
		ordem1.setPecasPendentes(7);
		ordem1.setTransform(ordem1.new Transform("P3", "P5"));// maquina A
		
		Ordens ordem3 = new Ordens("3", 300, Ordem.localDate(), 300, fabrica);
		ordem3.setPecasPendentes(6);
		ordem3.setTransform(ordem3.new Transform("P7", "P9"));// maquina B
		
		Ordens ordem4 = new Ordens("4", 300, Ordem.localDate(), 300, fabrica);
		ordem4.setPecasPendentes(5);
		ordem4.setTransform(ordem4.new Transform("P4", "P5"));// maquina C

		
		fabrica.atualizaHeap();
		fabrica.gereOrdens();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("***COMEÇA***");

		/** COMMAND 1 */
		fabrica.addToHeap(ordem1);
		fabrica.addToHeap(ordem3);
		fabrica.addToHeap(ordem4);

		try {
			Thread.sleep(9000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Ordens ordem2 = new Ordens("2", 200, Ordem.localDate(), 200, fabrica);
		ordem2.setPecasPendentes(1);
		ordem2.setTransform(ordem2.new Transform("P1", "P9"));// maquina A

		fabrica.addToHeap(ordem2);

	}
	public void testar3() {
		/*GereOrdensThread.setmBLivreSeleciona("45", 0);
		GereOrdensThread.setmBLivreSeleciona("45", 1);
		GereOrdensThread.setmBLivreSeleciona("45", 2);*/
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
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("PRONTO:");
		Fabrica fabrica = Fabrica.getInstance();

		Ordens ordem1 = new Ordens("1", 300, Ordem.localDate(), 300, fabrica);
		ordem1.setPecasPendentes(7);
		ordem1.setTransform(ordem1.new Transform("P3", "P5"));// maquina A

		
		Ordens ordem3 = new Ordens("3", 300, Ordem.localDate(), 300, fabrica);
		ordem3.setPecasPendentes(7);
		ordem3.setTransform(ordem3.new Transform("P3", "P7"));// maquina B
		
		Ordens ordem4 = new Ordens("4", 300, Ordem.localDate(), 300, fabrica);
		ordem4.setPecasPendentes(3);
		ordem4.setTransform(ordem4.new Transform("P4", "P5"));// maquina C

		fabrica.atualizaHeap();
		fabrica.gereOrdens();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("***COMEÇA***");

		/** COMMAND 1 */

		fabrica.addToHeap(ordem1);
		//fabrica.addToHeap(ordem4);

		/*10S*/

		System.out.println("COMEÇAAAAAAAAAAA");
		//GereOrdensThread.setmBLivreSeleciona("", 0);
		//GereOrdensThread.setmBLivreSeleciona("", 1);
		//GereOrdensThread.setmBLivreSeleciona("", 2);
		fabrica.addToHeap(ordem3);

		Ordens ordem2 = new Ordens("2", 200, Ordem.localDate(), 200, fabrica);
		ordem2.setPecasPendentes(5);
		ordem2.setTransform(ordem2.new Transform("P3", "P5"));// maquina A

		//fabrica.addToHeap(ordem2);

	}

}
