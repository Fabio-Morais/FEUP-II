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
	
	protected ResultSet selectPecasDescarregadasTotal(DataBase db) {
		String query = "SELECT tipodescarga, count(*) FROM fabrica.zonaDescarga GROUP BY tipodescarga ORDER BY tipodescarga";
		return db.executeQueryResult(query);
	}
	
	protected ResultSet selectPecasDescarregadasPorTipo(DataBase db) {
		String query = "SELECT tipodescarga, tipopecadescarregada, count(*) FROM fabrica.zonaDescarga GROUP BY tipodescarga,tipopecadescarregada ORDER BY tipodescarga ASC, count DESC";
		return db.executeQueryResult(query);
	}

}
