package fabrica;

import db.Ordem;

public class Ordens {
	private String numeroOrdem;
	private int prioridade; //tempo restante
	private String dataInicio; //MM/dd/yyyy HH:mm
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
	/**Cria o objeto ordens, em que no inicio prioridade = atrasoMaximo, ao longo do tempo a prioridade vai decrementando � medida que o tempo passa*/
	public Ordens(String numeroOrdem, int prioridade, String dataInicio, int atrasoMaximo) {
		this.numeroOrdem = numeroOrdem;
		this.prioridade = prioridade;
		this.dataInicio = dataInicio;
		this.atrasoMaximo = atrasoMaximo;
		this.pecasEmProducao=0;
		this.pecasPendentes=0;
		this.pecasProduzidas=0;
	}
	/***/
	public Ordens(String numeroOrdem, String dataInicio, int atrasoMaximo) {
		this.numeroOrdem = numeroOrdem;
		this.prioridade = atrasoMaximo;
		this.dataInicio = dataInicio;
		this.atrasoMaximo = atrasoMaximo;
		this.pecasEmProducao=0;
		this.pecasPendentes=0;
		this.pecasProduzidas=0;
	}
	public Ordens(String numeroOrdem, int atrasoMaximo) {
		this.numeroOrdem = numeroOrdem;
		this.prioridade = atrasoMaximo;
		this.dataInicio = Ordem.localDate();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numeroOrdem == null) ? 0 : numeroOrdem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ordens other = (Ordens) obj;
		if (numeroOrdem == null) {
			if (other.numeroOrdem != null)
				return false;
		} else if (!numeroOrdem.equals(other.numeroOrdem))
			return false;
		return true;
	}
	

}
