package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Semaphore;

import org.postgresql.util.PSQLException;

import fabrica.Ordens;

public class DataBase {
	private static DataBase instance = null;

	// DADOS BASE DE DADOS
	private String url;
	private String user;
	private String password;
	private Connection c;

	// TABELAS DATA BASE
	private ZonaDescarga zonaDescarga;
	private Maquina maquina;
	private Producao producao;
	private Descarga descarga;
	private Ordem ordem;
	private Semaphore sem;

	private DataBase() {
		this.url = "jdbc:postgresql://db.fe.up.pt:5432/up201504257?currentSchema=fabrica";
		this.user = "up201504257";
		this.password = "hFj8JWsg9";
		this.c = null;
		DriverManager.setLoginTimeout(3);
		this.zonaDescarga = new ZonaDescarga();
		this.maquina = new Maquina();
		this.producao = new Producao();
		this.descarga = new Descarga();
		this.ordem = new Ordem();
		this.sem = ConnectSemaphore.getSem();

	}

	public static DataBase getInstance() {
		if (instance == null)
			instance = new DataBase();
		return instance;
	}

	/**
	 * Retorna url da base de dados
	 * 
	 * @return String - url da base de dados
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Retorna user da base de dados
	 * 
	 * @return String - user da base de dados
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Retorna password da base de dados
	 * 
	 * @return String - password da base de dados
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Função para conectar à base de dados
	 * 
	 * @return boolean true se conexão com exito / false caso contrario
	 */
	public synchronized boolean connect() {
		
		try {
			Class.forName("org.postgresql.Driver");
			this.c = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			//System.out.println(e.toString());

			return false;
		}
		return true;
	}

	/**
	 * Função para testar conexão à base de dados
	 * 
	 * @return boolean true se conexão com exito / false caso contrario
	 */
	public synchronized boolean checkConnection() {
		if (connect()) {
			return disconnect();
		} else {
			return false;
		}
	}

	/**
	 * Função para disconectar da base de dados
	 * 
	 * @return boolean true se conexão terminada com exito / false caso contrario
	 */
	public synchronized boolean disconnect() {
		
		try {
			this.c.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Executa a query pretendida
	 * 
	 * @param sql - string a executar via sql
	 * @return boolean - true se executar corretamente / false caso contrario
	 */
	public synchronized boolean executeQuery(String sql) {

		connect();
		try {
			
			try{
				Statement stmt = getC().createStatement();
				stmt.executeUpdate("SET search_path to fabrica;" + sql);
			}catch(PSQLException e) {
				sem.release();
				connect();
				Statement stmt = getC().createStatement();
				stmt.executeUpdate("SET search_path to fabrica;" + sql);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			disconnect();
			
			return false;
		}
		disconnect();
		return true;
	}

	/**
	 * Executa a query pretendida e retorna o ResultSet
	 * 
	 * @param sql - string a executar via sql
	 * @return ResultSet - retorna corretamente o ResultSet caso nao haja erros /
	 *         null caso contrario
	 */
	public synchronized ResultSet executeQueryResult(String sql) {
		connect();
		ResultSet rs = null;

		try {
			
			Statement stmt = getC().createStatement();
			rs = stmt.executeQuery(sql);
		} catch (Exception e) {
			disconnect();
			e.printStackTrace();
		}
		disconnect();
		return rs;
	}

	/**
	 * Retorna a conexão com a base de dados
	 * 
	 * @return Conexão Conexão com base de dados
	 */
	public Connection getC() {
		return c;
	}

	/**
	 * Define a conexao com a base de dados
	 * 
	 * @param conexao Conexão com a base de dados
	 */
	public void setC(Connection conexao) {
		this.c = conexao;
	}

	public boolean insereMaquina(Maquina maquina) {
		return this.maquina.insere(this, maquina);
	}

	public ResultSet selectMaquina() {
		return maquina.select(this);
	}

	public ResultSet selectMaquinaTempoTotal() {
		return maquina.selectTempoTotal(this);
	}

	public ResultSet selectMaquinaTotalPecas() {
		return maquina.selectTotalPecas(this);
	}

	public ResultSet selectMaquinaPecasPorTipo() {
		return maquina.selectPecasPorTipo(this);
	}

	public boolean insereZonaDescarga(ZonaDescarga zonaDescarga) {
		return this.zonaDescarga.insere(this, zonaDescarga);
	}

	public ResultSet selectZonaDescarga() {
		return zonaDescarga.select(this);
	}

	public ResultSet selectZonaDescargaTotal() {
		return zonaDescarga.selectPecasDescarregadasTotal(this);
	}

	public ResultSet selectZonaDescargaPorPeca() {
		return zonaDescarga.selectPecasDescarregadasPorTipo(this);
	}

	public boolean insereProducao(Producao producao) {
		return this.ordem.insert(this, new Ordem(producao.getNumeroOrdem(), producao.getQuantidadeProduzir(), producao.getAtrasoMaximo()))
				&& this.producao.insere(this, producao);
	}

	public boolean executaOrdemProducao(String numeroOrdem) {
		return ordem.executaOrdem(this, numeroOrdem);
	}

	public boolean terminaOrdemProducao(String numeroOrdem, int prioridade) {
		return ordem.terminaOrdem(this, numeroOrdem,prioridade);
	}

	public boolean insereDescarga(Descarga descarga) {
		return ordem.insert(this, new Ordem(descarga.getNumeroOrdem(), descarga.getQuantidadePecasDescarregar(), 0))
				&& this.descarga.insere(this, descarga);
	}

	/** Retorna as ordens pendentes e em execuçao */
	public ResultSet selectProducao() {
		return producao.selectOrdensPendentes(this);
	}

	/** Retorna as ordens pendentes e em execuçao */
	public ResultSet selectDescarga() {
		return descarga.selectOrdensPendentes(this);
	}

	public ResultSet selectOrdem() {
		return ordem.select(this);
	}

	public boolean updatePecasPendentes(String numeroOrdem, int pecas) {
		return ordem.updatePecasPendentes(this, numeroOrdem, pecas);
	}

	public boolean updatePecasProduzidas(String numeroOrdem, int pecas, int prioridade) {
		return ordem.updatePecasProduzidas(this, numeroOrdem, pecas, prioridade);
	}

	public boolean updatePecasEmProducao(String numeroOrdem, int pecas, int prioridade) {
		return ordem.updatePecasEmProducao(this, numeroOrdem, pecas, prioridade);
	}

	public boolean updateFolgaExecucao(Ordens ordem) {
		return this.ordem.updateFolgaExecucao(this, ordem);
	}

}
