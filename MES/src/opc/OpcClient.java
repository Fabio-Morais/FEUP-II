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
	private String sfc = "|var|CODESYS Control Win V3 x64.Application.";
	private String publicHostName;

	
	private OpcClient() {
		super();
		try {
			this.publicHostName = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		connect();
	}

	public static OpcClient getInstance() {
		if(instance == null)
			instance = new OpcClient();
		return instance;
	}
	/**
	 * Função para ler o valor de uma variavel em especifico de uma célula em
	 * especifico
	 * 
	 * @return true se fez conexão corretamente, false caso contrario
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
	 * Função para ler o valor de uma variavel em especifico ARRAY OU INTEIRO (|var|CODESYS Control Win V3 x64.Application.)
	 * @param localizacao - localizaçao da variavel (SFS ou Fabrica)
	 * @param nomeVariavel - contém o nome da variavel
	 * @return short[1] caso retorne uma valor, ou um short[x] caso retorne um array
	 */
	public short[] getValue(String localizacao, String nomeVariavel) {
		short[] valueShort = new short[1];
		
		String id=sfc +localizacao+"."+ nomeVariavel ;
		NodeId nodeIdString = new NodeId(idNode, id);
		DataValue value = null;

		/*ler para array*/
		if(nomeVariavel.equals("Stock")) {
			return readToArray(nomeVariavel);
		}
		
		client.readValue(0, TimestampsToReturn.Both, nodeIdString);
		try {
			value = client.readValue(0, TimestampsToReturn.Both, nodeIdString).get();
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		valueShort[0] = (short) value.getValue().getValue();
		return valueShort;
		
	}
	
	/**
	 * Função para ler o valor de uma variavel em especifico BOOLEANA (|var|CODESYS Control Win V3 x64.Application.)
	 * @param localizacao - localizaçao da variavel (SFS ou Fabrica)
	 * @param nomeVariavel - contém o nome da variavel
	 * @return short[1] caso retorne uma valor, ou um short[x] caso retorne um array
	 */
	public boolean getValueBool(String localizacao, String nomeVariavel) {
		
		
		String id=sfc +localizacao+"."+ nomeVariavel ;
		NodeId nodeIdString = new NodeId(idNode, id);
		DataValue value = null;

		client.readValue(0, TimestampsToReturn.Both, nodeIdString);
		try {
			value = client.readValue(0, TimestampsToReturn.Both, nodeIdString).get();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (boolean) value.getValue().getValue();
		
	}
	
	private short[] readToArray(String nomeVariavel) {
		short[] valueShort = new short[9];

		for(int i=0; i<9; i++) {
			String id = sfc + nomeVariavel+"["+(i+1)+"]";
			NodeId nodeIdString = new NodeId(idNode, id);
			client.readValue(0, TimestampsToReturn.Both, nodeIdString);
			try {
				valueShort[i] = (short) client.readValue(0, TimestampsToReturn.Both, nodeIdString).get().getValue().getValue();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return valueShort;
	}
	
	

	/**Função para inserir valores booleanos (|var|CODESYS Control Win V3 x64.Application.)
	 * @param localizacao - localizaçao da variavel (SFS ou Fabrica)
	 * @param nomeVariavel - Nome da variavel a alterar
	 * @param set - valor da variavel a alterar (pode ser qualquer tipo)
	 * @return true se inseriu corretamente, false caso contrario
	 */
	public <E> boolean setValue(String localizacao, String nomeVariavel, E set ) {
		String id = sfc+localizacao+"." + nomeVariavel;
		NodeId nodeIdString = new NodeId(idNode, id);
		Variant v = new Variant(set);
		DataValue dv = new DataValue(v);
		try {
			client.writeValue(nodeIdString, dv).get();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	

}