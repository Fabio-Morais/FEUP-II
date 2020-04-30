package fabrica;

import java.util.List;
import java.util.concurrent.Semaphore;

import db.DataBase;
import db.Ordem;

public class Ordens {
	private String numeroOrdem;
	private int prioridade; // tempo restante
	private String dataInicio; // MM/dd/yyyy HH:mm
	private int atrasoMaximo;
	private int pecasProduzidas;
	private int pecasEmProducao;
	private int pecasPendentes;
	private int quantidade;
	private Semaphore sem;
	private Fabrica fabrica;
	private DataBase db;
	private Transform transform;
	private Unload unload;
	private ControlaPlc enviaOrdem;
	
	public class  Transform{
		private String from;
		private String to;
		public Transform(String from, String to) {
			super();
			this.from = from;
			this.to = to;
		}
		public String getFrom() {
			return from;
		}
		public String getTo() {
			return to;
		}
		@Override
		public String toString() {
			return "Transform [from=" + from + ", to=" + to + "]";
		}

		
	};
	public class  Unload{
		private String type;
		private String destinantion;
		public Unload(String type, String destinantion) {
			super();
			this.type = type;
			this.destinantion = destinantion;
		}
		public String getType() {
			return type;
		}
		public String getDestinantion() {
			return destinantion;
		}
		@Override
		public String toString() {
			return "Unload [type=" + type + ", destinantion=" + destinantion + "]";
		}
		
		
	};


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

	/**
	 * Cria o objeto ordens, em que no inicio prioridade = atrasoMaximo, ao longo do
	 * tempo a prioridade vai decrementando à medida que o tempo passa
	 */
	public Ordens(String numeroOrdem, int prioridade, String dataInicio, int atrasoMaximo, Fabrica fabrica) {
		this.numeroOrdem = numeroOrdem;
		this.prioridade = prioridade;
		this.dataInicio = dataInicio;
		this.atrasoMaximo = atrasoMaximo;
		this.pecasEmProducao = 0;
		this.pecasPendentes = 0;
		this.pecasProduzidas = 0;
		this.sem = GeneralSemaphore.getSem();
		this.fabrica = fabrica;
		db = DataBase.getInstance();
	}

	/***/
	public Ordens(String numeroOrdem, String dataInicio, int atrasoMaximo) {
		this.numeroOrdem = numeroOrdem;
		this.prioridade = atrasoMaximo;
		this.dataInicio = dataInicio;
		this.atrasoMaximo = atrasoMaximo;
		this.pecasEmProducao = 0;
		this.pecasPendentes = 0;
		this.pecasProduzidas = 0;
		this.sem = GeneralSemaphore.getSem();
		this.fabrica = Fabrica.getInstance();
		db = DataBase.getInstance();

	}

	public Ordens(String numeroOrdem, int atrasoMaximo) {
		this.numeroOrdem = numeroOrdem;
		this.prioridade = atrasoMaximo;
		this.dataInicio = Ordem.localDate();
		this.atrasoMaximo = atrasoMaximo;
		this.pecasEmProducao = 0;
		this.pecasPendentes = 0;
		this.pecasProduzidas = 0;
		this.sem = GeneralSemaphore.getSem();
		this.fabrica = Fabrica.getInstance();
		db = DataBase.getInstance();

	}

	/**
	 * executa uma ordem
	 * 
	 * @param ordem - Ordem a executar
	 */
	public void executaOrdem() {
		try {
			sem.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		fabrica.getHeapOrdemExecucao().put(this.numeroOrdem, this);
		sem.release();
		try {
			db.executaOrdemProducao(this.numeroOrdem);
		} catch (Exception e) {

		}
		/*if(this.getTransform() != null && this.getUnload() == null) {
			System.out.println("executa ordem");

		}*/
		if(this.fabrica.getHeapOrdemPendente().peek().equals(this)){
			this.fabrica.getHeapOrdemPendente().poll();
			System.out.println("removeu corretamente da heap");
		}else {
			System.out.println("errada");
			fabrica.reorganizaHeap(this);
		}
			
		System.out.println("acaba o metodo executaOrdem");
	}

	/**
	 * Termina ordem
	 * 
	 * @param numeroOrdem - numero da ordem
	 */
	public void terminaOrdem() {
		if (db.terminaOrdemProducao(this.numeroOrdem)) {
			try {
				sem.acquire();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			fabrica.getHeapOrdemExecucao().remove(this.numeroOrdem);
			sem.release();

		}
	}

	/** Retira uma peça de "pendente" para "em produçao" */
	public void pecaParaProducao() {
		if (this.pecasPendentes > 0) {
			db.updatePecasPendentes(this.numeroOrdem, --this.pecasPendentes);//atualiza db e variaveis da classe
			db.updatePecasEmProducao(this.numeroOrdem, ++this.pecasEmProducao);//atualiza db e variaveis da classe
		}

	}

	/** Retira uma peça de "em produçao" para "produzida" */
	public void pecasProduzidas() {
		if (this.pecasEmProducao > 0) {
			db.updatePecasEmProducao(this.numeroOrdem, --this.pecasEmProducao);//atualiza db e variaveis da classe
			db.updatePecasProduzidas(this.numeroOrdem, ++this.pecasProduzidas);//atualiza db e variaveis da classe
		}

	}
	
	/**Retorna lista da tipo pecas ex: P1->P8 lista(p1,p4,p8)
	 * */
	public List<String> getListaPecas(int tempoRestanteMaquina){
		return Receitas.rotaMaquinas(transform.getFrom(), transform.getTo(), tempoRestanteMaquina, 1);
	}
	
	/**Retorna lista da receita((0)->Maquina |(1)->tempo na maquina |(2)->tipo ferramenta)
	 * */
	public List<String> getReceita(int tempoRestanteMaquina){
		return Receitas.rotaMaquinas(transform.getFrom(), transform.getTo(), tempoRestanteMaquina, 0);
	}

	/**
	 * entradaData (dd/MM/yy HH:mm:ss)
	 */
	public int calculaPrioridade() {
		return (int) Ordem.calculaTempoRestante(Ordem.converteData3(this.dataInicio), Integer.valueOf(this.atrasoMaximo));
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
		this.quantidade=pecasPendentes;
	}


	

	public int getQuantidade() {
		return quantidade;
	}

	public Transform getTransform() {
		return transform;
	}


	public Unload getUnload() {
		return unload;
	}
	
	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	public void setUnload(Unload unload) {
		this.unload = unload;
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

	@Override
	public String toString() {
		return "Ordens [numeroOrdem=" + numeroOrdem + ", prioridade=" + prioridade + ", dataInicio=" + dataInicio
				+ ", atrasoMaximo=" + atrasoMaximo + ", pecasProduzidas=" + pecasProduzidas + ", pecasEmProducao="
				+ pecasEmProducao + ", pecasPendentes=" + pecasPendentes + ", sem=" + sem + ", fabrica=" + fabrica + ", db=" + db + ", transform=" + transform
				+ ", unload=" + unload + "]";
	}


}
