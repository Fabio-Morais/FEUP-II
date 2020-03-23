package db;

import java.sql.ResultSet;

public class Maquina {
	private String tipoMaquina; 
	private String tipoPecaOperada;
	private int tempo;
	public Maquina(String tipoMaquina, String tipoPecaOperada, int tempo) {
		this.tipoMaquina = tipoMaquina;
		this.tempo=tempo;
		this.tipoPecaOperada =tipoPecaOperada; 
	}
	public Maquina() {
		
	}
	
	protected boolean insere(DataBase db, Maquina maquina) {
		String query= "INSERT INTO Maquina (tipoMaquina, tipoPecaOperada, tempo) VALUES ('"+maquina.tipoMaquina+"', '"+maquina.tipoPecaOperada+"', "+maquina.tempo+")";
		return db.executeQuery(query);
	}
	
	protected ResultSet select(DataBase db) {
		String query= "SELECT tipomaquina,tipopecaoperada,tempo FROM fabrica.maquina";
		return db.executeQueryResult(query);
	}
	
	protected ResultSet selectTempoTotal(DataBase db) {
		String query= "SELECT tipomaquina,sum(tempo) FROM fabrica.maquina GROUP BY tipomaquina ORDER BY tipomaquina";
		return db.executeQueryResult(query);
	}
	
	protected ResultSet selectTotalPecas(DataBase db) {
		String query= "SELECT tipomaquina,count(tipopecaoperada) FROM fabrica.maquina GROUP BY tipomaquina ORDER BY tipomaquina";
		return db.executeQueryResult(query);
	}
	
	protected ResultSet selectPecasPorTipo(DataBase db) {
		String query= "SELECT tipomaquina,tipopecaoperada,count(tipopecaoperada) FROM fabrica.maquina GROUP BY tipomaquina, tipopecaoperada ORDER BY tipomaquina ASC, count DESC";
		return db.executeQueryResult(query);
	}


}
