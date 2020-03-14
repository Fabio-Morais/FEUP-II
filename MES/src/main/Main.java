package main;


import java.util.Scanner;

import udp.Client;
import udp.Server;
import xml.Xml;

public class Main {

	public static void main(String[] args) {
		Xml xml = new Xml();
		//xml.existenciaPeca();
		Server server = new Server();
		server.run();
		
		/*Client client = new Client();
		Scanner scan = new Scanner(System.in);

			client.sendEcho(scan.nextLine());
			
			client.close();	*/
	
	}

}
