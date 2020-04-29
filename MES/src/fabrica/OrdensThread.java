package fabrica;

public class OrdensThread implements Runnable {
	private Ordens ordem;
	private ControlaPlc controlaPlc;
	
	public OrdensThread(Ordens ordem, ControlaPlc controlaPlc) {
		super();
		this.ordem = ordem;
		this.controlaPlc = controlaPlc;
	}

	@Override
	public void run() {
		
	}

}
