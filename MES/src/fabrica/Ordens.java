package fabrica;

public class Ordens {
	private String numeroOrdem;
	private int prioridade; //tempo restante
	private String dataInicio; //tempo em que foi enviado a ordem
	private int atrasoMaximo;
	private int pecasProduzidas;
	private int pecasEmProducao;
	private int pecasPendentes;
	private String pecaOrigem;
	private String pecaFinal;
	
	public String getNumeroOrdem() {
		return numeroOrdem;
	}

	public int getPrioridade() {
		return prioridade;
	}
	
	
	public String getDataInicio() {
		return dataInicio;
	}

	public int getAtrasoMaximo() {
		return atrasoMaximo;
	}

	public void setPrioridade(int prioridade) {
		this.prioridade = prioridade;
	}

	public Ordens(String numeroOrdem, int prioridade, String dataInicio, int atrasoMaximo) {
		this.numeroOrdem = numeroOrdem;
		this.prioridade = prioridade;
		this.dataInicio = dataInicio;
		this.atrasoMaximo = atrasoMaximo;
		this.pecasEmProducao=0;
		this.pecasPendentes=0;
		this.pecasProduzidas=0;
	}

	public int getPecasProduzidas() {
		return pecasProduzidas;
	}

	public void setPecasProduzidas(int pecasProduzidas) {
		this.pecasProduzidas = pecasProduzidas;
	}

	public int getPecasEmProducao() {
		return pecasEmProducao;
	}

	public void setPecasEmProducao(int pecasEmProducao) {
		this.pecasEmProducao = pecasEmProducao;
	}

	public int getPecasPendentes() {
		return pecasPendentes;
	}

	public void setPecasPendentes(int pecasPendentes) {
		this.pecasPendentes = pecasPendentes;
	}

	public String getPecaOrigem() {
		return pecaOrigem;
	}

	public void setPecaOrigem(String pecaOrigem) {
		this.pecaOrigem = pecaOrigem;
	}

	public String getPecaFinal() {
		return pecaFinal;
	}

	public void setPecaFinal(String pecaFinal) {
		this.pecaFinal = pecaFinal;
	}
	

}
