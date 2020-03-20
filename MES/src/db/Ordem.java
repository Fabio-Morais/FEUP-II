package db;

import java.text.SimpleDateFormat;

public class Ordem {

	public Ordem() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Funçao usada para saber Data e hora atual 
	 * 
	 * @return DataHora String com data e hora
	 */
	static String localDate() {
		java.util.Date date = new java.util.Date();
		java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
		return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp);
		
	}
}
