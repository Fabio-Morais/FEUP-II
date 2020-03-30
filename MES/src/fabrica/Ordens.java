package fabrica;

public class Ordens {
	private String numeroOrdem;
	private int prioridade;
	public String getNumeroOrdem() {
		return numeroOrdem;
	}

	public int getPrioridade() {
		return prioridade;
	}
	
	public Ordens(String numeroOrdem, int prioridade) {
		this.numeroOrdem = numeroOrdem;
		this.prioridade = prioridade;
	}

}
