package db;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Ordem {
	private final static String dateFormat="MM/dd/yyyy HH:mm:ss";
	public Ordem() {
	}
	/**
	 * Funçao usada para saber Data e hora atual 
	 * 
	 * @return DataHora String com data e hora
	 */
	public static String localDate() {
		Date date = new java.util.Date();
		Timestamp timestamp = new java.sql.Timestamp(date.getTime());
		return new SimpleDateFormat(dateFormat).format(timestamp);
		
	}
	/**
	 * Funçao usada para saber a diferença entre 2 datas
	 * @param firstDateString - Data em formato de String (dd/MM/yy HH:mm:ss)
	 * @param secondDateString - Data em formato de String (dd/MM/yy HH:mm:ss)
	 * @return retorna a diferença em segundos
	 */
	public static int calculaDiferenca(String firstDateString, String secondDateString) {
		final String OLD_FORMAT = "dd/MM/yyyy HH:mm:ss";
		final String NEW_FORMAT = "MM/dd/yyyy HH:mm:ss";


		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
	    Date firstDate=null;
	    Date secondDate=null;

		try {
			firstDate = sdf.parse(firstDateString);
			secondDate = sdf.parse(secondDateString);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		sdf.applyPattern(NEW_FORMAT);


		if(firstDate.compareTo(secondDate) == -1)
			return 0;

	    int diff=-1;
		int diffInMillies = (int) Math.abs(secondDate.getTime() - firstDate.getTime());
		diff = (int) TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		
	    return diff;
	}
	
	/**
	 * Funçao usada para saber adicionar segundos à hora passada como parametro
	 * @param dateString - Data em formato de String (dd/MM/yy HH:mm:ss)
	 * @param seconds - numero de segundos a adicionar na data 
	 * @return Data em formato (dd/MM/yyyy HH:mm:ss)
	 */
	public static String addDate(String dateString, int seconds) {
		final String OLD_FORMAT = "dd/MM/yyyy HH:mm:ss";

		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
	    Date firstDate=null;

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
	
	
	
	public ResultSet select(DataBase db) {
		String query= "SELECT  x.numeroordem, x.estadoordem, x.pecasproduzidas, x.pecasproducao, x.pecaspendentes,TO_CHAR(x.horaentradaordem :: TIMESTAMP, 'dd/MM/yyyy HH24:MI:SS') as horaentradaordem, \r\n" + 
				"TO_CHAR(x.horainicioexecucao :: TIMESTAMP, 'dd/MM/yyyy HH24:MI:SS') as horainicioexecucao, \r\n" + 
				"TO_CHAR(x.horafimexecucao :: TIMESTAMP, 'dd/MM/yyyy HH24:MI:SS') as horafimexecucao, x.folgaexecucao, producao.atrasomaximo \r\n" + 
				"FROM (SELECT numeroordem, estadoordem, pecasproduzidas, pecasproducao, pecaspendentes,horaentradaordem, horainicioexecucao, \r\n" + 
				"horafimexecucao, folgaexecucao FROM fabrica.Producao\r\n" + 
				"UNION ALL \r\n" + 
				"select numeroordem, estadoordem, pecasproduzidas, pecasproducao, pecaspendentes,horaentradaordem, horainicioexecucao, horafimexecucao, folgaexecucao \r\n" + 
				"from fabrica.descarga) as x\r\n" + 
				"FULL OUTER JOIN fabrica.producao ON x.numeroordem = producao.numeroordem";
		return db.executeQueryResult(query);	
		}
}
