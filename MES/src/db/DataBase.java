package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBase {
	private static DataBase instance=null;


	//DADOS BASE DE DADOS
	private String url;
	private String user;
	private String password;
	private Connection c;

	//TABELAS DATA BASE
	private ZonaDescarga zonaDescarga;
	private Maquina maquina;
	private Producao producao;
	private Descarga descarga;
	private Ordem ordem;
	
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

	}
	public static DataBase getInstance() {
		if(instance == null)
			instance = new DataBase();
		return instance;
	}
	/**
	 * Retorna url da base de dados
	 * @return String - url da base de dados 	
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Retorna user da base de dados
	 * @return String - user da base de dados 	
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * Retorna password da base de dados
	 * @return String - password da base de dados 	
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Fun��o para conectar � base de dados
	 * @return boolean true se conex�o com exito / false caso contrario 	
	 */
	public boolean connect() {
		try {
			Class.forName("org.postgresql.Driver");
	        this.c = DriverManager.getConnection(url,  user, password);
		} catch (Exception e) {
			System.out.println(e.toString());
	        return false;
		}
		return true;
	}
	
	/**
	 * Fun��o para testar conex�o � base de dados
	 * @return boolean true se conex�o com exito / false caso contrario 	
	 */
	public boolean checkConnection() {
		if(connect()) {
			return disconnect();
		}else {
			return false;
		}
	}
	
	/**
	 * Fun��o para disconectar da base de dados
	 * @return boolean true se conex�o terminada com exito / false caso contrario 	
	 */
	public boolean disconnect() {
		try {
			this.c.close();
		} catch (Exception e) {
	        return false;
		}
		return true;
	}
	
	/**
	 * Executa a query pretendida
	 * @param sql - string a executar via sql
	 * @return boolean - true se executar corretamente / false caso contrario
	 * */
	public boolean executeQuery(String sql) {

		connect();
		try {
			Statement stmt = getC().createStatement();
			stmt.executeUpdate("SET search_path to fabrica;"+sql);
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
	 * @param sql - string a executar via sql
	 * @return ResultSet - retorna corretamente o ResultSet caso nao haja erros / null caso contrario
	 * */
	public ResultSet executeQueryResult(String sql) {
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
	 * Retorna a conex�o com a base de dados
	 * @return Conex�o Conex�o com base de dados
	 */
	public Connection getC() {
		return c;
	}
	
	/**
	 * Define a conexao com a base de dados
	 * @param conexao Conex�o com a base de dados
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
		return ordem.insert(this,new Ordem(producao.getNumeroOrdem())) && this.producao.insere(this, producao);
	}
	
	public boolean executaOrdemProducao(String numeroOrdem) {
		return ordem.executaOrdem(this, numeroOrdem);
	}
	
	public boolean terminaOrdemProducao(String numeroOrdem) {
		return ordem.terminaOrdem(this, numeroOrdem);
	}
	
	public boolean insereDescarga(Descarga descarga) {
		return ordem.insert(this,new Ordem(descarga.getNumeroOrdem())) && this.descarga.insere(this, descarga);
	}	
	/**Retorna as ordens pendentes e em execu�ao*/
	public ResultSet selectProducao() {
		return producao.selectOrdensPendentes(this);
	}
	
	/**Retorna as ordens pendentes e em execu�ao*/
	public ResultSet selectDescarga() {
		return descarga.selectOrdensPendentes(this);
	}
	
	public ResultSet selectOrdem() {
		return ordem.select(this);
	}
	
	
}
