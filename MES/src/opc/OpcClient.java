package opc;

import java.io.File;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import fabrica.Fabrica;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class OpcClient {
	private static OpcClient instance = null;
	private Fabrica fabrica;
	private OpcUaClient client;
	private int idNode = 4;
	private String sfc = "|var|CODESYS Control Win V3 x64.Application.";
	private String publicHostName;

	private final AtomicLong clientHandles = new AtomicLong(1L);

	private OpcClient() {
		super();
		this.fabrica = Fabrica.getInstance();
		try {
			this.publicHostName = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		connect();

		try {
			this.createSubscription();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static OpcClient getInstance() {
		if (instance == null)
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
			OpcUaClientConfig config = OpcUaClientConfig.builder()
					.setApplicationName(LocalizedText.english("MinimalClient")).setApplicationUri("theURI")
					.setCertificate(null).setKeyPair(null).setEndpoint(endpoints[0])
					.setMaxResponseMessageSize(uint(50000)).setIdentityProvider(new AnonymousProvider())
					.setRequestTimeout(uint(5000)).build();
			client = new OpcUaClient(config);
			client.connect().get();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	/** Cria subscrição para as variaveis escritas num ficheiro .txt */
	private void createSubscription() throws Exception {

		UaSubscription sub = client.getSubscriptionManager().createSubscription(10.0).get();

		BiConsumer<UaMonitoredItem, Integer> onItemCreated = (item, id) -> item
				.setValueConsumer(this::onSubscriptionValue);

		List<UaMonitoredItem> items = sub
				.createMonitoredItems(TimestampsToReturn.Both, createMonitoredItemCreateRequests(), onItemCreated)
				.get();

		/*for (UaMonitoredItem item : items) {
			if (item.getStatusCode().isGood()) {
				System.out.println("Item created for NodeId: " + item.getReadValueId().getNodeId());
			} else {
				System.out.println("Failed to create item for NodeId: " + item.getReadValueId().getNodeId()
						+ item.getStatusCode());
			}
		}*/

	}

	/** Sempre que uma variavel muda de valor esta função corre */
	private void onSubscriptionValue(UaMonitoredItem item, DataValue value) {
		String aux = item.getReadValueId().getNodeId().getIdentifier().toString();
		boolean valor = (boolean) value.getValue().getValue();
		//System.out.println("->" + aux.substring(44, aux.length()) + " - " + value.getValue().getValue());
		String node = aux.substring(44, aux.length());
		int estado = (valor == true) ? 0 : 1; // se estiver free -> 0, se estiver ocupado -> 1
		int[] coords = calculaCoords(node);
		if(coords.length == 2) {
			fabrica.getPlant().changeMap(coords[0], coords[1], estado);
		}
	}

	/** Vai buscar os Nodes ID no ficheiro de texto */
	private List<MonitoredItemCreateRequest> createMonitoredItemCreateRequests() throws IOException {
		File yourFile = new File("NodeIDs.txt");
		yourFile.createNewFile(); // if file already exists will do nothing

		List<String> ids = Files.readAllLines(Paths.get("Nodes/NodeIDs.txt"));
		List<ReadValueId> rvIDs = new ArrayList<>();

		for (String line : ids) {
			rvIDs.add(new ReadValueId(new NodeId(idNode, "|var|CODESYS Control Win V3 x64.Application." + line),
					AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE));
		}
		List<MonitoredItemCreateRequest> MICR = new ArrayList<>();
		for (ReadValueId ID : rvIDs) {
			UInteger clientHandle = uint(clientHandles.getAndIncrement());
			MonitoringParameters parameters = new MonitoringParameters(clientHandle, 10.0, null, uint(10), true);
			MICR.add(new MonitoredItemCreateRequest(ID, MonitoringMode.Reporting, parameters));
		}
		return MICR;
	}

	private int[] calculaCoords(String string) {
		String aux = string.substring(8, string.length() - ".free".length());
		int[] x = new int[2];
		try{
			x[0] = Integer.valueOf(aux.substring(1, 2));
			x[1] = Integer.valueOf(aux.substring(3, 4))-1;//corrige o valor, pois o Y começa em 1
		} catch(Exception e ) {
			return new int [0];
		}
		return x;
	}

	/**
	 * Função para ler o valor de uma variavel em especifico ARRAY OU INTEIRO
	 * (|var|CODESYS Control Win V3 x64.Application.)
	 * 
	 * @param localizacao  - localizaçao da variavel (SFS ou Fabrica)
	 * @param nomeVariavel - contém o nome da variavel
	 * @return short[1] caso retorne uma valor, ou um short[x] caso retorne um array
	 */
	public short[] getValue(String localizacao, String nomeVariavel) {
		short[] valueShort = new short[1];

		String id = sfc + localizacao + "." + nomeVariavel;
		NodeId nodeIdString = new NodeId(idNode, id);
		DataValue value = null;

		/* ler para array */
		if (nomeVariavel.equals("Stock")) {
			return readToArray(nomeVariavel);
		}
		client.readValue(0, TimestampsToReturn.Both, nodeIdString);
		try {
			value = client.readValue(0, TimestampsToReturn.Both, nodeIdString).get();
		} catch (Exception e) {
			e.printStackTrace();

			return new short[0];
		}

		valueShort[0] = (short) value.getValue().getValue();
		return valueShort;

	}

	/**
	 * Função para ler o valor de uma variavel em especifico BOOLEANA (|var|CODESYS
	 * Control Win V3 x64.Application.)
	 * 
	 * @param localizacao  - localizaçao da variavel (SFS ou Fabrica)
	 * @param nomeVariavel - contém o nome da variavel
	 * @return short[1] caso retorne uma valor, ou um short[x] caso retorne um array
	 */
	public boolean getValueBool(String localizacao, String nomeVariavel) {

		String id = sfc + localizacao + "." + nomeVariavel;
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

		for (int i = 0; i < 9; i++) {
			String id = sfc + nomeVariavel + "[" + (i + 1) + "]";
			NodeId nodeIdString = new NodeId(idNode, id);
			client.readValue(0, TimestampsToReturn.Both, nodeIdString);
			try {
				valueShort[i] = (short) client.readValue(0, TimestampsToReturn.Both, nodeIdString).get().getValue()
						.getValue();
			} catch (Exception e) {
				e.printStackTrace();
				return new short[0];
			}
		}

		return valueShort;
	}

	/**
	 * Função para inserir valores booleanos (|var|CODESYS Control Win V3
	 * x64.Application.)
	 * 
	 * @param localizacao  - localizaçao da variavel (SFS ou Fabrica)
	 * @param nomeVariavel - Nome da variavel a alterar
	 * @param set          - valor da variavel a alterar (pode ser qualquer tipo)
	 * @return true se inseriu corretamente, false caso contrario
	 */
	public <E> boolean setValue(String localizacao, String nomeVariavel, E set) {
		String id = sfc + localizacao + "." + nomeVariavel;
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