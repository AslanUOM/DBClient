package com.aslan.dbclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.aslan.dbclient.model.Location;
import com.aslan.dbclient.model.SensorData;
import com.aslan.dbclient.model.SensorResponse;
import com.aslan.dbclient.model.WiFi;

public class DBHandler {
	private List<Location> locationList = new ArrayList<>();
	private List<WiFi> wifiList = new ArrayList<>();
	private static final String[] ZERO_LENGTH_ARRAY = new String[0];

	@SuppressWarnings("deprecation")
	public void readDB() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager
					.getConnection("jdbc:sqlite:/Studies/University/FYP/Code/sqlite/vishnu_location_db_dump_full.db");
			c.setAutoCommit(false);

			System.out.println("Opened database successfully");

			stmt = c.createStatement();

			// Location table
			ResultSet rs1 = stmt.executeQuery("SELECT Time,Provider,Latitude,Longitude,Accuracy FROM Locations;");
			while (rs1.next()) {
				Location loc = new Location();
				loc.setTimeStamp(Timestamp.valueOf(rs1.getString("Time")));
				loc.setProvider(rs1.getString("Provider"));
				loc.setLatitude(rs1.getString("Latitude"));
				loc.setLongitude(rs1.getString("Longitude"));
				loc.setAccuracy(rs1.getDouble("Accuracy"));

				locationList.add(loc);
			}
			rs1.close();

			// WiFi table
			ResultSet rs2 = stmt.executeQuery("SELECT Time,SSID,BSSID,Level FROM WiFi;");
			while (rs2.next()) {
				WiFi wifi = new WiFi();
				wifi.setTimeStamp(Timestamp.valueOf(rs1.getString("Time")));
				wifi.setSSID(rs2.getString("SSID"));
				wifi.setBSSID(rs2.getString("BSSID"));
				wifi.setLevel(rs2.getInt("Level"));

				wifiList.add(wifi);
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

	public void processData() {

		int countLoc = 0;
		int countWifi = 0;

		for (; countLoc < locationList.size() - 1; countLoc++) {
			SensorResponse response = new SensorResponse();
			SensorData locData = new SensorData();
			locData.setType("location");
			locData.setSource(locationList.get(countLoc).getProvider());
			locData.setTime(locationList.get(countLoc).getTimeStamp().getTime());
			locData.setAccuracy(locationList.get(countLoc).getAccuracy());
			locData.setData(new String[] { locationList.get(countLoc).getLatitude(),
					locationList.get(countLoc).getLongitude() });
			response.addSensorData(locData);

			if (locationList.get(countLoc).equals(locationList.get(countLoc + 1))) {
				continue;
			}

			List<String> bssids = new ArrayList<>();
			for (; countWifi < wifiList.size() - 1; countWifi++) {
				if (locationList.get(countLoc).getRoundedTimeStamp().getTime() < wifiList.get(countWifi).getRoundedTimeStamp().getTime()) {
					break;
				} else if (locationList.get(countLoc).equals(wifiList.get(countWifi))) {
					bssids.add(wifiList.get(countWifi).getBSSID());
					if (wifiList.get(countWifi).equals(wifiList.get(countWifi + 1))) {
						continue;
					}
					SensorData wifiData = new SensorData();
					wifiData.setType("networks");
					wifiData.setSource("wifi");
					wifiData.setTime(wifiList.get(countWifi).getTimeStamp().getTime());
					wifiData.setAccuracy(wifiList.get(countWifi).getLevel());
					wifiData.setData(bssids.toArray(ZERO_LENGTH_ARRAY));

					response.addSensorData(wifiData);
					break;
				}
			}

			sendData(response);
			print(response);
		}

		// List<String> bssids = new ArrayList<>();
		// for (countWifi = 0; countWifi < wifiList.size() - 1; countWifi++) {
		// bssids.add(wifiList.get(countWifi).getBSSID());
		// if (wifiList.get(countWifi).equals(wifiList.get(countWifi + 1))) {
		// continue;
		// }
		// SensorData data = new SensorData();
		// data.setType("networks");
		// data.setSource("wifi");
		// data.setTime(wifiList.get(countWifi).getTimeStamp().getTime());
		// data.setAccuracy(wifiList.get(countWifi).getLevel());
		//
		// String datas[] = new String[bssids.size()];
		// for (int j = 0; j < bssids.size(); j++) {
		// datas[j] = bssids.get(j);
		// }
		// data.setData(datas);
		//
		// response.addSensorData(data);
		// sendData(response);
		//
		// response = new SensorResponse();
		// bssids.clear();
		// }
	}

	public void sendData(SensorResponse response) {
		NetworkHandler networkHandler = new NetworkHandler();
		response.setUserID("U0001");
		response.setDeviceID("D0001");

		networkHandler.postRequest(response);

	}
	
	public void print(SensorResponse res) {
		System.out.println("U_ID: " + res.getUserID());
		for(SensorData data : res.getSensorDatas()) {
			System.out.println(data);
		}
		System.out.println();
	}
}
