package com.aslan.dbclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import com.aslan.dbclient.SensorData;
import com.aslan.dbclient.SensorResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

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
			
			//Location table
			ResultSet rs1 = stmt.executeQuery("SELECT * FROM Locations;");
			while (rs1.next()) {
//				int id = rs.getInt("id");
				Timestamp timestamp = Timestamp.valueOf(rs1.getString("Time"));
				String provider = rs1.getString("Provider");
				float latitude = rs1.getFloat("Latitude");
				float longitude = rs1.getFloat("Longitude");
				float accuracy = rs1.getFloat("Accuracy");
				
				SensorData data = new SensorData();
				data.setType("location");
				data.setAccuracy(accuracy);
				data.setSource(provider);
				data.setTime(timestamp.getTime());
				data.setData(new String[] { String.valueOf(latitude), String.valueOf(longitude) });
				
				postRequest(data);
			
//				System.out.println("Time = " + time);
//				System.out.println("Provider = " + provider);
//				System.out.println("Latitude = " + latitude);
//				System.out.println("Longitude = " + longitude);
//				System.out.println("Accuracy = " + accuracy);
//				System.out.println();
			}
			rs1.close();
			
			//WiFi table
			ResultSet rs2 = stmt.executeQuery("SELECT * FROM WiFi;");
			while (rs2.next()) {
//				int id = rs.getInt("id");
				Timestamp timestamp = Timestamp.valueOf(rs2.getString("Time"));
				String SSID = rs2.getString("SSID");
				String BSSID = rs2.getString("BSSID");
//				float latitude = rs2.getFloat("Latitude");
//				float longitude = rs2.getFloat("Longitude");
//				float accuracy = rs2.getFloat("Accuracy");
				
				SensorData data = new SensorData();
				data.setType("networks");
				data.setAccuracy(100.00);
				data.setSource("wifi");
				data.setTime(timestamp.getTime());
				data.setData(new String[] { String.valueOf(SSID), String.valueOf(BSSID) });
				
				postRequest(data);
			}
			rs2.close();
			
			stmt.close();
			c.close();
			System.out.println("Operation done successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
	
	public static void getRequest() {
		try {
			Client client = Client.create();
			WebResource webResource = client.resource("http://localhost:8080/ConTra/sensordatareceiver/save");
			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			String output = response.getEntity(String.class);
			System.out.println("Output from Server .... \n");
			System.out.println(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void postRequest(SensorData data) {
		SensorResponse sensorResponse = new SensorResponse();
		sensorResponse.setUserID("U0001");
		sensorResponse.setDeviceID("D0001");
		sensorResponse.addSensorData(data);

		try {

			Client client = Client.create();
			WebResource webResource = client.resource("http://localhost:8080/ConTra/sensordatareceiver/save");
			String input = "{\"singer\":\"Metallica\",\"title\":\"Fade To Black\"}";
			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, sensorResponse);
			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
