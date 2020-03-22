package udp.estatistica;

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
	
	public void exportaFicheiro() throws IOException {
		String x=null;
		File htmlTemplateFile = new File("htmlTemplate/template.html");
		String htmlString = FileUtils.readFileToString(htmlTemplateFile,x);
		String title = "Pe&ccedil;as Descarregadas";
		StringBuilder body = new StringBuilder();


		body.append("<div class=\"container-fluid \" >\r\n" + 
				"<table  class=\"table table-dark table-hover\">\r\n" + 
				"  <tr>\r\n" + 
				"    <th class=\"text-center\">Tipo de Descarga</th>\r\n" + 
				"    <th class=\"text-center\">Tipo de pe&ccedil;a pescarregada</th>\r\n" + 
				"  </tr>\r\n");
		
		ResultSet rs= db.selectZonaDescarga();
		try {
			while(rs.next()) {
				body.append("<tr>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("tipodescarga")+"</td>\r\n");
				body.append("<td class=\"text-center\">"+rs.getString("tipodescarga")+"</td>\r\n");
				body.append("</tr>\r\n");

			}
		} catch (SQLException e) {
		}
		body.append("</table></div>");
		
		htmlString = htmlString.replace("$title", title);
		htmlString = htmlString.replace("#sec", title);
		htmlString = htmlString.replace("$body", body);
		String fileName= "pecasDescarregadas("+ Estatistica.localDate()+").html";
		System.out.println(fileName);
		File newHtmlFile = new File(fileName);
		FileUtils.writeStringToFile(newHtmlFile, htmlString, x);
	}

}
