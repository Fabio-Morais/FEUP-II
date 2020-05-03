package fabrica;

import java.util.concurrent.Semaphore;

public class GereOrdensThread {
	private GereOrdensThread(){
		
	}

	private static boolean[] mALivre= {true,true,true};
	private static boolean[] mBLivre= {true,true,true};
	private static boolean[] mCLivre= {true,true,true};
	
	private static boolean[] mALivreSeleciona = {true, true, true};
	private static boolean[] mBLivreSeleciona = {true, true, true};
	private static boolean[] mCLivreSeleciona = {true, true, true};
	private static long[] tempoMA = {0,0,0};
	private static long[] tempoMB = {0,0,0};
	private static long[] tempoMC = {0,0,0};
	private static int numberOfThreads =0;
	
	/**Se mutex on nao deixa enviar as outras ordens*/
	protected static Semaphore sem = GeneralSemaphore.getSem2();
	
	public static synchronized  boolean[] getmALivre() {
		return mALivre;
	}
	public static synchronized void setmALivre(boolean mALivre, int pos) {
		GereOrdensThread.mALivre[pos] = mALivre;
	}
	public static synchronized boolean[] getmBLivre() {
		return mBLivre;
	}
	public static synchronized void setmBLivre(boolean mBLivre, int pos) {
		GereOrdensThread.mBLivre[pos] = mBLivre;
	}
	public static synchronized  boolean[] getmCLivre() {
		return mCLivre;
	}
	public static synchronized void setmCLivre(boolean mCLivre, int pos) {
		GereOrdensThread.mCLivre[pos] = mCLivre;
	}
	public static synchronized long[] getTempoMA() {
		return tempoMA;
	}
	public static synchronized void setTempoMA(long tempoMA, int pos) {
		GereOrdensThread.tempoMA[pos] = tempoMA;
	}
	public static synchronized long[] getTempoMB() {
		return tempoMB;
	}
	public static synchronized void setTempoMB(long tempoMB, int pos) {
		GereOrdensThread.tempoMB[pos] = tempoMB;
	}
	public static synchronized long[] getTempoMC() {
		return tempoMC;
	}
	public static synchronized void setTempoMC(long tempoMC, int pos) {
		GereOrdensThread.tempoMC[pos] = tempoMC;
	}
	public static synchronized int getNumberOfThreads() {
		return numberOfThreads;
	}
	public static synchronized void incrementNumberOfThreads() {
		GereOrdensThread.numberOfThreads++;
	}
	public static synchronized void decrementNumberOfThreads() {
		GereOrdensThread.numberOfThreads++;
	}

	
	public static boolean[] getmALivreSeleciona() {
		return mALivreSeleciona;
	}
	public static boolean[] getmBLivreSeleciona() {
		return mBLivreSeleciona;
	}
	public static boolean[] getmCLivreSeleciona() {
		return mCLivreSeleciona;
	}
	public static synchronized void setmALivreSeleciona(boolean mALivre, int pos) {
		GereOrdensThread.mALivreSeleciona[pos] = mALivre;
	}
	public static synchronized void setmBLivreSeleciona(boolean mBLivre, int pos) {
		GereOrdensThread.mBLivreSeleciona[pos] = mBLivre;
	}public static synchronized void setmCLivreSeleciona(boolean mCLivre, int pos) {
		GereOrdensThread.mCLivreSeleciona[pos] = mCLivre;
	}
}
