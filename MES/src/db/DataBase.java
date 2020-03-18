package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

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
	 * Função para conectar à base de dados
	 * @return boolean true se conexão com exito / false caso contrario 	
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
	 * Função para testar conexão à base de dados
	 * @return boolean true se conexão com exito / false caso contrario 	
	 */
	public boolean checkConnection() {
		if(connect()) {
			return disconnect();
		}else {
			return false;
		}
	}
	
	/**
	 * Função para disconectar da base de dados
	 * @return boolean true se conexão terminada com exito / false caso contrario 	
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
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = getC().createStatement();
			rs = stmt.executeQuery("SET search_path to fabrica;"+sql);
			
		} catch (Exception e) {
			disconnect();
		}
		disconnect();
		return rs;
	}
	
	/**
	 * Funçao usada para saber Data e hora atual 
	 * 
	 * @return DataHora String com data e hora
	 */
	public String localDate() {
		java.util.Date date = new java.util.Date();
		java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
		return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp);
		
	}
	/**
	 * Retorna a conexão com a base de dados
	 * @return Conexão Conexão com base de dados
	 */
	public Connection getC() {
		return c;
	}
	
	/**
	 * Define a conexao com a base de dados
	 * @param conexao Conexão com a base de dados
	 */
	public void setC(Connection conexao) {
		this.c = conexao;
	}

	public void insereMaquina(Maquina maquina) {
		maquina.insere(this, maquina);
	}
	
	
}
