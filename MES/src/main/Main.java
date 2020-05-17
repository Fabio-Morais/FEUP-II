package main;

import java.awt.EventQueue;

import fabrica.Fabrica;
import gui.Gui;
import udp.ServerUdp;

public class Main {

	public static void main(String[] args) {

	
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
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Fabrica fabrica = Fabrica.getInstance();
		fabrica.atualizaHeap();
		fabrica.gereOrdens();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ServerUdp udp = ServerUdp.getInstance();
		udp.start();
	}

}
