package udp.estatistica;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;

import db.DataBase;
import db.Ordem;

public class ListaOrdens {
	private DataBase db;

	public ListaOrdens() {
		db= DataBase.getInstance();

	}
	public void exportaFicheiro(boolean abrirFicheiro) throws IOException {
		String x=null;
		File htmlTemplateFile = new File("htmlTemplate/template.html");
		String htmlString = FileUtils.readFileToString(htmlTemplateFile,x);
		String title = "Lista Ordens";
		StringBuilder body = new StringBuilder();

		ordens(body);
		
		
		htmlString = htmlString.replace("$title", title);
		htmlString = htmlString.replace("#sec", title);
		htmlString = htmlString.replace("$body", body);
		String fileName= "estatisticas/ordens("+ Estatistica.localDate()+").html";
		File newHtmlFile = new File(fileName);
		FileUtils.writeStringToFile(newHtmlFile, htmlString, x);
		//first check if Desktop is supported by Platform or not
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }
        
        if(abrirFicheiro) {
	        Desktop desktop = Desktop.getDesktop();
	        if(newHtmlFile.exists()) desktop.open(newHtmlFile);
        }
        
       
	}
	private void ordens(StringBuilder body) {
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
				"    <th class=\"text-center\">Folga (s)</th>\r\n" + 
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
				body.append("<td class=\"text-center\">"+converteNull(rs.getString("horainicioexecucao"))+"</td>\r\n");
				body.append("<td class=\"text-center\">"+converteNull(rs.getString("horafimexecucao"))+"</td>\r\n");
				body.append("<td class=\"text-center\">"+calculaFolga(rs.getInt("estadoordem"), rs.getString("atrasomaximo"), 
						rs.getString("horaentradaordem"),rs.getString("horafimexecucao"), rs.getString("folgaexecucao") )+"</td>\r\n");
				body.append("</tr>\r\n");

			}
		} catch (SQLException e) {
		}
		body.append("</table></div>");
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
	
	private String converteNull(String x) {
		if(x==null) {
			return "---";
		}
		else 
			return x;
	}
	/**
	 * entradaData (dd/MM/yy HH:mm:ss)
	 * */
	private int calculaFolga(int estado, String folgaMaxima, String entradaData, String fimData, String folgaExecucao) {
		if(folgaMaxima == null)
			return 0;
		int folga=0;
		if(estado==1) {
			return (int) Ordem.calculaTempoRestante(Ordem.converteData3(entradaData), Integer.valueOf(folgaMaxima));
			
		}else if(estado==2) {
			String dataLimite = Ordem.addDate(Ordem.converteData3(entradaData), Integer.valueOf(folgaMaxima));
			return Ordem.calculaDiferenca(Ordem.converteData(dataLimite), fimData);
		}else if(estado==0) {
			return Integer.valueOf(folgaExecucao);
		}
		return folga;
	}
}
