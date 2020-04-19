package db;

import java.sql.ResultSet;

public class Descarga {
	private String numeroOrdem;
	private int estadoOrdem; //0-> pendente, 1->execuçao, 3-> terminada
	private int pecasProduzidas;
	private int pecasPendentes;
	private String horaEntradaOrdem;
	private String horaInicioExecucao;
	private String horaFimExecucao;
	private int folgaExecucao;
	
	private String pecaDescarga;
	private String destino;
	private int quantidadePecasDescarregar;
	
	public Descarga(String numeroOrdem, String pecaDescarga, String destino, int quantidadePecasDescarregarS) {
		this.numeroOrdem = numeroOrdem;
		this.horaEntradaOrdem = Ordem.localDate();
		this.pecaDescarga = pecaDescarga;
		this.destino = destino;
		this.quantidadePecasDescarregar = quantidadePecasDescarregarS;
	}
	public Descarga() {
		
	}

	public void setNumeroOrdem(String numeroOrdem) {
		this.numeroOrdem = numeroOrdem;
	}

	public void setEstadoOrdem(int estadoOrdem) {
		this.estadoOrdem = estadoOrdem;
	}

	public void setPecasProduzidas(int pecasProduzidas) {
		this.pecasProduzidas = pecasProduzidas;
	}

	public void setPecasPendentes(int pecasPendentes) {
		this.pecasPendentes = pecasPendentes;
	}

	public void setHoraInicioExecucao(String horaInicioExecucao) {
		this.horaInicioExecucao = horaInicioExecucao;
	}

	public void setHoraFimExecucao(String horaFimExecucao) {
		this.horaFimExecucao = horaFimExecucao;
	}

	public void setFolgaExecucao(int folgaExecucao) {
		this.folgaExecucao = folgaExecucao;
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
