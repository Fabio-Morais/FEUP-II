package fabrica;

import java.util.concurrent.Semaphore;

public class GereOrdensThread {
	private GereOrdensThread(){
		
	}

	private static boolean[] mALivre= {true,true,true};
	private static boolean[] mBLivre= {true,true,true};
	private static boolean[] mCLivre= {true,true,true};
	private static long[] tempoMA = {0,0,0};
	private static long[] tempoMB = {0,0,0};
	private static long[] tempoMC = {0,0,0};
	private static int numberOfThreads =0;
	
	/**Se mutex on nao deixa enviar as outras ordens*/
	protected static Semaphore sem = GeneralSemaphore.getSem2();
	
	public static boolean[] getmALivre() {
		return mALivre;
	}
	public static void setmALivre(boolean mALivre, int pos) {
		GereOrdensThread.mALivre[pos] = mALivre;
	}
	public static boolean[] getmBLivre() {
		return mBLivre;
	}
	public static void setmBLivre(boolean mBLivre, int pos) {
		GereOrdensThread.mBLivre[pos] = mBLivre;
	}
	public static boolean[] getmCLivre() {
		return mCLivre;
	}
	public static void setmCLivre(boolean mCLivre, int pos) {
		GereOrdensThread.mCLivre[pos] = mCLivre;
	}
	public static long[] getTempoMA() {
		return tempoMA;
	}
	public static void setTempoMA(long tempoMA, int pos) {
		GereOrdensThread.tempoMA[pos] = tempoMA;
	}
	public static long[] getTempoMB() {
		return tempoMB;
	}
	public static void setTempoMB(long tempoMB, int pos) {
		GereOrdensThread.tempoMB[pos] = tempoMB;
	}
	public static long[] getTempoMC() {
		return tempoMC;
	}
	public static void setTempoMC(long tempoMC, int pos) {
		GereOrdensThread.tempoMC[pos] = tempoMC;
	}
	public static int getNumberOfThreads() {
		return numberOfThreads;
	}
	public static void incrementNumberOfThreads() {
		GereOrdensThread.numberOfThreads++;
	}
	public static void decrementNumberOfThreads() {
		GereOrdensThread.numberOfThreads++;
	}
	
	
}
