package fabrica;

import java.util.concurrent.Semaphore;

public abstract class GeneralSemaphore {
	private static Semaphore sem = new Semaphore(1);
	private static Semaphore sem2 = new Semaphore(1);
	private static Semaphore sem3 = new Semaphore(1);
	private static Semaphore sem4 = new Semaphore(1);
	private static Semaphore sem5 = new Semaphore(1);
	private static Semaphore sem6 = new Semaphore(1);

	/**Usado para a heap das ordens pedentes*/
	public static Semaphore getSem() {
		return sem;
	}
	
	/**Usado para gerir threads das ordens que estao a enviar*/
	public static Semaphore getSem2() {
		return sem2;
	}

	/**Usado para as ordens, impede que aumente enquanto outra thread diminua*/
	public static Semaphore getSem3() {
		return sem3;
	}
	
	/**Usado para a heap das ordens em execuçao*/
	public static Semaphore getSem4() {
		return sem4;
	}
	

	/**Usado para o controlaPlc*/
	public static Semaphore getSem5() {
		return sem5;
	}

	/**Usado para as descargas*/
	public static Semaphore getSem6() {
		return sem6;
	}
	
}
