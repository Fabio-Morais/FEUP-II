package db;

import java.sql.ResultSet;

public class Descarga {
	private String numeroOrdem;

	
	private String pecaDescarga;
	private String destino;
	private int quantidadePecasDescarregar;
	
	public Descarga(String numeroOrdem, String pecaDescarga, String destino, int quantidadePecasDescarregarS) {
		this.numeroOrdem = numeroOrdem;
		this.pecaDescarga = pecaDescarga;
		this.destino = destino;
		this.quantidadePecasDescarregar = quantidadePecasDescarregarS;
	}
	public Descarga() {
		
	}

	public void setNumeroOrdem(String numeroOrdem) {
		this.numeroOrdem = numeroOrdem;
	}

	protected boolean insere(DataBase db, Descarga descarga) {
		String query= "INSERT INTO Descarga (numeroOrdem, pecaDescarga, destino, quantidadePecasDescarregar) VALUES ('"+descarga.numeroOrdem+"', '"
				+descarga.pecaDescarga+"', '"+descarga.destino+"',"+descarga.quantidadePecasDescarregar+")";
		return db.executeQuery(query);
		
	}

	
	protected ResultSet selectOrdensPendentes(DataBase db) {
		String query= "SELECT * FROM fabrica.ordem \r\n" + 
				"INNER JOIN fabrica.descarga\r\n" + 
				"ON ordem.numeroOrdem = descarga.numeroOrdem  WHERE estadoordem = '0' OR  estadoordem = '1'";
		return db.executeQueryResult(query);
	}
	public String getNumeroOrdem() {
		return numeroOrdem;
	}
	public int getQuantidadePecasDescarregar() {
		return quantidadePecasDescarregar;
	}
	
	
}
