package fabrica;

import java.util.concurrent.Semaphore;

public class GereOrdensThread {
	private GereOrdensThread(){
		
	}

	private static boolean[] mALivre= {true,true,true};
	private static boolean[] mBLivre= {true,true,true};
	private static boolean[] mCLivre= {true,true,true};
	
	private static boolean[] mALivreOpc= {true,true,true};
	private static boolean[] mBLivreOpc= {true,true,true};
	private static boolean[] mCLivreOpc= {true,true,true};
	private static long[] tempoMA = {0,0,0};
	private static long[] tempoMB = {0,0,0};
	private static long[] tempoMC = {0,0,0};
	private static int numberOfThreads =0;
	
	/**Se mutex on nao deixa enviar as outras ordens*/
	protected static Semaphore sem = GeneralSemaphore.getSem2();
	
	public synchronized static boolean[] getmALivre() {
		return mALivre;
	}
	public synchronized static void setmALivre(boolean mALivre, int pos) {
		GereOrdensThread.mALivre[pos] = mALivre;
	}
	public synchronized static boolean[] getmBLivre() {
		return mBLivre;
	}
	public synchronized static void setmBLivre(boolean mBLivre, int pos) {
		GereOrdensThread.mBLivre[pos] = mBLivre;
	}
	public synchronized static boolean[] getmCLivre() {
		return mCLivre;
	}
	public synchronized static void setmCLivre(boolean mCLivre, int pos) {
		GereOrdensThread.mCLivre[pos] = mCLivre;
	}
	public synchronized static long[] getTempoMA() {
		return tempoMA;
	}
	public synchronized static void setTempoMA(long tempoMA, int pos) {
		GereOrdensThread.tempoMA[pos] = tempoMA;
	}
	public synchronized static long[] getTempoMB() {
		return tempoMB;
	}
	public synchronized static void setTempoMB(long tempoMB, int pos) {
		GereOrdensThread.tempoMB[pos] = tempoMB;
	}
	public synchronized static long[] getTempoMC() {
		return tempoMC;
	}
	public synchronized static void setTempoMC(long tempoMC, int pos) {
		GereOrdensThread.tempoMC[pos] = tempoMC;
	}
	public synchronized static int getNumberOfThreads() {
		return numberOfThreads;
	}
	public synchronized static void incrementNumberOfThreads() {
		GereOrdensThread.numberOfThreads++;
	}
	public synchronized static void decrementNumberOfThreads() {
		GereOrdensThread.numberOfThreads++;
	}
	public synchronized static boolean[] getmALivreOpc() {
		return mALivreOpc;
	}
	public synchronized static boolean[] getmBLivreOpc() {
		return mBLivreOpc;
	}
	public synchronized static boolean[] getmCLivreOpc() {
		return mCLivreOpc;
	}
	
	public synchronized static void setmALivreOpc(boolean mALivre, int pos) {
		GereOrdensThread.mALivreOpc[pos] = mALivre;
	}
	public synchronized static void setmBLivreOpc(boolean mBLivre, int pos) {
		GereOrdensThread.mBLivreOpc[pos] = mBLivre;
	}public synchronized static void setmCLivreOpc(boolean mCLivre, int pos) {
		GereOrdensThread.mCLivreOpc[pos] = mCLivre;
	}
}
