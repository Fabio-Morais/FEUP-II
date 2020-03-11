package main;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import opc.OPCUA_Connection;
import xml.Xml;

public class Main {
	public static OPCUA_Connection MyConnection ;
	public static OpcUaClient client;
	public static String aux = "teste";
	public static String Client = "opc.tcp://"+aux+":53880";
	public static void main(String[] args) {
		Xml xml = new Xml();
		xml.read();
		
		MyConnection = new OPCUA_Connection(Client);
		MyConnection.MakeConnection();
		
		
		//Inicialização das variáveis
		String cellName, variable;
		Boolean value;
		cellName = "eu";
		variable = "Hello";
		value= false;
		
		/*Funções: get_Value para saber o valor de uma variavel
		*          setValue para escrever o valor numa variavel
		*/
		System.out.println("--------------Value Get--------------");
		MyConnection.get_Value(cellName,variable);
		System.out.println("--------------Value Change--------------");
		MyConnection.setValue(cellName,variable,value);
	}

}
