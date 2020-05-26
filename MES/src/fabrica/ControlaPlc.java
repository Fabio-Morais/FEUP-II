package fabrica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import db.Ordem;
import dijkstra.DijkstraAlgorithm;
import dijkstra.Edge;
import dijkstra.Graph;
import dijkstra.Vertex;
import opc.OpcClient;

public class ControlaPlc {
	private final int sizeOfPath = 41;
	private int[][][][] temposExtras = new int[3][3][7][16];
	private short[][][] machineTool = new short[3][3][50];
	private short[][] machineToolPointer = new short[3][3];
	private short[] recipeTool = new short[31];
	private short recipeToolPointer = 0;
	private short[] macProcessa = new short[31];
	private short macProcessaPointer;
	private long present_time;
	private long last_time;
	private OpcClient opcClient;
	private int testeOPC = 0;

	private PriorityQueue<S> heapS;
	private int[][] originalMap = { { 0, 0, 500, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 500, 0 },
			{ 0, 0, 500, 0, 0, 0, 1500, 0, 1500, 0, 1500, 0, 0, 0, 500, 0 },
			{ 0, 0, 1000, 1, 1500, 1, 1000, 1, 1000, 1, 1000, 1, 1500, 1, 1000, 0 },
			{ 0, 0, 500, 0, 0, 0, 1500, 0, 1500, 0, 1500, 0, 0, 0, 500, 0 },
			{ 0, 0, 1000, 1, 1500, 1, 1000, 1, 1000, 1, 1000, 1, 1500, 1, 1000, 0 },
			{ 0, 0, 500, 0, 0, 0, 1500, 0, 1500, 0, 1500, 0, 0, 0, 500, 0 },
			{ 0, 0, 1000, 1, 1500, 1, 1000, 1, 1000, 1, 1000, 1, 1500, 1, 1000, 0 }, };

	private boolean speedMode;

	public ControlaPlc() {
		this.last_time = System.currentTimeMillis();
		for (int i = 0; i < 3; i++)
			for (int x = 0; x < 7; x++)
				for (int y = 0; y < 16; y++)
					for (int j = 0; j < 3; j++)
						temposExtras[i][j][x][y] = 0;

		opcClient = OpcClient.getInstance();

		machineToolPointer = opcClient.getValueMatrix("Fabrica", "rebootToolPointer");
		machineTool = opcClient.getValueMatrix3("Fabrica", "bufferMachineTools");
		opcClient.setValue("Fabrica", "syncWarOut", true);

		opcClient.setValue("Fabrica", "syncWarOut", true);
		// Add costs to warehouse
		/*
		 * for (int x = 0; x < 3; x++) { for (int y = 0; y < 3; y++) { originalMap[(x *
		 * 2) + 1][(y + 3) * 2] += 100000; } }
		 */
	}

	private void addLineAuto() {
		/* tapetes cima */
		for (int i = 0; i < 6; i++) {
			addLane("tapete", i, (i + 1), 1);
		}
		/* tapetes baixo */
		for (int i = 37; i > 31; i--) {
			addLane("tapete", i, (i - 1), 1);
		}

		/* Maquinas */
		int x = 0;
		for (int i = 10; i < 27; i++) {
			if (i != 15 && i != 21) {
				if (i % 2 == 0) {
					addLane("maquina", i, (i + 1), 15);
					addLane("maquina", (i + 1), i, 15);
				} else {
					addLane("maquina", i, (i + 1), 18);
				}

			}

		}
		/* 1º linha */
		addLane("tapete", 2, 7, 1);
		addLane("tapete", 4, 8, 1);
		addLane("tapete", 6, 9, 1);

		/* 5º linha */
		addLane("tapete", 28, 33, 1);
		addLane("tapete", 29, 35, 1);
		addLane("tapete", 30, 37, 1);

		/* 1º coluna */
		addLane("tapete", 7, 11, 1);
		addLane("tapete", 11, 17, 1);
		addLane("tapete", 17, 23, 1);
		addLane("tapete", 23, 28, 1);

		/* 2º coluna */
		addLane("tapete", 8, 13, 1);
		addLane("tapete", 13, 19, 1);
		addLane("tapete", 19, 25, 1);
		addLane("tapete", 25, 29, 1);

		/* 3º coluna */
		addLane("tapete", 9, 15, 1);
		addLane("tapete", 15, 21, 1);
		addLane("tapete", 21, 27, 1);
		addLane("tapete", 27, 30, 1);

	}

	private void addVertex() {
		int x = 0;
		int y = 1;
		for (int i = 0; i < 38; i++) {

			Vertex location = new Vertex("" + x + "," + y, "" + x + "," + y);
			nodes.add(location);

			if (i == 6 || i == 9 || i == 15 || i == 21 || i == 27 || i == 30) {
				x = 0;
				y++;
			}
			if (i == 6 || i == 7 || i == 8 || i == 27 || i == 28 || i == 29) {
				x += 2;
			} else {
				if (i != 30) {
					x++;
				}

			}

		}
	}

	/** Escolhe o nó de acordo com a string enviada */
	private int converteString(String string) {
		String aux = string.toUpperCase();
		switch (aux) {
		case "A1":
			return 10;
		case "A2":
			return 12;
		case "A3":
			return 14;
		case "B1":
			return 16;
		case "B2":
			return 18;
		case "B3":
			return 20;
		case "C1":
			return 22;
		case "C2":
			return 24;
		case "C3":
			return 26;

		case "E":
			return 0;
		case "S":
			return 31;
		default:
			return 0;
		}
	}

	/** Converte a lista de coordenadas para um short[][] para enviar via opc */
	private short[][] pathReturn(List<String> array) {
		short path[][] = new short[50][2];
		if (array.isEmpty()) {
			return new short[0][0];
		}
		for (int i = 0; i < array.size(); i++) {
			String[] coords = array.get(i).split(",");
			path[i][0] = Short.valueOf(coords[0]);
			path[i][1] = Short.valueOf(coords[1]);
		}
		path[49][0] = (short) array.size();
		return path;

	}

	static List<Vertex> nodes;
	static List<Edge> edges;

	/**
	 * Retorna a lista do path path
	 * 
	 * @param origem  - origem. E-Entrada<br>
	 *                A1-maquina A 1<br>
	 *                B2-maquina B 2<br>
	 *                S-saida
	 * @param destino - destino
	 */
	public synchronized List<String> rotaMaquinas(List<String> rota, String origem, String destino) {
		int origemInt = converteString(origem);
		int destinoInt = converteString(destino);
		nodes = new ArrayList<>();
		edges = new ArrayList<>();

		addVertex();
		addLineAuto();

		// Lets check from location Loc_1 to Loc_10
		Graph graph = new Graph(nodes, edges);
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		dijkstra.execute(nodes.get(origemInt));
		LinkedList<Vertex> path = dijkstra.getPath(nodes.get(destinoInt));
		String fim = "";
		if (path != null) {
			for (int i = 0; i < path.size(); i++) {
				for (Edge edge : edges) {
					if (i != path.size() - 1) {
						if (edge.getSource().getName().equals(path.get(i).getId())
								&& edge.getDestination().getName().equals(path.get(i + 1).getId())) {
							rota.add(path.get(i).getId());
							fim = path.get(i + 1).getId();
						}
					}
				}
			}
			rota.add(fim);
		}
		if (origemInt == destinoInt) {
			rota.add(nodes.get(origemInt).getId());
		}
		return rota;
	}

	private static void addLane(String laneId, int sourceLocNo, int destLocNo, int duration) {
		Edge lane = new Edge(laneId, nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
		edges.add(lane);

	}

	/**
	 * @return String - retorna o destino, ex: "A1"
	 */
	private String distribuiPecasParaMaquinas(List<String> rota, String origem, String destino, int pecasPendentes,
			short tool) {
		boolean[] maquinaLivre = { false, false, false };
		String returnString = "";
		int x = 0;
		int y = 0;
		int coluna = -1;
		if (origem.length() > 1)
			coluna = Integer.valueOf(origem.substring(1, 2));

		if (this.speedMode) {

			switch (destino) {
			case "C1":
				rotaMaquinas(rota, origem, destino);
				y = 2;
				x = 0;
				returnString = "C1";
				break;
			case "C2":
				rotaMaquinas(rota, origem, destino);
				y = 2;
				x = 1;
				returnString = "C2";
				break;
			case "C3":
				rotaMaquinas(rota, origem, destino);
				y = 2;
				x = 2;
				returnString = "C3";
				break;
			case "B1":
				rotaMaquinas(rota, origem, destino);
				y = 1;
				x = 0;
				returnString = "B1";
				break;
			case "B2":
				rotaMaquinas(rota, origem, destino);
				y = 1;
				x = 1;
				returnString = "B2";
				break;
			case "B3":
				rotaMaquinas(rota, origem, destino);
				y = 1;
				x = 2;
				returnString = "B3";
				break;
			case "A1":
				rotaMaquinas(rota, origem, destino);
				y = 0;
				x = 0;
				returnString = "A1";
				break;
			case "A2":
				rotaMaquinas(rota, origem, destino);
				y = 0;
				x = 1;
				returnString = "A2";
				break;
			case "A3":
				rotaMaquinas(rota, origem, destino);
				y = 0;
				x = 2;
				returnString = "A3";
				break;
			}
		} else {
			if (destino.equals("A")) {
				maquinaLivre = GereOrdensThread.getmALivre();
			} else if (destino.equals("B")) {
				maquinaLivre = GereOrdensThread.getmBLivre();
				y = 1;
			} else if (destino.equals("C")) {
				maquinaLivre = GereOrdensThread.getmCLivre();
				y = 2;
			}


			if (maquinaLivre[2]&& (coluna == -1 || coluna == 3)) {
				rotaMaquinas(rota, origem, destino + "3");
				returnString = destino + "3";
				x = 2;
			} else if (maquinaLivre[1] && (coluna == -1 || coluna == 2)) {
				rotaMaquinas(rota, origem, destino + "2");
				returnString = destino + "2";
				x = 1;
			} else if (maquinaLivre[0] && (coluna == -1 || coluna == 1)) {
				rotaMaquinas(rota, origem, destino + "1");
				returnString = destino + "1";
				x = 0;
			} else if ((coluna == -1 || coluna == 3)) {
				rotaMaquinas(rota, origem, destino + "3");
				returnString = destino + "3";
				x = 2;
			} else if ((coluna == -1 || coluna == 2)) {
				rotaMaquinas(rota, origem, destino + "2");
				returnString = destino + "2";
				x = 1;
			} else if ((coluna == -1 || coluna == 1)) {
				rotaMaquinas(rota, origem, destino + "1");
				returnString = destino + "1";
				x = 0;

			}

		}

		/*
		 * System.out.println(); System.out.println("machineTool[" + x + "][" + y + "]["
		 * + machineToolPointer[x][y] + "] = " + tool); System.out.println();
		 * System.out.println("machineTool[" + x + "][" + y + "][" +
		 * machineToolPointer[x][y] + "] = " + tool);
		 */

		machineTool[x][y][machineToolPointer[x][y]] = tool;// pointer = 0-> 1, pointer = 1 -> 2
		machineToolPointer[x][y]++;
		if (machineToolPointer[x][y] > 49) {
			machineToolPointer[x][y] = 0;
		}

		return returnString;
	}

	private short[][] calculaRota(List<String> maquinas, int pecasPendentes, short[] tool) {
		List<String> rota = new ArrayList<>();

		String origem = "E";
		String destino = "";

		if (this.speedMode) {
			for (int i = 0; i < maquinas.size(); i++) {
				destino = maquinas.get(i).toUpperCase() + (i + 1);
				// System.out.println(destino);
				origem = distribuiPecasParaMaquinas(rota, origem, destino, pecasPendentes, tool[i]);
				// System.out.println(origem);
				/* se houver seguinte */
				if (rota.size() > 0 && (i + 1) < maquinas.size()) {
					rota.remove(rota.size() - 1);
				}

			}
		} else {
			for (int i = 0; i < maquinas.size(); i++) {
				destino = maquinas.get(i).toUpperCase();
				origem = distribuiPecasParaMaquinas(rota, origem, destino, pecasPendentes, tool[i]);
				/* se houver seguinte */
				if (rota.size() > 0 && (i + 1) < maquinas.size()) {
					rota.remove(rota.size() - 1);
				}

			}
		}

		if (!rota.isEmpty()) {
			rota.remove(rota.size() - 1);
			rotaMaquinas(rota, origem, "S");
		}
		// System.out.println("\n\n");
		return pathReturn(rota);

	}

	private List<String> speed(Ordens ordem) {
		for (int i = 0; i < 23; i += 11) {
			int c = (i == 22) ? 15 : 0;
			int x = (i == 22) ? 15 : i;
			String receita = ordem.getReceita(x, c).get(0);
			// System.out.println("********** receita: "+receita);
			boolean[] aux = { false, false, false };
			String mALivre = GereOrdensThread.getmALivreSeleciona()[0];
			String mBLivre = GereOrdensThread.getmBLivreSeleciona()[0];
			String mCLivre = GereOrdensThread.getmCLivreSeleciona()[0];

			if (receita.equals("A") && mALivre.length() > 1
					&& ordem.getNumeroOrdem().equals(mALivre.substring(1, mALivre.length()))) {
				aux = GereOrdensThread.getmALivre();
			} else if (receita.equals("B") && mBLivre.length() > 1
					&& ordem.getNumeroOrdem().equals(mBLivre.substring(1, mBLivre.length()))) {
				aux = GereOrdensThread.getmBLivre();
			} else if (receita.equals("C") && mCLivre.length() > 1
					&& ordem.getNumeroOrdem().equals(mCLivre.substring(1, mCLivre.length()))) {
				aux = GereOrdensThread.getmCLivre();
			}
			if (aux[0]) {
				tempoC = x;
				tempoA = c;
				return ordem.getReceita(i, c);
			}
		}
		return ordem.getReceita(0, 0);
	}

	private int tempoC;
	private int tempoA;

	/**
	 * Corre apenas 1 vez
	 * 
	 * @param ordem- Ordem a se executar
	 */
	public synchronized boolean runOrder(Ordens ordem) {
		int smallest = 0;
		this.speedMode = ordem.isSpeedMode();
		List<String> transformations = ordem.getReceita(smallest, 0);// lista de transformaçoes
		if (this.speedMode) {
			transformations = speed(ordem);
		}
		short tipo = Short.parseShort("" + ordem.getTransform().getFrom().charAt(1));// peça inicial
		short tipoFinal = Short.parseShort("" + ordem.getTransform().getTo().charAt(1));// peça final
		short numeroOrdem = Short.parseShort(ordem.getNumeroOrdem()); // numero de ordem
		List<String> maquinas = new ArrayList<>();
		int auxIndice = -1;
		String pre = "";
		for (int i = 0; i < macProcessa.length; i++) {
			macProcessa[i] = 0;
		}
		if (!ordem.isSpeedMode()) {
			for (int i = 0; i < transformations.size(); i += 3) {
				maquinas.add(transformations.get(i));
				if (!transformations.get(i).equals(pre)) {
					macProcessa[++auxIndice]++;
					pre = transformations.get(i);
				} else {
					macProcessa[auxIndice]++;
				}
			}
		} else {
			for (int i = 0; i < transformations.size(); i += 3) {
				maquinas.add(transformations.get(i));
				macProcessa[++auxIndice]++;
			}
		}

		long recipeTime[] = new long[31];
		for (int i = 0; i < transformations.size() / 3; i++) {
			recipeTime[i] = 1000 * Long.valueOf(transformations.get((i * 3) + 1)); // tempo de ferramenta
		}
		int i = 0;
		short[] tool = new short[31];
		for (int j = 2; j < transformations.size(); j += 3) {
			tool[i++] = Short.valueOf(transformations.get(j));// [1,2,0,0,0]-> [1,1,0]
		}

		short[][] path = calculaRota(maquinas, ordem.getPecasPendentes(), tool);
		if (path.length == 0) {
			return false;
		}
		i = 0;
		short[] pecas = new short[31];
		for (String aux : ordem.getListaPecas(tempoC, tempoA)) {
			pecas[i++] = Short.parseShort("" + aux.charAt(1));
		}

		// recipeTool => lsita de ferramentas
		sendPath(path, tool, recipeTime, tipo, tipoFinal, numeroOrdem, pecas);
		return true;

	}

	private synchronized void sendPath(short[][] path, short[] tool, long[] time, short tipo, short tipoFinal,
			short numeroOrdem, short[] listaPecas) {
		
		//opcClient.setValue("Fabrica", "syncWarOut", false);
		boolean in;
		do {
			in = opcClient.getValueBool("Fabrica", "freeOutput");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!in);

		opcClient.setValue("Fabrica", "syncWarOut", false);

		opcClient.setValue("Fabrica", "tipoPecaInput", tipo);
		opcClient.setValue("Fabrica", "pecainput.recipeTool", tool);
		opcClient.setValue("Fabrica", "pecainput.recipeTime", time);
		opcClient.setValue("Fabrica", "pecainput.pathPointer", (short) 1);
		opcClient.setValue("Fabrica", "pecainput.tipofinal", (short) tipoFinal);
		opcClient.setValue("Fabrica", "pecainput.numeroOrdem", (short) numeroOrdem);
		opcClient.setValue("Fabrica", "pecainput.pecasEtapas", listaPecas);

		/*
		 * System.out.println("tool "+Arrays.toString(tool));
		 * System.out.println("time "+Arrays.toString(time));
		 * System.out.println("length: "+path[49][0]+ "mac->"+
		 * Arrays.toString(macProcessa)); System.out.println();
		 */
		if (path[49][0] > 0)
			opcClient.setValue("Fabrica", "pecainput.MacProcessa", macProcessa);

		short[] path_x = new short[sizeOfPath];
		short[] path_y = new short[sizeOfPath];
		for (int i = 0; i < path[49][0]; i++) {
			path_x[i] = path[i][0];
			path_y[i] = path[i][1];
		}
		// 3,3,50
		opcClient.setValue("Fabrica", "bufferMachineTools", machineTool);
		opcClient.setValue("Fabrica", "rebootToolPointer", machineToolPointer);
		opcClient.setValue("Fabrica", "pecainput.pathX", path_x);
		opcClient.setValue("Fabrica", "pecainput.pathY", path_y);
		opcClient.setValue("Fabrica", "pecainput.pathLength", path[49][0]);
		
		testeOPC++;

		
		do {
			in = opcClient.getValueBool("Fabrica", "sentOutput");
		} while (!in);
		opcClient.setValue("Fabrica", "pecainput.pathLength", (short) 0);
		opcClient.setValue("Fabrica", "tipoPecaInput", (short) 0);
		opcClient.setValue("Fabrica", "syncWarOut", true);

	}

	// warehouseOut: X = 0, Y = 1
	// warehouseIn: X = 0, Y = 7
	private class S {
		private int dist;
		private int x;
		private int y;

		public int getDist() {
			return dist;
		}

		public S(int dist, int x, int y) {
			this.dist = dist;
			this.x = x;
			this.y = y;
		}
	}

	private void addToHeap(S S) {
		heapS.add(S);
	}

	private void removeFromHeap(S node) {
		this.heapS.remove(node);
	}

	private S getFromHeap() {
		return this.heapS.peek();
	}

	private void clearHeap() {
		heapS.clear();
	}

	private void addToHeap(int arrival_x, int arrival_y, int dist) {
		S node = new S(dist, arrival_x, arrival_y);
		addToHeap(node);
	}

	private void criaHeap() {
		if (this.heapS == null) {
			Comparator<S> result = new Comparator<S>() {

				@Override
				public int compare(S arg0, S arg1) {
					Integer x = arg0.getDist();
					Integer y = arg1.getDist();
					return x.compareTo(y);
				}
			};

			this.heapS = new PriorityQueue<>(result);
		}
	}

	private boolean isValidNode(int X, int Y) {
		if ((X > 6) || (X < 0) || (Y > 15) || (Y < 0)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Corre apenas 1 vez
	 * 
	 * @param ordem- Ordem a se executar
	 */
	public synchronized boolean runOrder2(Ordens ordem) {
		int smallest = 0;
		if (ordem.getTransform() != null) {
			if (ordem.getTransform().getFrom().equals("P1") && ordem.getTransform().getTo().equals("P9")) {
				long[] auxTempo = GereOrdensThread.getTempoMC();
				if (auxTempo[0] <= auxTempo[1] && auxTempo[0] <= auxTempo[2]) {
					smallest = (int) auxTempo[0] / 1000;
				} else if (auxTempo[1] <= auxTempo[2] && auxTempo[1] <= auxTempo[0]) {
					smallest = (int) auxTempo[1] / 1000;
				} else {
					smallest = (int) auxTempo[2] / 1000;
				}
			}
		}

		List<String> transformations = ordem.getReceita(smallest, 0);
		// int numerOfPieces = ordem.getPecasPendentes();
		short tipo = Short.parseShort("" + ordem.getTransform().getFrom().charAt(1));
		short tipoFinal = Short.parseShort("" + ordem.getTransform().getTo().charAt(1));
		short numeroOrdem = Short.parseShort(ordem.getNumeroOrdem());

		long recipeTime[] = new long[31];
		int time_since_last_piece;
		for (int i = 0; i < transformations.size() / 3; i++) {
			recipeTime[i] = 1000 * Long.valueOf(transformations.get((i * 3) + 1)); // tempo de ferramenta
		}
		short path[][] = new short[50][2];
		int path_i[][] = new int[50][2];
		present_time = System.currentTimeMillis();
		time_since_last_piece = (int) (present_time - last_time);
		path_i = runTransformation(transformations, time_since_last_piece);
		last_time = present_time;
		for (int j = 0; j < path.length; j++) {
			for (int k = 0; k < 2; k++) {
				path[j][k] = (short) path_i[j][k];
			}
		}
		short[] x = new short[31];
		int i = 0;
		for (String aux : ordem.getListaPecas(0, 0)) {
			x[i++] = Short.parseShort("" + aux.charAt(1));
		}
		short[] tool = new short[31];
		for (int j = 2; j < transformations.size(); j += 3) {
			tool[i++] = Short.valueOf(transformations.get(j));
		}
		System.out.println("recipe " + Arrays.toString(recipeTool));
		System.out.println("toll " + Arrays.toString(tool));
		sendPath(path, recipeTool, recipeTime, tipo, tipoFinal, numeroOrdem, x);
		return true;
	}

	public synchronized void runOrdemDescarga(Ordens ordem) {// tipo P1 = 1 # pusher1 =1
		short tipo = Short.parseShort("" + ordem.getUnload().getType().charAt(1));
		short pusher = Short.parseShort("" + ordem.getUnload().getDestinantion().charAt(2));
		short numeroOrdem = Short.parseShort(ordem.getNumeroOrdem());
		short[][] path = new short[50][2];
		path[0][0] = (short) 1;
		path[1][0] = (short) 1;
		path[2][0] = (short) 2;
		path[3][0] = (short) 3;
		path[4][0] = (short) 4;
		path[5][0] = (short) 5;
		path[6][0] = (short) 6;
		path[7][0] = (short) 7;
		path[8][0] = (short) 7;
		path[9][0] = (short) 7;

		path[0][1] = (short) 1;
		path[1][1] = (short) 1;
		path[2][1] = (short) 1;
		path[3][1] = (short) 1;
		path[4][1] = (short) 1;
		path[5][1] = (short) 1;
		path[6][1] = (short) 1;
		path[7][1] = (short) 1;
		path[8][1] = (short) 2;
		path[9][1] = (short) 3;

		if (pusher == 1) {
			path[49][0] = (short) 10;
		}
		if (pusher == 2) {
			path[10][0] = (short) 7;
			path[10][1] = (short) 4;
			path[49][0] = (short) 11;
		}
		if (pusher == 3) {
			path[10][0] = (short) 7;
			path[10][1] = (short) 4;
			path[11][0] = (short) 7;
			path[11][1] = (short) 5;
			path[49][0] = (short) 12;
		}
		short[] tool = new short[50];
		tool[0] = (short) 0;
		long[] time = new long[50];
		time[0] = (short) 0;
		sendPath(path, tool, time, tipo, tipo, numeroOrdem, new short[31]);

	}

	private int[][] runTransformation(List<String> transformations, int time_since_last_piece) {
		int[] departure = new int[2];
		int[] arrival = new int[2];
		int[][] total_path = new int[50][2];
		int total_path_size = 0;
		String maquina_anterior = "X";
		recipeToolPointer = 0;
		macProcessaPointer = -1;

		departure[0] = 0;
		departure[1] = 1;

		int min_index = 0;

		// Update TemposExtras
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 16; y++) {
				for (int k = 0; k < 3; k++) {
					for (int j = 0; j < 3; j++) {
						temposExtras[k][j][x][y] -= time_since_last_piece * 0.9;
						// 0.75 funciona bem - 0.65
						if (temposExtras[k][j][x][y] < 0)
							temposExtras[k][j][x][y] = 0;
					}
				}
			}
		}

		for (int i = 0; i < transformations.size(); i += 3) {
			int[][][] local_path = new int[3][50][2];
			String maquina = transformations.get(i);
			int tempo;
			tempo = 1000 * Integer.valueOf(transformations.get(i + 1));
			short tool;
			tool = Short.valueOf(transformations.get(i + 2));
			// Verifica Varias Transformacoes na mesma maquina
			if ((maquina.equals(maquina_anterior)) && (i >= 3)) {
				// Preenche No. Operacoes na maquina
				macProcessa[macProcessaPointer]++;

				// Preenche Ferramentas Peca
				recipeTool[recipeToolPointer] = tool;
				recipeToolPointer++;

				// Preenche Ferramentas Maquina
				int m_x = departure[0] / 2;
				int m_y = departure[1] - 3;
				System.out.println("---1g----");
				System.out.println("m_x= " + m_x + "m_y= " + m_y);
				System.out.println("pointer " + machineToolPointer[m_x][m_y]);
				System.out.println("tool " + tool);
				machineTool[m_x][m_y][machineToolPointer[m_x][m_y]] = tool;
				System.out.println(
						"machineTool[" + m_x + "][" + m_y + "][" + machineToolPointer[m_x][m_y] + "] = " + tool);

				machineToolPointer[m_x][m_y]++;
				if (machineToolPointer[m_x][m_y] > 49)
					machineToolPointer[m_x][m_y] = 0;

				// Atualiza Tempos Extras c/ mudanca Ferramenta: 20s
				temposExtras[arrival[1] - 3][min_index][arrival[0]][arrival[1] * 2] += (tempo + 20000);

			} else {
				int min_tempo;
				min_tempo = 999999999;
				arrival[1] = ((int) (maquina.charAt(0)) - 62);
				local_path = new int[3][50][2];

				for (int j = 0; j < 3; j++) {
					arrival[0] = (j * 2) + 1;

					for (int x = 0; x < 7; x++) {
						for (int y = 0; y < 16; y++) {
							if (temposExtras[arrival[1] - 3][j][x][y] > 0)
								originalMap[x][y] += temposExtras[arrival[1] - 3][j][x][y];

						}
					}

					if (j == 0) {
						for (int x = 0; x < 7; x++) {
							for (int y = 0; y < 16; y++) {
								int aux = 0;
								for (int theta = 0; theta < 3; theta++) {
									for (int omega = 0; omega < 3; omega++) {
										aux = aux + temposExtras[theta][omega][x][y];
									}
								}
							}
						}

					}

					// parte que funciona!!
					local_path[j] = findPath(arrival[0], arrival[1], departure[0], departure[1]);
					// if ((local_path[j][49][1] + 1500) < min_tempo) {
					if ((local_path[j][49][1] + 1500) < min_tempo) {
						min_tempo = local_path[j][49][1];
						min_index = j;
					}
					// retirar tempos extras do mapa original
					for (int x = 0; x < 7; x++) {
						for (int y = 0; y < 16; y++) {
							if (temposExtras[arrival[1] - 3][j][x][y] > 0)
								originalMap[x][y] -= temposExtras[arrival[1] - 3][j][x][y];
						}
					}

				}

				arrival[0] = (min_index * 2) + 1;

				int local_path_size = local_path[min_index][49][0];

				// (min_tempo-2500) aproximação do tempo que a proxima peca vai demorar a chegar
				// lá
				temposExtras[arrival[1] - 3][min_index][arrival[0]][arrival[1] * 2] += (tempo + 6500); // -
				// (local_path[min_index][49][1]-10000);

				for (int j = total_path_size; j < total_path_size + local_path_size; j++) {
					total_path[j][0] = local_path[min_index][j - total_path_size][0];
					total_path[j][1] = local_path[min_index][j - total_path_size][1];
					// RecipeTool
					if ((j < total_path_size + local_path_size - 1) && (j > total_path_size)) {
						if ((total_path[j][1] > 2) && (total_path[j][1] < 6) && ((total_path[j][0] % 2) != 0)) {
							recipeTool[recipeToolPointer] = 0;
							recipeToolPointer++;
						}
					} else {
						if (j != total_path_size) {
							recipeTool[recipeToolPointer] = tool;
							recipeToolPointer++;

							// machineTool
							int m_x = total_path[j][0] / 2;
							int m_y = total_path[j][1] - 3;
							System.out.println("---2----");
							System.out.println("m_x= " + m_x + "m_y= " + m_y);
							System.out.println("pointer " + machineToolPointer[m_x][m_y]);
							System.out.println("tool " + tool);
							System.out.println("machineTool[" + m_x + "][" + m_y + "][" + machineToolPointer[m_x][m_y]
									+ "] = " + tool);

							machineTool[m_x][m_y][machineToolPointer[m_x][m_y]] = tool;
							machineToolPointer[m_x][m_y]++;
							if (machineToolPointer[m_x][m_y] > 49)
								machineToolPointer[m_x][m_y] = 0;

						}
					}

				}
				total_path_size += local_path_size - 1;
				departure[0] = local_path[min_index][local_path_size - 1][0];
				departure[1] = local_path[min_index][local_path_size - 1][1];

				macProcessaPointer++;
				macProcessa[macProcessaPointer] = 1;
			}
			maquina_anterior = maquina;
		}

		arrival[0] = 0;
		arrival[1] = 7;

		// Add costs to warehouse
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				originalMap[(x * 2) + 1][(y + 3) * 2] += 100000;
			}
		}

		int[][][] local_path = new int[3][50][2];

		local_path[0] = findPath(arrival[0], arrival[1], departure[0], departure[1]);

		for (int j = total_path_size; j < total_path_size + local_path[0][49][0]; j++) {
			total_path[j][0] = local_path[0][j - total_path_size][0];
			total_path[j][1] = local_path[0][j - total_path_size][1];
		}
		total_path_size += local_path[0][49][0];
		total_path[49][0] = total_path_size;
		// Remove costs from warehouse
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				originalMap[(x * 2) + 1][(y + 3) * 2] -= 100000;
			}
		}

		// TODO: Add recipeTool and machineTool
		return total_path;
	}

	private int[][] findPath(int arrival_x, int arrival_y, int departure_x, int departure_y) {
		// usar esta funcao para ter o mapa como na fábrica
		int[][] original_path = new int[50][2];
		int[][] path = new int[50][2];
		int path_len = 0;

		original_path = AStar(arrival_x, arrival_y * 2, departure_x, departure_y * 2);
		int len = original_path[49][0];
		int time = original_path[49][1];
		path[49][1] = time;

		for (int i = 0; i < len; i++) {
			if (original_path[i][1] % 2 == 0) {
				path[path_len][0] = original_path[i][0];
				path[path_len][1] = original_path[i][1] / 2;
				path_len++;
			}
		}
		path[49][0] = path_len;

		return path;
	}

	private int[][] AStar(int arrival_x, int arrival_y, int departure_x, int departure_y) {
		int pathCells = 0;
		int[][] path = new int[50][2];
		int[] explore_node = { 0, 0 };

		int[][] current_map = new int[7][16]; // tamanho igual ao this.map

		int[][] nn = new int[4][2];

		int[][] map = new int[7][16];
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 16; j++) {
				map[i][j] = originalMap[i][j];
			}
		}

		criaHeap();
		addToHeap(arrival_x, arrival_y, 1);

		boolean stay_in_loop = true;

		while (stay_in_loop) {

			S explore = getFromHeap();
			explore_node[0] = explore.x;
			explore_node[1] = explore.y;

			// neighbor nodes - baixo, esquerda, direita, cima
			nn[3][0] = explore_node[0];
			nn[3][1] = explore_node[1] + 1;
			nn[2][0] = explore_node[0] - 1;
			nn[2][1] = explore_node[1];
			nn[1][0] = explore_node[0] + 1;
			nn[1][1] = explore_node[1];
			nn[0][0] = explore_node[0];
			nn[0][1] = explore_node[1] - 1;

			for (int i = 0; i < 4; i++) {
				if (isValidNode(nn[i][0], nn[i][1])) {
					if (map[nn[i][0]][nn[i][1]] != 0) {
						current_map[nn[i][0]][nn[i][1]] = map[nn[i][0]][nn[i][1]]
								+ current_map[explore_node[0]][explore_node[1]];
						// Adiciona à heap
						addToHeap(nn[i][0], nn[i][1], current_map[nn[i][0]][nn[i][1]]);
						if ((nn[i][0] == departure_x) && (nn[i][1] == departure_y)) {
							stay_in_loop = false;
						}
					}
				}
			}
			map[explore_node[0]][explore_node[1]] = 0;

			// S.remove[explore_node];
			removeFromHeap(explore);
		}

		pathCells = 0;
		explore_node[0] = departure_x;
		explore_node[1] = departure_y;
		path[pathCells][0] = explore_node[0];
		path[pathCells][1] = explore_node[1];
		pathCells = pathCells + 1;
		current_map[arrival_x][arrival_y] = 5;
		while (current_map[explore_node[0]][explore_node[1]] != 5) {
			// 4- Conn : 0 - up; 1 - down; 2 - left; 3 - right
			nn[0][0] = explore_node[0];
			nn[0][1] = explore_node[1] - 1;
			nn[1][0] = explore_node[0];
			nn[1][1] = explore_node[1] + 1;
			nn[2][0] = explore_node[0] - 1;
			nn[2][1] = explore_node[1];
			nn[3][0] = explore_node[0] + 1;
			nn[3][1] = explore_node[1];

			for (int i = 0; i < 4; i++) {
				if (isValidNode(nn[i][0], nn[i][1])) {
					if ((current_map[nn[i][0]][nn[i][1]] > 0)
							&& (current_map[nn[i][0]][nn[i][1]] < current_map[explore_node[0]][explore_node[1]])) {
						// Add X (nn[i][0]) to Path
						// Add Y (nn[i][1]) to Path

						explore_node[0] = nn[i][0];
						explore_node[1] = nn[i][1];
						path[pathCells][0] = nn[i][0];
						path[pathCells][1] = nn[i][1];
						pathCells = pathCells + 1;
					}
				}
			}

		}

		path[49][0] = pathCells;
		path[49][1] = current_map[departure_x][departure_y] + originalMap[arrival_x][arrival_y];
		// path[49][1] = current_map[departure_x][departure_y];

		clearHeap();
		return path;
	}

	public void test() {

		int[][] path = new int[50][2];
		Fabrica fabrica = Fabrica.getInstance();
		Ordens ordem1 = new Ordens("1", 500, Ordem.localDate(), 500, fabrica);
		ordem1.setPecasPendentes(2);
		ordem1.setTransform(ordem1.new Transform("P3", "P5"));// maquina A
		this.speedMode = true;
		ArrayList<String> maquinas = new ArrayList<>();
		maquinas.add("C");
		maquinas.add("C");
		maquinas.add("C");
		// runOrder(ordem1);

		calculaRota(maquinas, 2, new short[] { 1, 2, 3 });

		/* enviar numero de ordem */
	}

}