package fabrica;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import opc.OpcClient;

public class ControlaPlc{
	private final int sizeOfPath=31;
	private int [][][][] temposExtras = new int[3][3][7][16];
	private short [][][] machineTool = new short[3][3][50];
	private short [][] machineToolPointer = new short[3][3];
	private short [] recipeTool = new short[31];
	private short recipeToolPointer = 0;
	private short [] macProcessa = new short[31];
	private short macProcessaPointer;
	private long present_time;
	private long last_time ;

	private  PriorityQueue<S> heapS;
	private  int[][] originalMap = {
			{0, 0, 500,  0, 0,    0, 0,    0, 0,    0, 0,    0, 0,    0, 500,  0}, 
			{0, 0, 500,  0, 0,    0, 1500, 0, 1500, 0, 1500, 0, 0,    0, 500,  0}, 
			{0, 0, 1000, 1, 1500, 1, 1000, 1, 1000, 1, 1000, 1, 1500, 1, 1000, 0}, 
			{0, 0, 500,  0, 0,    0, 1500, 0, 1500, 0, 1500, 0, 0,    0, 500,  0}, 
			{0, 0, 1000, 1, 1500, 1, 1000, 1, 1000, 1, 1000, 1, 1500, 1, 1000, 0}, 
			{0, 0, 500,  0, 0,    0, 1500, 0, 1500, 0, 1500, 0, 0,    0, 500,  0}, 
			{0, 0, 1000, 1, 1500, 1, 1000, 1, 1000, 1, 1000, 1, 1500, 1, 1000, 0}, 
			};     
	
	
	public ControlaPlc() {
		this.last_time = System.currentTimeMillis();
		for(int i=0; i<3; i++) 
			for(int x=0; x<7; x++) 
				for(int y=0; y<16; y++) 
					for(int j=0; j<3; j++)
						temposExtras[i][j][x][y] = 0;
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
		if(this.heapS == null) {
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
		if((X > 6) || (X < 0) || (Y > 15) || (Y < 0)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**Corre apenas 1 vez
	 * @param ordem- Ordem a se executar
	 * */
	public synchronized void runOrder(Ordens ordem) {
		List<String> transformations = ordem.getReceita(0);
		//int numerOfPieces = ordem.getPecasPendentes();
		short tipo = Short.parseShort(""+ordem.getTransform().getFrom().charAt(1));
		short tipoFinal = Short.parseShort(""+ordem.getTransform().getTo().charAt(1));
		short numeroOrdem = Short.parseShort(ordem.getNumeroOrdem());
		
		
		
		long recipeTime[] = new long[31];
		int time_since_last_piece;
		//System.out.println(transformations);
		for(int i=0; i<transformations.size()/3; i++) {
			recipeTime[i] = 1000*Long.valueOf(transformations.get((i*3)+1)); //tempo de ferramenta
			//System.out.println("tempo: "+i+ " : "+recipeTime[i]);
		}
		//for(int i=0; i<numerOfPieces; i++) {
			short path[][] = new short [50][2];
			int path_i[][] = new int [50][2];
			present_time = System.currentTimeMillis();
			time_since_last_piece = (int) (present_time - last_time);
			last_time = present_time;
			path_i = runTransformation(transformations, time_since_last_piece);
			for(int j=0; j<path.length; j++)
				for(int k=0; k<2; k++)
					path[j][k] = (short) path_i[j][k];
			sendPath(path, recipeTool, recipeTime, tipo,tipoFinal, numeroOrdem);
			//System.out.println(Arrays.toString(recipeTool));
			/*System.out.println("---> "+ i);
		}*/
		
		short path3[][] = new short [50][2];
		short [] recipeToolTest = new short [31];
		sendPath(path3, recipeToolTest, recipeTime, (short) 0, (short) 0, (short) 0);
	}

	private void sendPath(short[][] path, short[] tool, long[] time, short tipo, short tipoFinal, short numeroOrdem) {
		//System.out.println("send new path");
		OpcClient opcClient = OpcClient.getInstance();
		boolean in;	
		do {
			in = opcClient.getValueBool("Fabrica", "freeOutput");
		}while(! in);
		
	/*	for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				for(int k=0; k<10; k++) {
					System.out.print(machineTool[j][i][k]);
				}
				System.out.print("  ");
			}
			System.out.println();
		}*/

		
		opcClient.setValue("Fabrica", "tipoPecaInput", tipo);
		opcClient.setValue("Fabrica", "pecainput.recipeTool", tool);
		opcClient.setValue("Fabrica", "pecainput.recipeTime", time);
		opcClient.setValue("Fabrica", "pecainput.pathPointer", (short) 1);
		opcClient.setValue("Fabrica", "pecainput.tipofinal", (short) tipoFinal);
		opcClient.setValue("Fabrica", "pecainput.numeroOrdem", (short) numeroOrdem);

		if(path[49][0] > 0)
			opcClient.setValue("Fabrica", "pecainput.MacProcessa", macProcessa);
		
	/*	for(int i=0;i<4; i++) {
			System.out.print(macProcessa[i] + " ");
		}
		System.out.println();*/
		
		short [] path_x = new short[sizeOfPath];
		short [] path_y = new short[sizeOfPath];
		for(int i=0;i<path[49][0]; i++) {
			path_x[i] = path[i][0];
			path_y[i] = path[i][1];
		}
		opcClient.setValue("Fabrica", "pecainput.pathX", path_x);
		opcClient.setValue("Fabrica", "pecainput.pathY", path_y);
		opcClient.setValue("Fabrica", "pecainput.pathLength", path[49][0]);
		

		opcClient.setValue("Fabrica", "bufferMachineTools", machineTool);
		
		
		do {
			in = opcClient.getValueBool("Fabrica", "freeOutput");
		}while(in);
		opcClient.setValue("Fabrica", "pecainput.pathLength", 0);
		

		//System.out.println("path sent");
	}
	
	
	private int[][] runTransformation(List<String> transformations, int timeSinceLastPiece) {
		int [] departure = new int[2];
		int [] arrival = new int[2];
		int [][] total_path = new int [50][2];
		int total_path_size = 0;
		String maquina_anterior = new String("X");
		recipeToolPointer = 0;
		macProcessaPointer = -1;
		
		departure[0] = 0;
		departure[1] = 1;
		
		
		int min_index = 0;
		
		for(int i=0; i<transformations.size(); i+=3) {
			int[][][] local_path = new int[3][50][2];
			String maquina = transformations.get(i); //tipo de maquina
			int tempo;
			tempo = 1000 * Integer.valueOf(transformations.get(i+1)); // tempo processamento 
			short tool;
			tool = Short.valueOf(transformations.get(i+2));//ferramemta
			
			// Verifica Varias Transformacoes na mesma maquina
			if((maquina == maquina_anterior) && (i>=3)) {
				// Preenche No. Operacoes na maquina
				macProcessa[macProcessaPointer] ++; 
				
				// Preenche Ferramentas Peca
				recipeTool[recipeToolPointer] = tool;
				recipeToolPointer++;
				
				// Preenche Ferramentas Maquina
				int m_x = departure[0] / 2;
				int m_y = departure[1] - 3;
				machineTool[m_x][m_y][machineToolPointer[m_x][m_y]] = tool;
				machineToolPointer[m_x][m_y] ++;
				if(machineToolPointer[m_x][m_y] > 49) machineToolPointer[m_x][m_y] = 0;
				
			}
			else {
				int min_tempo;
				min_tempo = 999999999;
				arrival[1] = ((int)(maquina.charAt(0))-62);
				local_path = new int[3][50][2];
				for (int j=0; j<3; j++) {
					arrival[0] = (j*2)+1;
	
					// adicionar tempos extras do mapa original
					for(int x=0; x<7; x++) {
						for(int y=0; y<16; y++) {
							temposExtras[arrival[1]-3][j][x][y] -= timeSinceLastPiece*0.65;
							// 0.75 funciona bem
							if(temposExtras[arrival[1]-3][j][x][y] < 0) temposExtras[arrival[1]-3][j][x][y] = 0;
							if(temposExtras[arrival[1]-3][j][x][y] > 0)
								originalMap[x][y] +=  temposExtras[arrival[1]-3][j][x][y];
							
						}
					}
				
					
					local_path[j] = findPath(arrival[0], arrival[1], departure[0], departure[1]);
					if((local_path[j][49][1] + 1500) < min_tempo) {
						min_tempo = local_path[j][49][1];
						min_index = j;
					}
					//retirar tempos extras do mapa original
					for(int x=0; x<7; x++) {
						for(int y=0; y<16; y++) {
							if(temposExtras[arrival[1]-3][j][x][y] > 0)
								originalMap[x][y] -=  temposExtras[arrival[1]-3][j][x][y];
						}
					}
					
					
				}
			
				arrival[0] = (min_index*2)+1;
				
				int local_path_size = local_path[min_index][49][0];
				
				//calculo dos tempos extras
				for(int k=0; k<local_path_size; k++) {
					//if Posição passagem
	    			if((local_path[min_index][k][1] == arrival[1]) && ((Math.abs(arrival[0] - local_path[min_index][k][0]) == 1))) {
	    				//(min_tempo-2500) aproximação do tempo que a proxima peca vai demorar a chegar lá
	    				temposExtras[arrival[1]-3][min_index][local_path[min_index][k][0]][local_path[min_index][k][1]*2] += tempo;// - (local_path[min_index][49][1]-10000);
	    			}
					
				}
				
				//(min_tempo-2500) aproximação do tempo que a proxima peca vai demorar a chegar lá
				temposExtras[arrival[1]-3][min_index][arrival[0]][arrival[1]*2] += tempo; //- (local_path[min_index][49][1]-10000);
				
				for(int j=total_path_size; j<total_path_size+local_path_size; j++) {
					total_path[j][0] = local_path[min_index][j-total_path_size][0];
					total_path[j][1] = local_path[min_index][j-total_path_size][1];	
					//RecipeTool
					if((j<total_path_size+local_path_size-1) && (j>total_path_size)) {
						if((total_path[j][1] > 2) && (total_path[j][1] < 6) && ((total_path[j][0] % 2) != 0)) {
							recipeTool[recipeToolPointer] = 0;
							recipeToolPointer++;
						}
					}else {
						if(j!= total_path_size) {
							recipeTool[recipeToolPointer] = tool;
							recipeToolPointer++;
	
							//machineTool
							int m_x = total_path[j][0] / 2;
							int m_y = total_path[j][1] - 3;
							machineTool[m_x][m_y][machineToolPointer[m_x][m_y]] = tool;
							machineToolPointer[m_x][m_y] ++;
							if(machineToolPointer[m_x][m_y] > 49) machineToolPointer[m_x][m_y] = 0;
						
						}
					}
					
					
				}
				total_path_size += local_path_size - 1;
				departure[0] =  local_path[min_index][local_path_size-1][0];
				departure[1] = local_path[min_index][local_path_size-1][1];

				macProcessaPointer++;
				macProcessa[macProcessaPointer] = 1;
			}
			maquina_anterior = maquina;
		}
		arrival[0] = 0;
		arrival[1] = 7;
		// Add costs to warehouse
		for(int x=0; x<3; x++) {
			for(int y=0; y<3; y++) {
				originalMap[(x*2)+1][(y+3)*2] +=  100000;
			}
		}
		
		int[][][] local_path = new int[3][50][2];
		
		if ((departure[0] == 5) && (departure[1] == 4)) {
			System.out.println();
		}
		
		local_path[0] = findPath(arrival[0],arrival[1], departure[0], departure[1]);
		
		for(int j=total_path_size; j<total_path_size+local_path[0][49][0]; j++) {
			total_path[j][0] = local_path[0][j-total_path_size][0];
			total_path[j][1] = local_path[0][j-total_path_size][1];
		}
		total_path_size += local_path[0][49][0];
		total_path[49][0] = total_path_size;
		// Remove costs from warehouse
		for(int x=0; x<3; x++) {
			for(int y=0; y<3; y++) {
				originalMap[(x*2)+1][(y+3)*2] -=  100000;
			}
		}	
		
		
		//TODO: Add recipeTool and machineTool
		return total_path;
	}
	
	
	private int[][] findPath(int arrival_x, int arrival_y, int departure_x, int departure_y ) {
		//usar esta funcao para ter o mapa como na fábrica
		int [][] original_path = new int[50][2];
		int[][] path = new int[50][2];
		int path_len = 0;
		
		original_path = AStar(arrival_x, arrival_y*2, departure_x, departure_y*2 );
		int len = original_path[49][0];
		int time = original_path[49][1];
		path[49][1] = time;
		
		for(int i=0; i<len; i++) {
			if(original_path[i][1] % 2 == 0) {
				path[path_len][0] = original_path[i][0];
				path[path_len][1] = original_path[i][1] / 2; 
				path_len++;
			}
		}
		path[49][0] = path_len;
		
		return path;
	}
	
	private int[][] AStar(int arrival_x, int arrival_y, int departure_x, int departure_y ) {
		int pathCells = 0;
		int[][]path = new int[50][2];
		int [] explore_node = {0,0};
		
		int [][] current_map = new int[7][16]; //tamanho igual ao this.map
		
		int [][] nn = new int[4][2];
		
		int [][] map = new int [7][16];
		for(int i=0; i< 7; i++) {
			for(int j=0; j<16; j++) {
				map[i][j] = originalMap[i][j];
			}
		}
		
		criaHeap();
		addToHeap(arrival_x, arrival_y, 1);
		
		
		boolean stay_in_loop = true;
		
		
		while(stay_in_loop) {
			
			S explore = getFromHeap();
			explore_node[0] = explore.x;
			explore_node[1] = explore.y;
			
			
			//neighbor nodes - baixo, esquerda, direita, cima
			nn[3][0] = explore_node[0];
			nn[3][1] = explore_node[1]+1;
			nn[2][0] = explore_node[0]-1;
			nn[2][1] = explore_node[1];
			nn[1][0] = explore_node[0]+1;
			nn[1][1] = explore_node[1];
			nn[0][0] = explore_node[0];
			nn[0][1] = explore_node[1]-1;
			
			for(int i=0; i<4; i++) {
				if(isValidNode(nn[i][0], nn[i][1])) {
					if(map[nn[i][0]][ nn[i][1]] != 0) {
						current_map[nn[i][0]][ nn[i][1]] = map[nn[i][0]][ nn[i][1]] + current_map[explore_node[0]][explore_node[1]];
						//Adiciona à heap
						addToHeap(nn[i][0], nn[i][1], current_map[nn[i][0]][nn[i][1]]);
						if((nn[i][0] == departure_x) && (nn[i][1] == departure_y)) {
							stay_in_loop = false;
						}
					}
				}
			}
			map[explore_node[0]][explore_node[1]] = 0;
			
			removeFromHeap(explore);	
		}
		
		
		pathCells = 0;
		explore_node[0] = departure_x;
		explore_node[1] = departure_y;
		path[pathCells][0] = explore_node[0];
		path[pathCells][1] = explore_node[1];
		pathCells = pathCells + 1;
		current_map[arrival_x][arrival_y] = 5;
		while(current_map[explore_node[0]][explore_node[1]] != 5) {
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
		    	if(isValidNode(nn[i][0], nn[i][1])) {
		    		if((current_map[nn[i][0]][nn[i][1]] > 0) && (current_map[nn[i][0]][nn[i][1]] < current_map[explore_node[0]][explore_node[1]])) {
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
		
		clearHeap();
		return path;
	}

	public void ordemDescarga(short tipo,short pusher,short numeroOrdem){// tipo P1 = 1 # pusher1 =1 
		short[][] path = new short[50][2];
		path[0][0] = (short)1;
		path[1][0] = (short)1;
		path[2][0] = (short)2;
		path[3][0] =(short) 3;
		path[4][0] =(short) 4;
		path[5][0] =(short) 5;
		path[6][0] =(short) 6;
		path[7][0] =(short) 7;
		path[8][0] =(short) 7;
		path[9][0] =(short) 7;
		
		path[0][1] = (short)1;
		path[1][1] = (short)1;
		path[2][1] = (short)1;
		path[3][1] =(short) 1;
		path[4][1] =(short) 1;
		path[5][1] =(short) 1;
		path[6][1] =(short) 1;
		path[7][1] =(short) 1;
		path[8][1] =(short) 2;
		path[9][1] =(short) 3;
		path[49][0] = (short)10;
		short[] tool= new short[50];
		tool [0] = (short)0;
		long[] time= new long[50];
		time[0] =(short)0;
		sendPath(path, tool,time, (short)2, (short)2,numeroOrdem);
		short path24[][] = new short [50][2];
		short [] recipeToolTest = new short [31];
		sendPath(path24, recipeToolTest,time, (short) 0, (short) 0,numeroOrdem);
	}
	
	public void test() {
		int [][] path = new int[50][2];
		Ordens ordem = new Ordens("123","2015", 10);
		ordem.setPecasPendentes(5);
		ordem.setTransform(ordem.new Transform("P1", "P2"));
		List<String> transformations = ordem.getReceita(0);
		
		System.out.println(transformations.toString());
		System.out.println("begin");
		runOrder(ordem);
		System.out.println("end");
		/*enviar numero de ordem*/
	}


}