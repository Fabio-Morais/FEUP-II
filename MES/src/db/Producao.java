package db;

import java.sql.ResultSet;

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
		String query= "INSERT INTO Producao (numeroOrdem, pecaOrigem, pecaFinal, quantidadeProduzir, atrasoMaximo) VALUES ('"+producao.numeroOrdem+"', "
				+"'"+producao.pecaOrigem+"', '"+producao.pecaFinal+"',"+producao.quantidadeProduzir+", "+producao.atrasoMaximo+")";
		return db.executeQuery(query);
		
	}

	
	protected ResultSet selectOrdensPendentes (DataBase db) {
		String query= "SELECT * FROM fabrica.ordem \r\n" + 
				"INNER JOIN fabrica.producao\r\n" + 
				"ON ordem.numeroOrdem = producao.numeroOrdem  WHERE estadoordem = '0' OR estadoordem='1' ";
		return db.executeQueryResult(query);
	}
	public String getNumeroOrdem() {
		return numeroOrdem;
	}
	public int getQuantidadeProduzir() {
		return quantidadeProduzir;
	}
	public int getAtrasoMaximo() {
		return atrasoMaximo;
	}
	
}
