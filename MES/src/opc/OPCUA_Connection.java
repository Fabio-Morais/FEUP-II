package opc;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

public class OPCUA_Connection {
	
	private static OpcUaClient client;
	private static Object ValueL;
	private String Client_Name;
	private static int id_node = 4;
	private static String aux = "|var|CODESYS Control Win V3 x64.Application.";
	
	public static OpcUaClient getClient() {
		return client;
	}

	public static void setClient(OpcUaClient client) {
		OPCUA_Connection.client = client;
	}

	public static Object getValueL() {
		return ValueL;
	}

	public static void setValueL(Object valueL) {
		ValueL = valueL;
	}

	public OPCUA_Connection(String client_Name) {
		super();
		Client_Name = client_Name;
	}
	
	//Fun��o de Conex�o
	public void MakeConnection() {
		
		EndpointDescription[] endpoints;
		System.out.println("asd");
		try {
			endpoints = UaTcpStackClient.getEndpoints(Client_Name).get();
			OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();
			cfg.setEndpoint(endpoints[0]);
			client = new OpcUaClient(cfg.build());
			client.connect().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("asd232q");

			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			System.out.println("123123");

			e.printStackTrace();
		}
		
	}

	
	/*Fun��o para ler o valor de uma variavel em especifico de uma c�lula em especifico
	 * String celulaName -> cont�m o nome do POU da c�lula
	 * String VarName -> cont�m o nome da variav�l que se pretende ler o valor
	 */
	public static void get_Value(String cellName, String VarName) {
		String aux1;
		aux1 = aux + cellName + "." + VarName;
		NodeId nodeidstring = new NodeId(id_node, aux1);
		DataValue value;
		try {
		value = client.readValue(0, TimestampsToReturn.Both, nodeidstring).get();
		setValueL(value);
		ValueL = ((DataValue)getValueL()).getValue().getValue();
		//System.out.println("O valor da vari�vel �: " + ValueL);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*Fun��o para inserir valores
	 * String celulaName -> cont�m o nome do POU da c�lula
	 * String VarName -> cont�m o nome da variav�l que se pretende mudar o valor
	 * boolean ValueSet -> cont�m o valor "true" ou "false" que se pretende atribuir � vari�vel
	 */
	public static void setValue(String cellName, String VarName, boolean ValueSet) {
		String aux2;
		aux2 = aux + cellName + "." + VarName;
		NodeId nodeidstring = new NodeId(id_node, aux2);
		
		boolean i = ValueSet;
		Variant v = new Variant(i);
		DataValue dv = new DataValue(v);
		
		try {
			getClient().writeValue(nodeidstring, dv).get();
			//System.out.println("Variavel alterada para: " + ((DataValue) client.readValue(0, TimestampsToReturn.Both, nodeidstring).get()).getValue().getValue());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
        static void setValue_int(String cellName, String VarName, int ValueSet) {
		String aux2;
		aux2 = aux + cellName + "." + VarName;
		NodeId nodeidstring = new NodeId(id_node, aux2);
		
		int i = ValueSet;
		Variant v = new Variant((short) i);
		DataValue dv = new DataValue(v);
		
		try {
			getClient().writeValue(nodeidstring, dv).get();
			//System.out.println("Variavel alterada para: " + ((DataValue) client.readValue(0, TimestampsToReturn.Both, nodeidstring).get()).getValue().getValue());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}