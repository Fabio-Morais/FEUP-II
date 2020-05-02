package fabrica;

import java.util.concurrent.Semaphore;

public abstract class HeapSemaphore {
	private static Semaphore sem = new Semaphore(1);

	public static Semaphore getSem() {
		return sem;
	}
	
}
