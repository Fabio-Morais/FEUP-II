package db;

import java.sql.ResultSet;
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
	public static String localDate() {
		java.util.Date date = new java.util.Date();
		java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
		return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp);
		
	}
	
	public ResultSet select(DataBase db) {
		String query= "SELECT numeroordem, estadoordem, pecasproduzidas, pecasproducao, pecaspendentes,horaentradaordem, horainicioexecucao, horafimexecucao, folgaexecucao\r\n" + 
				"FROM fabrica.Producao\r\n" + 
				"UNION ALL (select numeroordem, estadoordem, pecasproduzidas, pecasproducao, pecaspendentes,horaentradaordem, horainicioexecucao, horafimexecucao, folgaexecucao from fabrica.descarga)";
		return db.executeQueryResult(query);	
		}
}
