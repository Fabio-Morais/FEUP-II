package fabrica;

public abstract class Stock {

	private static short[] pecasStock= new short[10];
	private static boolean faltaStock;
	public static short[] getPecasStock() {
		return pecasStock;
	}
	public static void aumentaPecasStock(short pos) {
		Stock.pecasStock[pos]++;
	}
	public static void diminuiPecasStock(short pos) {
		Stock.pecasStock[pos]--;
	}
	public static boolean isFaltaStock() {
		return faltaStock;
	}
	public static void setFaltaStock(boolean faltaStock) {
		Stock.faltaStock = faltaStock;
	}
	public static void setPecasStock(short pos, short sto) {
		Stock.pecasStock[pos] = sto;
	}
	
	public static short getPecaStock(short pos) {
		return pecasStock[pos];
	}

}
