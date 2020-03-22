package udp.estatistica;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;

import db.DataBase;

public class ListaOrdens {
	private DataBase db;

	public ListaOrdens() {
		db= DataBase.getInstance();

	}
	public void exportaFicheiro() throws IOException {
		String x=null;
		File htmlTemplateFile = new File("htmlTemplate/template.html");
		String htmlString = FileUtils.readFileToString(htmlTemplateFile,x);
		String title = "Lista Ordens";
		StringBuilder body = new StringBuilder();


		body.append("<div class=\"container-fluid \" >\r\n" + 
				"<table  class=\"table table-dark table-hover\">\r\n" + 
				"  <tr>\r\n" + 
				"    <th class=\"text-center\">Numero Ordem</th>\r\n" + 
				"    <th class=\"text-center\">Estado</th>\r\n" + 
				"    <th class=\"text-center\">N.&ordm; pe&ccedil;as ja produzidas</th>\r\n" + 
				"    <th class=\"text-center\">N.&ordm; pe&ccedil;as em produ&ccedil;ao</th>\r\n" + 
				"    <th class=\"text-center\">N.&ordm; pe&ccedil;as pendentes</th>\r\n" + 
				"    <th class=\"text-center\">Hora entrada</th>\r\n" + 
				"    <th class=\"text-center\">Hora inicio execu&ccedil;ao</th>\r\n" + 
				"    <th class=\"text-center\">Hora fim execu&ccedil;ao</th>\r\n" + 
				"    <th class=\"text-center\">Folga</th>\r\n" + 
				"  </tr>\r\n");
		
		ResultSet rs= db.selectOrdem();
		try {
			while(rs.next()) {
				body.append("<tr>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("numeroordem")+"</td>\r\n");				
				body.append("<td class=\"text-center\">"+estadoOrdem(rs.getInt("estadoordem"))+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("pecasproduzidas")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("pecasproducao")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("pecaspendentes")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("horaentradaordem")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("horainicioexecucao")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("horafimexecucao")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("folgaexecucao")+"</td>\r\n");
				body.append("</tr>\r\n");

			}
		} catch (SQLException e) {
		}
		body.append("</table></div>");
		
		htmlString = htmlString.replace("$title", title);
		htmlString = htmlString.replace("#sec", title);
		htmlString = htmlString.replace("$body", body);
		String fileName= "ordens("+ Estatistica.localDate()+").html";
		File newHtmlFile = new File(fileName);
		FileUtils.writeStringToFile(newHtmlFile, htmlString, x);
		System.out.println("entrou");
	}
	
	private String estadoOrdem(int x) {
		if(x==0) {
			return "pendente";
		}else if(x==1) {
			return "em produ&ccedil;ao";
		}else if(x==2) {
			return "completa";
		}
		return "";
	}
}
