package udp.estatistica;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class Estatistica {	

	public Estatistica() {
	}

	/** exporta em .html as estatisticas
	 * @param abrirFicheiro - true se quiser abrir ficheiro, false caso contrario
	 * */
	public void exportaFicheiros(boolean abrirFicheiro) {
		PecasDescarregadas pecasDescarregadas = new PecasDescarregadas();
		EstatisticasMaquina estatisticasMaquina = new EstatisticasMaquina();
		ListaOrdens listaOrdens = new ListaOrdens();
		
		try {
			listaOrdens.exportaFicheiro(abrirFicheiro);
			estatisticasMaquina.exportaFicheiro(abrirFicheiro);
			pecasDescarregadas.exportaFicheiro(abrirFicheiro);
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
