package db;

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

}
