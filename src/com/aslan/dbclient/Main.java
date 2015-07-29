package com.aslan.dbclient;

public class Main {
	public static void main(String args[]) {
		DBHandler dbHandler = new DBHandler();
		dbHandler.readDB();
		dbHandler.processData();
	}
}
