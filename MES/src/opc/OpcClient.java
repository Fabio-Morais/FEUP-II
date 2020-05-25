package opc;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

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

import db.Maquina;
import db.ZonaDescarga;
import fabrica.Fabrica;
import fabrica.GereOrdensThread;

public class OpcClient {
	private static OpcClient instance = null;
	private Fabrica fabrica;
	private OpcUaClient client;
	private int idNode = 4;
	private String sfc = "|var|CODESYS Control Win V3 x64.Application.";
	private String publicHostName;
	private UaSubscription sub = null;
	private final AtomicLong clientHandles = new AtomicLong(1L);
	private EndpointDescription[] endpoints;

	private OpcClient() {
		super();
		this.fabrica = Fabrica.getInstance();
		try {
			this.publicHostName = InetAddress.getLocalHost().getHostAddress();
			connect();
			tunningTimers();// mete os tempos nas maquinas para ela sinalizar como livre

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	public synchronized static OpcClient getInstance() {
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
	public synchronized boolean connect() {
		System.out.println(endpoints != null);
		if (endpoints != null)
			return true;

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
		try {
			this.createSubscription();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	private void tunningTimers() {
		setValue("SFS", "tunnigMA1", (long) 1200);
		setValue("SFS", "tunnigMA2", (long) 2200);
		setValue("SFS", "tunnigMA3", (long) 5500);
		setValue("SFS", "tunnigMB1", (long) 2500);
		setValue("SFS", "tunnigMB2", (long) 2800);
		setValue("SFS", "tunnigMB3", (long) 6100);
		setValue("SFS", "tunnigMC1", (long) 4300);
		setValue("SFS", "tunnigMC2", (long) 5700);
		setValue("SFS", "tunnigMC3", (long) 7700);

	}

	/** Cria subscrição para as variaveis escritas num ficheiro .txt */
	private void createSubscription() throws Exception {

		if (sub != null) {
			return;
		}
		sub = client.getSubscriptionManager().createSubscription(10.0).get();

		BiConsumer<UaMonitoredItem, Integer> onItemCreated = (item, id) -> item
				.setValueConsumer(this::onSubscriptionValue);

		List<UaMonitoredItem> items = sub
				.createMonitoredItems(TimestampsToReturn.Both, createMonitoredItemCreateRequests(), onItemCreated)
				.get();

	}

	/** Sempre que uma variavel muda de valor esta função corre */
	private void onSubscriptionValue(UaMonitoredItem item, DataValue value) {
		String aux = item.getReadValueId().getNodeId().getIdentifier().toString();
		String node = aux.substring(44, aux.length());

		if (node.substring(7, node.length()).equals("Livre")) {
			organizaFree(node, (boolean) value.getValue().getValue());
		} else if (node.substring(12, node.length()).equals("PodeLer")) {
			if ((boolean) value.getValue().getValue()) {
				short numeroOrdem = getValue("Fabrica", "AT2.pecaNoTapete.numeroOrdem")[0];
				try {
					fabrica.getHeapOrdemExecucao().get("" + numeroOrdem).pecasProduzidas();

				} catch (Exception e) {
					System.out.println("erro no opc on subscription");
				}
				setValue("SFS", "tapeteEntradaLido", true);
			}

		} else if (node.substring(13, node.length()).equals("tempoReal")) {
			organizaTempo(node, (long) value.getValue().getValue());
		} else if (node.equals("Fabrica.Pusher1.podeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String pecaDescarga = "" + this.getValue("Fabrica", "Pusher1.pecaNoTapete.tipoFinal")[0];
				String destino = "PM1";
				fabrica.mandarestatDescarga(new ZonaDescarga(destino, pecaDescarga));
				short numeroOrdem = getValue("Fabrica", "Pusher1.pecaNoTapete.numeroOrdem")[0];
				try {
					fabrica.getHeapOrdemExecucao().get("" + numeroOrdem).pecasProduzidas();

				} catch (Exception e) {
					System.out.println("erro no opc on subscription");
				}
				this.setValue("SFS", "pusher_1Lido", true);
			}
		} else if (node.equals("Fabrica.Pusher2.podeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String pecaDescarga = "" + this.getValue("Fabrica", "Pusher2.pecaNoTapete.tipoFinal")[0];
				String destino = "PM2";
				fabrica.mandarestatDescarga(new ZonaDescarga(destino, pecaDescarga));
				short numeroOrdem = getValue("Fabrica", "Pusher2.pecaNoTapete.numeroOrdem")[0];
				try {
					fabrica.getHeapOrdemExecucao().get("" + numeroOrdem).pecasProduzidas();

				} catch (Exception e) {
					System.out.println("erro no opc on subscription");
				}
				this.setValue("SFS", "pusher_2Lido", true);
			}
		}

		else if (node.equals("Fabrica.Pusher3.podeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String pecaDescarga = "" + this.getValue("Fabrica", "Pusher3.pecaNoTapete.tipoFinal")[0];
				String destino = "PM3";
				fabrica.mandarestatDescarga(new ZonaDescarga(destino, pecaDescarga));
				short numeroOrdem = getValue("Fabrica", "Pusher3.pecaNoTapete.numeroOrdem")[0];
				try {
					fabrica.getHeapOrdemExecucao().get("" + numeroOrdem).pecasProduzidas();

				} catch (Exception e) {
					System.out.println("erro no opc on subscription");
				}
				this.setValue("SFS", "pusher_3Lido", true);
			}
		} else if (node.equals("Fabrica.C1T3.PodeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String tipoPecaOperada = "P" + this.getValue("Fabrica", "C1T3.tipoPeca")[0];
				long tempo = this.getValueLong("Fabrica", "C1T3.tempo")[0];
				fabrica.mandarestatMaquina(new Maquina("MA1", tipoPecaOperada, (int) tempo));
				this.setValue("SFS", "LidoMA1", true);
			}
		} else if (node.equals("Fabrica.C1T4.PodeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String tipoPecaOperada = "P" + this.getValue("Fabrica", "C1T4.tipoPeca")[0];
				long tempo = (long) this.getValueLong("Fabrica", "C1T4.tempo")[0];
				fabrica.mandarestatMaquina(new Maquina("MB1", tipoPecaOperada, (int) tempo));
				this.setValue("SFS", "LidoMB1", true);
			}
		} else if (node.equals("Fabrica.C1T5.PodeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String tipoPecaOperada = "P" + this.getValue("Fabrica", "C1T5.tipoPeca")[0];
				long tempo = (long) this.getValueLong("Fabrica", "C1T5.tempo")[0];
				fabrica.mandarestatMaquina(new Maquina("MC1", tipoPecaOperada, (int) tempo));
				this.setValue("SFS", "LidoMC1", true);
			}
		} else if (node.equals("Fabrica.C3T3.PodeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String tipoPecaOperada = "P" + this.getValue("Fabrica", "C3T3.tipoPeca")[0];
				long tempo = (long) this.getValueLong("Fabrica", "C3T3.tempo")[0];
				fabrica.mandarestatMaquina(new Maquina("MA2", tipoPecaOperada, (int) tempo));
				this.setValue("SFS", "LidoMA2", true);
			}
		} else if (node.equals("Fabrica.C3T4.PodeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String tipoPecaOperada = "P" + this.getValue("Fabrica", "C3T4.tipoPeca")[0];
				long tempo = (long) this.getValueLong("Fabrica", "C3T4.tempo")[0];
				fabrica.mandarestatMaquina(new Maquina("MB2", tipoPecaOperada, (int) tempo));
				this.setValue("SFS", "LidoMB2", true);
			}
		} else if (node.equals("Fabrica.C3T5.PodeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String tipoPecaOperada = "P" + this.getValue("Fabrica", "C3T5.tipoPeca")[0];
				long tempo = (long) this.getValueLong("Fabrica", "C3T5.tempo")[0];
				fabrica.mandarestatMaquina(new Maquina("MC2", tipoPecaOperada, (int) tempo));
				this.setValue("SFS", "LidoMC2", true);
			}
		} else if (node.equals("Fabrica.C5T3.PodeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String tipoPecaOperada = "P" + this.getValue("Fabrica", "C5T3.tipoPeca")[0];
				short tempo = (short) this.getValueLong("Fabrica", "C5T3.tempo")[0];
				fabrica.mandarestatMaquina(new Maquina("MA3", tipoPecaOperada, (int) tempo));
				this.setValue("SFS", "LidoMA3", true);
			}
		} else if (node.equals("Fabrica.C5T4.PodeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String tipoPecaOperada = "P" + this.getValue("Fabrica", "C5T4.tipoPeca")[0];
				long tempo = (long) this.getValueLong("Fabrica", "C5T4.tempo")[0];
				fabrica.mandarestatMaquina(new Maquina("MB3", tipoPecaOperada, (int) tempo));
				this.setValue("SFS", "LidoMB3", true);
			}
		} else if (node.equals("Fabrica.C5T5.PodeLer")) {
			if ((boolean) value.getValue().getValue()) {
				String tipoPecaOperada = "P" + this.getValue("Fabrica", "C5T5.tipoPeca")[0];
				long tempo = (long) this.getValueLong("Fabrica", "C5T5.tempo")[0];
				fabrica.mandarestatMaquina(new Maquina("MC3", tipoPecaOperada, (int) tempo));
				this.setValue("SFS", "LidoMC3", true);
			} else if (node.substring(13, node.length()).equals("Step30")) {
				atualizaEstadoMaquina(node.substring(8, 12), (boolean) value.getValue().getValue());
			}
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

	private void atualizaEstadoMaquina(String maquina, boolean valor) {

		switch (maquina) {
		case "C1T3":
			GereOrdensThread.setmAEspera(valor, 0);
			break;
		case "C1T4":
			GereOrdensThread.setmBEspera(valor, 0);
			break;
		case "C1T5":
			GereOrdensThread.setmCEspera(valor, 0);
			break;
		case "C3T3":
			GereOrdensThread.setmAEspera(valor, 1);
			break;
		case "C3T4":
			GereOrdensThread.setmBEspera(valor, 1);
			break;
		case "C3T5":
			GereOrdensThread.setmCEspera(valor, 1);
			break;
		case "C5T3":
			GereOrdensThread.setmAEspera(valor, 2);
			break;
		case "C5T4":
			GereOrdensThread.setmBEspera(valor, 2);
			break;
		case "C5T5":
			GereOrdensThread.setmCEspera(valor, 2);
			break;
		}
	}

	private void organizaTempo(String node, long tempo) {
		String aux = node.substring(8, 12);
		switch (aux) {
		case "C1T3":
			GereOrdensThread.setTempoMA(tempo, 0);
			break;
		case "C1T4":
			GereOrdensThread.setTempoMB(tempo, 0);
			break;
		case "C1T5":
			GereOrdensThread.setTempoMC(tempo, 0);
			break;
		case "C3T3":
			GereOrdensThread.setTempoMA(tempo, 1);
			break;
		case "C3T4":
			GereOrdensThread.setTempoMB(tempo, 1);
			break;
		case "C3T5":
			GereOrdensThread.setTempoMC(tempo, 1);
			break;
		case "C5T3":
			GereOrdensThread.setTempoMA(tempo, 2);
			break;
		case "C5T4":
			GereOrdensThread.setTempoMB(tempo, 2);
			break;
		case "C5T5":
			GereOrdensThread.setTempoMC(tempo, 2);
			break;
		}
	}

	private void organizaFree(String node, boolean valor) {
		String aux = node.substring(4, 7);
		switch (aux) {
		case "mA1":
			GereOrdensThread.setmALivre(valor, 0);
			break;
		case "mA2":
			GereOrdensThread.setmALivre(valor, 1);
			break;
		case "mA3":
			GereOrdensThread.setmALivre(valor, 2);
			break;
		case "mB1":
			GereOrdensThread.setmBLivre(valor, 0);
			break;
		case "mB2":
			GereOrdensThread.setmBLivre(valor, 1);
			break;
		case "mB3":
			GereOrdensThread.setmBLivre(valor, 2);
			break;
		case "mC1":
			GereOrdensThread.setmCLivre(valor, 0);
			break;
		case "mC2":
			GereOrdensThread.setmCLivre(valor, 1);
			break;
		case "mC3":
			GereOrdensThread.setmCLivre(valor, 2);
			break;
		}
	}

	private int[] calculaCoords(String string) {
		String aux = string.substring(8, string.length() - ".free".length());
		int[] x = new int[2];
		int correcaoX = 0;
		/* para o caso do C7T1a OU C7T1b ...etc */
		if (aux.substring(aux.length() - 1, aux.length()).equals("b")) {
			correcaoX = 1;
		}
		if (aux.equals("AT1") || aux.equals("AT2")) {
			try {
				x[0] = 0;
				x[1] = aux.substring(aux.length() - 1, aux.length()).equals("1") ? 0 : 6;// corrige o valor, pois o Y
																							// começa em 1
			} catch (Exception e) {
				return new int[0];
			}
			return x;
		}
		try {
			x[0] = Integer.valueOf(aux.substring(1, 2)) + correcaoX;
			x[1] = Integer.valueOf(aux.substring(3, 4)) - 1;// corrige o valor, pois o Y começa em 1
		} catch (Exception e) {
			return new int[0];
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
	public synchronized short[] getValue(String localizacao, String nomeVariavel) {
		short[] valueShort = new short[1];

		String id = sfc + localizacao + "." + nomeVariavel;
		NodeId nodeIdString = new NodeId(idNode, id);
		DataValue value = null;

		/* ler para array */
		if (nomeVariavel.equals("Stock")) {
			return readToArray(id);
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

	public synchronized long[] getValueLong(String localizacao, String nomeVariavel) {
		long[] valueShort = new long[1];

		String id = sfc + localizacao + "." + nomeVariavel;
		NodeId nodeIdString = new NodeId(idNode, id);
		DataValue value = null;

		client.readValue(0, TimestampsToReturn.Both, nodeIdString);
		try {
			value = client.readValue(0, TimestampsToReturn.Both, nodeIdString).get();
		} catch (Exception e) {
			e.printStackTrace();
			return new long[0];
		}
		valueShort[0] = (long) value.getValue().getValue() / 1000;
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
	public synchronized boolean getValueBool(String localizacao, String nomeVariavel) {

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

	private short[] readToArray(String id) {
		short[] valueShort = new short[9];
		for (int i = 0; i < 9; i++) {
			String idArray = id + "[" + (i) + "]";
			NodeId nodeIdString = new NodeId(idNode, idArray);
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
	public synchronized <E> boolean setValue(String localizacao, String nomeVariavel, E set) {
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

	/**
	 * Função para ler o valor de uma variavel em especifico ARRAY de ARRAY
	 * (|var|CODESYS Control Win V3 x64.Application.)
	 * 
	 * @param localizacao - localizaçao da variavel (SFS ou Fabrica)sfc
	 * @return short[1][] caso retorne uma valor, ou um short[x] caso retorne um
	 *         array
	 */
	public synchronized short[][] getValueMatrix(String localizacao, String nomeVariavel) {
		short[][] valueShort = new short[3][3];

		String id = sfc + localizacao + "." + nomeVariavel;

		/* ler para array */
		if (nomeVariavel.equals("rebootToolPointer")) {
			return readToMatrix(id);
		} else {
			return new short[0][0];
		}

	}

	/**
	 * Função para ler o valor de uma variavel em especifico ARRAY de ARRAY de ARRAY
	 * (|var|CODESYS Control Win V3 x64.Application.)
	 * 
	 * @param localizacao  - localizaçao da variavel (SFS ou Fabrica)
	 * @param nomeVariavel - contém o nome da variavel
	 * @return short[1][] caso retorne uma valor, ou um short[x] caso retorne um
	 *         array
	 */
	public synchronized short[][][] getValueMatrix3(String localizacao, String nomeVariavel) {

		String id = sfc + localizacao + "." + nomeVariavel;

		/* ler para array */
		if (nomeVariavel.equals("bufferMachineTools")) {
			return readToMatrix3(id);
		} else {
			return new short[0][0][0];
		}

	}

	private short[][] readToMatrix(String id) {
		short[][] valueShort = new short[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				String idArray = id + "[" + (i) + "," + (j) + "]";
				NodeId nodeIdString = new NodeId(idNode, idArray);
				client.readValue(0, TimestampsToReturn.Both, nodeIdString);
				try {
					valueShort[i][j] = (short) client.readValue(0, TimestampsToReturn.Both, nodeIdString).get()
							.getValue().getValue();
				} catch (Exception e) {
					e.printStackTrace();
					return new short[0][0];
				}

			}
		}
		return valueShort;
	}

	private short[][][] readToMatrix3(String id) {
		short[][][] valueShort = new short[3][3][50];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 50; k++) {
					String idArray = id + "[" + (i) + "," + (j) + "," + (k) + "]";
					NodeId nodeIdString = new NodeId(idNode, idArray);
					client.readValue(0, TimestampsToReturn.Both, nodeIdString);
					try {
						valueShort[i][j][k] = (short) client.readValue(0, TimestampsToReturn.Both, nodeIdString).get()
								.getValue().getValue();
					} catch (Exception e) {
						e.printStackTrace();
						return new short[0][0][0];
					}
				}
			}
		}

		return valueShort;
	}
}