package opc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import db.DataBase;

public class OpcClient {
	private static OpcClient instance=null;

	private OpcUaClient client;
	private int idNode = 4;
	private String aux = "|var|CODESYS Control Win V3 x64.Application.SFS.";
	private String publicHostName;

	
	private OpcClient() {
		super();
		try {
			this.publicHostName = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static OpcClient getInstance() {
		if(instance == null)
			instance = new OpcClient();
		return instance;
	}
	/**
	 * Fun��o para ler o valor de uma variavel em especifico de uma c�lula em
	 * especifico
	 * 
	 * @return true se fez conex�o corretamente, false caso contrario
	 */
	public boolean connect() {

		EndpointDescription[] endpoints;
		try {
			endpoints = UaTcpStackClient.getEndpoints("opc.tcp://" + publicHostName + ":4840").get();
			OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();
			cfg.setEndpoint(endpoints[0]);
			client = new OpcUaClient(cfg.build());
			client.connect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	/**
	 * Fun��o para ler o valor de uma variavel em especifico
	 * @param celulaName -> cont�m o nome da variavel
	 * @return true se nao houve erro, false se houve algum erro
	 */
	public boolean getValue(String nomeVariavel) {
		String id = aux + nomeVariavel;
		NodeId nodeIdString = new NodeId(idNode, id);
		DataValue value = null;
		client.readValue(0, TimestampsToReturn.Both, nodeIdString);
		try {
			value = client.readValue(0, TimestampsToReturn.Both, nodeIdString).get();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("O valor da vari�vel �: " + value.getValue().getValue());
		return true;
	}
	
	
	

	/**Fun��o para inserir valores booleanos
	 * @return true se inseriu corretamente, false caso contrario
	 */
	public <E> boolean setValue(String nomeVariavel, E set ) {
		String id = aux + nomeVariavel;

		NodeId nodeIdString = new NodeId(idNode, id);

		Variant v = new Variant(set);
		DataValue dv = new DataValue(v);

		try {
			client.writeValue(nodeIdString, dv);
			System.out.println("Variavel alterada para: "
					+ ((DataValue) client.readValue(0, TimestampsToReturn.Both, nodeIdString).get()).getValue()
							.getValue());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	

}