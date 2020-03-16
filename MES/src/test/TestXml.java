package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import udp.Message;

class TestXml {

	@Test
	void testXmlRead() {
		 final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		 String x="Root element :ORDERS\r\n" + 
	        		"----------------------------\r\n" + 
	        		"Element :Order\r\n" + 
	        		"number: 004\r\n" + 
	        		"Tipo-TRANSFORM:\r\n" + 
	        		"From : P4\r\n" + 
	        		"To : P8\r\n" + 
	        		"Quantity : 7\r\n" + 
	        		"MaxDelay : 300\r\n" + 
	        		"\r\n" + 
	        		"Element :Order\r\n" + 
	        		"number: 005\r\n" + 
	        		"Tipo-Unload:\r\n" + 
	        		"Type: P9\r\n" + 
	        		"Destination: D2\r\n" + 
	        		"Quantity : 8\r\n" + 
	        		"\r\n" + 
	        		"Element :Order\r\n" + 
	        		"number: 006\r\n" + 
	        		"Tipo-Unload:\r\n" + 
	        		"Type: P6\r\n" + 
	        		"Destination: D3\r\n" + 
	        		"Quantity : 6\r\n" + 
	        		"\r\n" + 
	        		"Element :Order\r\n" + 
	        		"number: 023\r\n" + 
	        		"Tipo-Unload:\r\n" + 
	        		"Type: P7\r\n" + 
	        		"Destination: D1\r\n" + 
	        		"Quantity : 3\r\n" + 
	        		"\r\n" + 
	        		"request\r\n";
		 
	        System.setOut(new PrintStream(outContent));
	        Message xml=null;
			try {
				xml = new Message("127.0.0.1");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				xml.debugRead("test.xml");
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
	       
	        assertEquals(x, outContent.toString());	}

}
