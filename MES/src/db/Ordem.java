package db;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import fabrica.Ordens;

public class Ordem {
	private final static String dateFormat = "MM/dd/yyyy HH:mm:ss";

	private String numeroOrdem;
	private String horaEntradaOrdem;
	private int pecasPendentes;
	private int folga;
	
	public Ordem() {

	}

	public Ordem(String numeroOrdem, int pecasPendentes, int folga) {
		this.horaEntradaOrdem = Ordem.localDate(); // (MM/dd/yyyy HH:mm:ss)
		this.numeroOrdem = numeroOrdem;
		this.pecasPendentes = pecasPendentes;
		this.folga=folga;
	}

	/**
	 * Funçao usada para saber Data e hora atual em formato (MM/dd/yyyy HH:mm:ss)
	 * 
	 * @return DataHora String com data e hora
	 */
	public static String localDate() {
		Date date = new java.util.Date();
		Timestamp timestamp = new java.sql.Timestamp(date.getTime());
		return new SimpleDateFormat(dateFormat).format(timestamp);

	}

	/**
	 * Funçao para converter do formato (MM/dd/yyyy HH:mm:ss) para (dd/MM/yyyy
	 * HH:mm:ss)
	 * 
	 * @param firstDateString - Data em formato de String (MM/dd/yyyy HH:mm:ss)
	 * @return Data em string
	 */
	public static String converteData(String firstDateString) {
		final String OLD_FORMAT = dateFormat;
		final String NEW_FORMAT = "dd/MM/yyyy HH:mm:ss";

		// August 12, 2010
		String oldDateString = firstDateString;
		String newDateString;

		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date d = null;
		try {
			d = sdf.parse(oldDateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sdf.applyPattern(NEW_FORMAT);
		newDateString = sdf.format(d);

		return newDateString;
	}

	/**
	 * Funçao para converter do formato (yyyy/MM/dd HH:mm:ss) para (MM/dd/yyyy
	 * HH:mm:ss)
	 * 
	 * @param firstDateString - Data em formato de String (yyyy/MM/dd HH:mm:ss)
	 * @return Data em string
	 */
	public static String converteData2(String firstDateString) {
		final String OLD_FORMAT = "yyyy-MM-dd HH:mm:ss";
		final String NEW_FORMAT = "MM/dd/yyyy HH:mm:ss";
		// August 12, 2010
		String oldDateString = firstDateString;
		String newDateString;

		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date d = null;
		try {
			d = sdf.parse(oldDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sdf.applyPattern(NEW_FORMAT);
		newDateString = sdf.format(d);

		return newDateString;
	}

	/**
	 * Funçao para converter do formato (dd/MM/yyyy HH:mm:ss) para (MM/dd/yyyy
	 * HH:mm:ss)
	 * 
	 * @param firstDateString - Data em formato de String (dd/MM/yyyy HH:mm:ss)
	 * @return Data em string
	 */
	public static String converteData3(String firstDateString) {
		final String OLD_FORMAT = "dd/MM/yy HH:mm:ss";
		final String NEW_FORMAT = "MM/dd/yyyy HH:mm:ss";
		// August 12, 2010
		String oldDateString = firstDateString;
		String newDateString;

		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date d = null;
		try {
			d = sdf.parse(oldDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sdf.applyPattern(NEW_FORMAT);
		newDateString = sdf.format(d);

		return newDateString;
	}

	/**
	 * Funçao usada para saber a diferença entre 2 datas
	 * 
	 * @param firstDateString  - Data em formato de String (dd/MM/yyyy HH:mm:ss)
	 * @param secondDateString - Data em formato de String (dd/MM/yyyy HH:mm:ss)
	 * @return retorna a diferença em segundos
	 */
	public static int calculaDiferenca(String firstDateString, String secondDateString) {
		final String OLD_FORMAT = "dd/MM/yyyy HH:mm:ss";
		final String NEW_FORMAT = "MM/dd/yyyy HH:mm:ss";

		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date firstDate = null;
		Date secondDate = null;

		try {
			firstDate = sdf.parse(firstDateString);
			secondDate = sdf.parse(secondDateString);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		sdf.applyPattern(NEW_FORMAT);

		if (firstDate.compareTo(secondDate) == -1)
			return 0;

		int diff = -1;
		int diffInMillies = (int) Math.abs(secondDate.getTime() - firstDate.getTime());
		diff = (int) TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

		return diff;
	}

	/**
	 * Funçao usada para saber o tempo restante
	 * 
	 * @param date         - Data que a ordem foi enviada em formato de String
	 *                     (MM/dd/yyyy HH:mm:ss)
	 * @param atrasoMaximo - atraso maximo da ordem
	 * @return retorna a diferença em segundos
	 */
	public static long calculaTempoRestante(String date, int atrasoMaximo) {

		String dataLimite = addDate(date, atrasoMaximo);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date firstDate = null;
		Date secondDate = null;

		try {
			firstDate = sdf.parse(dataLimite);
			secondDate = sdf.parse(localDate());

		} catch (ParseException e) {
			e.printStackTrace();
		}
		long diff = -1;
		long diffInMillies = firstDate.getTime() - secondDate.getTime();
		diff = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

		return ((diff < 0) ? 0 : diff);
	}

	/**
	 * Funçao usada para saber adicionar segundos à hora passada como parametro
	 * 
	 * @param dateString - Data em formato de String (MM/dd/yyyy HH:mm:ss)
	 * @param seconds    - numero de segundos a adicionar na data
	 * @return Data em formato (MM/dd/yyyy HH:mm:ss)
	 */
	public static String addDate(String dateString, int seconds) {
		final String OLD_FORMAT = "MM/dd/yyyy HH:mm:ss";

		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date firstDate = null;

		try {
			firstDate = sdf.parse(dateString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(firstDate);
			cal.add(Calendar.SECOND, seconds);
			java.util.Date later = cal.getTime();
			java.sql.Timestamp timestamp2 = new java.sql.Timestamp(later.getTime());
			return new SimpleDateFormat(OLD_FORMAT).format(timestamp2);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;

	}

	public boolean insert(DataBase db, Ordem ordem) {
		String query = "INSERT INTO Ordem (numeroOrdem, horaEntradaOrdem, pecaspendentes, folgaexecucao) VALUES ('" + ordem.numeroOrdem
				+ "', '" + ordem.horaEntradaOrdem + "'," + ordem.pecasPendentes+","+ordem.folga + ")";
		return db.executeQuery(query);
	}

	protected boolean executaOrdem(DataBase db, String numeroOrdem) {
		String query = "UPDATE Ordem SET estadoOrdem= " + 1 + ", horaInicioExecucao= '" + Ordem.localDate()
				+ "' WHERE numeroOrdem = '" + numeroOrdem + "'";
		return db.executeQuery(query);

	}

	protected boolean terminaOrdem(DataBase db, String numeroOrdem, int prioridade) {
		String query = "UPDATE Ordem SET estadoOrdem= " + 2 + ", folgaexecucao="+prioridade+", horaFimExecucao= '" + Ordem.localDate()
				+ "' WHERE numeroOrdem = '" + numeroOrdem + "'";
		return db.executeQuery(query);
	}

	public ResultSet select(DataBase db) {
		String query = "SELECT  Ordem.numeroordem, ordem.folgaexecucao, estadoordem, pecasproduzidas, pecasproducao, pecaspendentes,TO_CHAR(horaentradaordem :: TIMESTAMP, 'dd/MM/yyyy HH24:MI:SS') as horaentradaordem,				TO_CHAR(horainicioexecucao :: TIMESTAMP, 'dd/MM/yyyy HH24:MI:SS') as horainicioexecucao, TO_CHAR(horafimexecucao :: TIMESTAMP, 'dd/MM/yyyy HH24:MI:SS') as horafimexecucao, producao.atrasomaximo\r\n"
				+ "     FROM fabrica.Ordem\r\n"
				+ "FULL OUTER JOIN fabrica.producao on ordem.numeroordem = producao.numeroordem";
		return db.executeQueryResult(query);
	}

	public boolean updatePecasPendentes(DataBase db, String numeroOrdem, int pecas) {
		String query = "UPDATE Ordem SET pecaspendentes=" + pecas + "WHERE numeroOrdem = '" + numeroOrdem + "'";
		return db.executeQuery(query);
	}

	public boolean updatePecasProduzidas(DataBase db, String numeroOrdem, int pecas, int prioridade) {
		String query = "UPDATE Ordem SET pecasproduzidas=" + pecas + ",folgaexecucao= "+prioridade+" WHERE numeroOrdem = '" + numeroOrdem + "'";
		return db.executeQuery(query);
	}

	public boolean updatePecasEmProducao(DataBase db, String numeroOrdem, int pecas, int prioridade) {
		String query = "UPDATE Ordem SET pecasproducao=" + pecas + ", folgaexecucao="+prioridade+" WHERE numeroOrdem = '" + numeroOrdem + "'";
		return db.executeQuery(query);
	}

	public boolean updateFolgaExecucao(DataBase db, Ordens ordem) {
		String query = "UPDATE Ordem SET folgaexecucao=" + ordem.getPrioridade() + " WHERE numeroOrdem = '" + ordem.getNumeroOrdem() + "'";
		return db.executeQuery(query);
	}
	
}
