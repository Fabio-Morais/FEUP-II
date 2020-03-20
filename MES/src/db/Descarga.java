package db;

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
		String query= "INSERT INTO Descarga (numeroOrdem, horaEntradaOrdem,pecaDescarga, destino, quantidadePecasDescarregar) VALUES ('"+descarga.numeroOrdem+"', '"+descarga.horaEntradaOrdem
				+"', '"+descarga.pecaDescarga+"', '"+descarga.destino+"',"+descarga.quantidadePecasDescarregar+")";
		return db.executeQuery(query);
		
	}

	protected boolean executaOrdem(DataBase db, String numeroOrdem) {
		String query= "UPDATE Descarga SET estadoOrdem= "+ 1 + ", horaInicioExecucao= '"+ Ordem.localDate()+ "' WHERE numeroOrdem = '"+ numeroOrdem+"'";
		return db.executeQuery(query);
		
	}
	
	protected boolean terminaOrdem(DataBase db, String numeroOrdem) {
		String query= "UPDATE Descarga SET estadoOrdem= "+ 2 + ", horaFimExecucao= '"+ Ordem.localDate()+ "' WHERE numeroOrdem = '"+ numeroOrdem+"'";
		return db.executeQuery(query);
		
	}
}
