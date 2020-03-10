package main;

import db.DataBase;

public class Main {

	public static void main(String[] args) {
		DataBase db = DataBase.getInstance();
		System.out.println(db.executeQuery("INSERT into fabrica.fabrica (npecasfabrica) VALUES (2)"));
	}

}
