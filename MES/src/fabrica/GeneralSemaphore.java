package fabrica;

import java.util.concurrent.Semaphore;

public abstract class GeneralSemaphore {
	private static Semaphore sem = new Semaphore(1);
	private static Semaphore sem2 = new Semaphore(1);

	/**Usado para a heap das ordens*/
	public static Semaphore getSem() {
		return sem;
	}
	
	/**Usado para gerir threads das ordens que estao a enviar*/
	public static Semaphore getSem2() {
		return sem2;
	}
	
}
