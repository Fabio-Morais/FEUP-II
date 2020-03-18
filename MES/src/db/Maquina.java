package db;

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

}
