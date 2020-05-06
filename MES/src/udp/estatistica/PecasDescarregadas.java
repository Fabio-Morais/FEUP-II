package udp.estatistica;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;

import db.DataBase;

public class PecasDescarregadas {
	private DataBase db;

	public PecasDescarregadas()  {
		db= DataBase.getInstance();
	
	}
	
	public void exportaFicheiro(boolean abrirFicheiro) throws IOException {
		String x=null;
		File htmlTemplateFile = new File("htmlTemplate/template.html");
		String htmlString = FileUtils.readFileToString(htmlTemplateFile,x);
		String title = "Pe&ccedil;as Descarregadas";
		StringBuilder body = new StringBuilder();

		numeroPecasDescarregadasTotal(body);
		numeroPecasDescarregadasPorTipo(body);
		
		htmlString = htmlString.replace("$title", title);
		htmlString = htmlString.replace("#sec", title);
		htmlString = htmlString.replace("$body", body);
		String fileName= "estatisticas/pecasDescarregadas("+ Estatistica.localDate()+").html";
		System.out.println(fileName);
		File newHtmlFile = new File(fileName);
		FileUtils.writeStringToFile(newHtmlFile, htmlString, x);
		
		 if(abrirFicheiro) {
		        Desktop desktop = Desktop.getDesktop();
		        if(newHtmlFile.exists()) desktop.open(newHtmlFile);
	        }
	}
	
	private void numeroPecasDescarregadasTotal(StringBuilder body) {
		body.append("<div class=\"container-fluid \" >\r\n" + 
				"<h2>Numero de pe&ccedil;as descarregadas</h2>\r\n"+
				"<table  class=\"table table-dark table-hover\">\r\n" + 
				"  <tr>\r\n" + 
				"    <th class=\"text-center\">Tipo de Descarga</th>\r\n" + 
				"    <th class=\"text-center\">Total de pe&ccedil;as descarregadas</th>\r\n" + 
				"  </tr>\r\n");
		
		ResultSet rs= db.selectZonaDescargaTotal();
		try {
			while(rs.next()) {
				body.append("<tr>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("tipodescarga")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("count")+"</td>\r\n");
				body.append("</tr>\r\n");

			}
		} catch (SQLException e) {
		}
		body.append("</table></div>");
	}

	private void numeroPecasDescarregadasPorTipo(StringBuilder body) {
		body.append("<div class=\"container-fluid \" >\r\n" + 
				"<h2>Numero de pe&ccedil;as descarregadas por tipo</h2>\r\n"+
				"<table  class=\"table table-dark table-hover\">\r\n" + 
				"  <tr>\r\n" + 
				"    <th class=\"text-center\">Tipo de Descarga</th>\r\n" + 
				"    <th class=\"text-center\">Tipo de pe&ccedil;a</th>\r\n" + 
				"    <th class=\"text-center\">Total de pe&ccedil;as descarregadas</th>\r\n" + 
				"  </tr>\r\n");
		
		ResultSet rs= db.selectZonaDescargaPorPeca();
		try {
			while(rs.next()) {
				body.append("<tr>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("tipodescarga")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+"P"+rs.getString("tipopecadescarregada")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("count")+"</td>\r\n");
				body.append("</tr>\r\n");

			}
		} catch (SQLException e) {
		}
		body.append("</table></div>");
	}

}
