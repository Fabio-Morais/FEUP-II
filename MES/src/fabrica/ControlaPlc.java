package fabrica;

import opc.OpcClient;

public class ControlaPlc {
	private final int sizeOfPath=31;

	private int tipoPeca; // tipo de peça
	private int[] recipeTime; // tempo da ferramenta
	private int[] tipoFerramenta; //tipo de ferramenta
	private int macPointer; //enviar a 0
	private int pathPointer; //enivar a 0
	private int[] pathX; 
	private int[] pathY; 
	private int pathLength; 
	public ControlaPlc() {
		// TODO Auto-generated constructor stub
	}

	/**E
	 * 
	 * */
	public void sendPath() {

		OpcClient opcClient = OpcClient.getInstance();
		short[] x = new short[sizeOfPath];
		x[0]=1;
		x[1]=2;
		x[2]=2;
		x[3]=2;
		x[4]=1;
		x[5]=2;
		x[6]=2;
		x[7]=2;
		x[8]=2;
		x[9]=2;
		x[10]=1;	
		
		short[] y = new short[sizeOfPath];
		y[0]=1;
		y[1]=1;
		y[2]=2;
		y[3]=3;
		y[4]=3;
		y[5]=3;
		y[6]=4;
		y[7]=5;
		y[8]=6;
		y[9]=7;
		y[10]=7;

		opcClient.setValue("Fabrica", "pecateste.pathX", x);
		opcClient.setValue("Fabrica", "pecateste.pathY", y);
	}
}
