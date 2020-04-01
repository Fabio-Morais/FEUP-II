package udp.estatistica;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;

import db.DataBase;
import db.Ordem;

public class EstatisticasMaquina {
	private DataBase db;
	
	public EstatisticasMaquina() {
	this.db = DataBase.getInstance();
	}

	public void exportaFicheiro(boolean abrirFicheiro) throws IOException {
		String x=null;
		File htmlTemplateFile = new File("htmlTemplate/template.html");
		String htmlString = FileUtils.readFileToString(htmlTemplateFile,x);
		String title = "Estatistica Maquinas";
		StringBuilder body = new StringBuilder();

		tempoTotalOperacao(body);
		numeroPecasOperadas(body);
		pecasPorTipo(body);
		
		htmlString = htmlString.replace("$title", title);
		htmlString = htmlString.replace("#sec", title);
		htmlString = htmlString.replace("$body", body);
		String fileName= "estatisticas/estatisticaMaquina("+ Estatistica.localDate()+").html";
		File newHtmlFile = new File(fileName);
		FileUtils.writeStringToFile(newHtmlFile, htmlString, x);
		
		 if(abrirFicheiro) {
		        Desktop desktop = Desktop.getDesktop();
		        if(newHtmlFile.exists()) desktop.open(newHtmlFile);
	        }
	}
	
	private void numeroPecasOperadas(StringBuilder body) {
		body.append("<div class=\"container-fluid \" >\r\n" + 
				"<h2>Numero de pe&ccedil;as operadas</h2>\r\n"+
				"<table  class=\"table table-dark table-hover\">\r\n" + 
				"  <tr>\r\n" + 
				"    <th class=\"text-center\">Tipo de Maquina</th>\r\n" + 
				"    <th class=\"text-center\">Total de pe&ccedil;a operadas</th>\r\n" + 
				"  </tr>\r\n");
		
		ResultSet rs= db.selectMaquinaTotalPecas();
		try {
			while(rs.next()) {
				body.append("<tr>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("tipomaquina")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("count")+"</td>\r\n");
				body.append("</tr>\r\n");

			}
		} catch (SQLException e) {
		}
		body.append("</table></div>");
	}
	
	private void tempoTotalOperacao(StringBuilder body) {
		body.append("<div class=\"container-fluid \" >\r\n" + 
				"<h2>Tempo total de opera&ccedil;ao</h2>\r\n"+
				"<table  class=\"table table-dark table-hover\">\r\n" + 
				"  <tr>\r\n" + 
				"    <th class=\"text-center\">Tipo de Maquina</th>\r\n" + 
				"    <th class=\"text-center\">Tempo total de opera&ccedil;ao</th>\r\n" + 
				"  </tr>\r\n");
		
		ResultSet rs= db.selectMaquinaTempoTotal();
		try {
			while(rs.next()) {
				body.append("<tr>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("tipomaquina")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("sum")+"</td>\r\n");
				body.append("</tr>\r\n");

			}
		} catch (SQLException e) {
		}
		body.append("</table></div>");
	}

	private void pecasPorTipo(StringBuilder body) {
		body.append("<div class=\"container-fluid \" >\r\n" + 
				"<h2>Numero de pe&ccedil;as operadas por tipo</h2>\r\n"+
				"<table  class=\"table table-dark table-hover\">\r\n" + 
				"  <tr>\r\n" + 
				"    <th class=\"text-center\">Tipo de Maquina</th>\r\n" + 
				"    <th class=\"text-center\">Tipo de pe&ccedil;a</th>\r\n" + 
				"    <th class=\"text-center\">Total pe&ccedil;as operadas</th>\r\n" + 
				"  </tr>\r\n");
		
		ResultSet rs= db.selectMaquinaPecasPorTipo();
		try {
			while(rs.next()) {
				body.append("<tr>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("tipomaquina")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("tipopecaoperada")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("count")+"</td>\r\n");
				body.append("</tr>\r\n");

			}
		} catch (SQLException e) {
		}
		body.append("</table></div>");
	}
}
