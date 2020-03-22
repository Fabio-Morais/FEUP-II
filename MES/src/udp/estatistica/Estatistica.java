package udp.estatistica;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class Estatistica {	

	public Estatistica() {
		exportaFicheiros();
	}

	public void exportaFicheiros() {
		PecasDescarregadas pecasDescarregadas = new PecasDescarregadas();
		EstatisticasMaquina estatisticasMaquina = new EstatisticasMaquina();
		ListaOrdens listaOrdens = new ListaOrdens();
		
		try {
			listaOrdens.exportaFicheiro();
			estatisticasMaquina.exportaFicheiro();
			pecasDescarregadas.exportaFicheiro();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Funçao usada para saber Data e hora atual 
	 * 
	 * @return DataHora String com data e hora
	 */
	public static String localDate() {
		java.util.Date date = new java.util.Date();
		java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
		return new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(timestamp);
		
	}
}
