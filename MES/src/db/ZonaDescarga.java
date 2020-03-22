package db;

import java.sql.ResultSet;

public class ZonaDescarga {
	private String tipoDescarga;
	private String tipoPecaDescarregada;

	public ZonaDescarga() {
		// TODO Auto-generated constructor stub
	}
	public ZonaDescarga(String tipoDescarga, String tipoPecaDescarregada) {
		this.tipoPecaDescarregada=tipoPecaDescarregada;
		this.tipoDescarga = tipoDescarga;
	}

	protected boolean insere(DataBase db, ZonaDescarga zonaDescarga) {
		String query = "INSERT INTO ZonaDescarga (tipoDescarga, tipoPecaDescarregada) VALUES ('" + zonaDescarga.tipoDescarga
				+ "', '" + zonaDescarga.tipoPecaDescarregada + "' )";
		return db.executeQuery(query);
	}
	
	protected ResultSet select(DataBase db) {
		String query = "SELECT tipodescarga, tipopecadescarregada FROM fabrica.ZonaDescarga";
		return db.executeQueryResult(query);
	}

}
