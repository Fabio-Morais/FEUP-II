package fabrica;

import java.util.concurrent.Semaphore;

public class GereOrdensThread {
	private GereOrdensThread(){
		
	}

	private static boolean[] mALivre= {true,true,true};
	private static boolean[] mBLivre= {true,true,true};
	private static boolean[] mCLivre= {true,true,true};
	
	private static String[] mALivreSeleciona = {"", "", ""};
	private static String[] mBLivreSeleciona = {"", "", ""};
	private static String[] mCLivreSeleciona = {"", "", ""};
	private static long[] tempoMA = {0,0,0};
	private static long[] tempoMB = {0,0,0};
	private static long[] tempoMC = {0,0,0};
	private static int numberOfThreads =0;
	private static boolean voltaInicio= false;
	
	private static boolean maquinasOcupadas;
	
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
		GereOrdensThread.numberOfThreads--;
	}

	
	public static String[] getmALivreSeleciona() {
		return mALivreSeleciona;
	}
	public static String[] getmBLivreSeleciona() {
		return mBLivreSeleciona;
	}
	public static String[] getmCLivreSeleciona() {
		return mCLivreSeleciona;
	}
	public static boolean mALivreSeleciona() {
		return mALivreSeleciona[0].equals("") || mALivreSeleciona[1].equals("") || mALivreSeleciona[2].equals("") ;
	}
	public static boolean mBLivreSeleciona() {
		return mBLivreSeleciona[0].equals("") || mBLivreSeleciona[1].equals("") || mBLivreSeleciona[2].equals("") ;
	}
	public static boolean mCLivreSeleciona() {
		return mCLivreSeleciona[0].equals("") || mCLivreSeleciona[1].equals("") || mCLivreSeleciona[2].equals("") ;
	}
	public static synchronized void setmALivreSeleciona(String numeroOrdem, int pos) {
		GereOrdensThread.mALivreSeleciona[pos] = numeroOrdem;
	}
	public static synchronized void setmBLivreSeleciona(String numeroOrdem, int pos) {
		GereOrdensThread.mBLivreSeleciona[pos] = numeroOrdem;
	}public static synchronized void setmCLivreSeleciona(String numeroOrdem, int pos) {
		GereOrdensThread.mCLivreSeleciona[pos] = numeroOrdem;
	}
	public static boolean isVoltaInicio() {
		return voltaInicio;
	}
	public static void setVoltaInicio(boolean voltaInicio) {
		GereOrdensThread.voltaInicio = voltaInicio;
	}
	
	public static boolean isMaquinasOcupadas() {
		return maquinasOcupadas;
	}
	public static void setMaquinasOcupadas(boolean maquinasOcupadas) {
		GereOrdensThread.maquinasOcupadas = maquinasOcupadas;
	}
}
