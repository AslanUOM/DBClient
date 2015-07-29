package com.aslan.dbclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHandler {
	public static void main(String args[]) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager
					.getConnection("jdbc:sqlite:/Studies/University/FYP/Code/sqlite/vishnu_location_db_dump_full.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Locations;");
			while (rs.next()) {
//				int id = rs.getInt("id");
				String time = rs.getString("Time");
				String provider = rs.getString("Provider");
				float latitude = rs.getFloat("Latitude");
				float longitude = rs.getFloat("Longitude");
				float accuracy = rs.getFloat("Accuracy");
				System.out.println("Time = " + time);
				System.out.println("Provider = " + provider);
				System.out.println("Latitude = " + latitude);
				System.out.println("Longitude = " + longitude);
				System.out.println("Accuracy = " + accuracy);
				System.out.println();
			}
			rs.close();
			stmt.close();
			c.close();
			System.out.println("Operation done successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
}
