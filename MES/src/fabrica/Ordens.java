package fabrica;

public class Ordens {
	private String numeroOrdem;
	private int prioridade; //tempo restante
	private String dataInicio; //tempo em que foi enviado a ordem
	private int atrasoMaximo;
	
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
	}

}
