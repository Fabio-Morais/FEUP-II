package db;

import java.sql.Date;

public class Producao {

	private String numeroOrdem;
	private int estadoOrdem; //0-> pendente, 1->execuçao, 3-> terminada
	private int pecasProduzidas;
	private int pecasPendentes;
	private String horaEntradaOrdem;
	private String horaInicioExecucao;
	private String horaFimExecucao;
	private int folgaExecucao;
	
	private String pecaOrigem;
	private String pecaFinal;
	private int quantidadeProduzir;
	private int atrasoMaximo;
	
	public Producao(String numeroOrdem, String pecaOrigem, String pecaFinal, int quantidadeProduzir, int atrasoMaximo) {
		this.numeroOrdem = numeroOrdem;
		this.horaEntradaOrdem = Ordem.localDate();
		this.pecaOrigem = pecaOrigem;
		this.pecaFinal = pecaFinal;
		this.quantidadeProduzir = quantidadeProduzir;
		this.atrasoMaximo = atrasoMaximo;
	}
	public Producao( ) {
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
	protected boolean insere(DataBase db, Producao producao) {
		String query= "INSERT INTO Producao (numeroOrdem, horaEntradaOrdem, pecaOrigem, pecaFinal, quantidadeProduzir, atrasoMaximo) VALUES ('"+producao.numeroOrdem+"', '"+producao.horaEntradaOrdem
				+"', '"+producao.pecaOrigem+"', '"+producao.pecaFinal+"',"+producao.quantidadeProduzir+", "+producao.atrasoMaximo+")";
		return db.executeQuery(query);
		
	}

	protected boolean executaOrdem(DataBase db, String numeroOrdem) {
		String query= "UPDATE Producao SET estadoOrdem= "+ 1 + ", horaInicioExecucao= '"+ Ordem.localDate()+ "' WHERE numeroOrdem = '"+ numeroOrdem+"'";
		return db.executeQuery(query);
		
	}
	
	protected boolean terminaOrdem(DataBase db, String numeroOrdem) {
		String query= "UPDATE Producao SET estadoOrdem= "+ 2 + ", horaFimExecucao= '"+ Ordem.localDate()+ "' WHERE numeroOrdem = '"+ numeroOrdem+"'";
		return db.executeQuery(query);
		
	}
}
