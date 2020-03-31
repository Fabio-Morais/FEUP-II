package fabrica;

public class Plant {
	/**0 representa caminho livre, 1 reperesenta caminho ocupado*/
	/*private int[][] map 
			= {
			{0,1,1,1,1,0,0,0,0}, 
			{1,1,0,1,0,1,0,0,1}, 
			{1,0,0,0,0,0,0,0,0},  
			{1,0,0,0,0,0,0,0,0}, 
			{1,0,0,0,0,0,0,0,0}, 
			{1,1,0,1,0,1,0,0,1}, 
			{0,0,0,0,0,0,0,0,0}
			};*/
	private int[][] map = new int [7][9];
	public Plant() {
		initializeMap();
	}
	
	/**Mudar o estado do mapa de acordo com as coordenadas (x,y)
	 * @param x - coordenada do eixo dos X
	 * @param y - coordenada do eixo dos y
	 * @param estado - 1 se estiver ocupado, 0 se estiver livre
	 * @return true se alterou com sucesso, false caso contrario
	 * */
	public boolean changeMap(int x, int y, int estado) {
		try {
			map[y][x]= estado;
		}catch(Exception e) {
			return false;
		}
		
		return true;
		
	}
	public void initializeMap() {
		for(int i=0; i<map.length; i++) {
			for(int j=0; j<map[i].length; j++) {
				map[i][j]=1;
			}
		}
	}
	
	public void printMap() {
		for(int i=0; i<map.length; i++) {
			for(int j=0; j<map[i].length; j++) {
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
	}

}
