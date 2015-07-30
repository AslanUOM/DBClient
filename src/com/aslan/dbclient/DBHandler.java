package com.aslan.dbclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aslan.dbclient.model.Location;
import com.aslan.dbclient.model.SensorData;
import com.aslan.dbclient.model.SensorResponse;
import com.aslan.dbclient.model.WiFi;

public class DBHandler {
	public static final String LOCATION_TABLE_NAME = "Locations";
    public static final String LOCATION_COLUMN_TIME = "Time";
    public static final String LOCATION_COLUMN_PROVIDER = "Provider";
    public static final String LOCATION_COLUMN_LATITUDE = "Latitude";
    public static final String LOCATION_COLUMN_LONGITUDE = "Longitude";
    public static final String LOCATION_COLUMN_ALTITUDE = "Altitude";
    public static final String LOCATION_COLUMN_SPEED = "Speed";
    public static final String LOCATION_COLUMN_BEARING = "Bearing";
    public static final String LOCATION_COLUMN_ACCURACY = "Accuracy";
    public static final String WIFI_TABLE_NAME = "WiFi";
    public static final String WIFI_COLUMN_TIME = "Time";
    public static final String WIFI_COLUMN_SSID = "SSID";
    public static final String WIFI_COLUMN_BSSID = "BSSID";
    public static final String WIFI_COLUMN_CAPABILITIES = "Capabilities";
    public static final String WIFI_COLUMN_LEVEL = "Level";
    public static final String WIFI_COLUMN_FREQUENCY = "Frequency";
    
    private final String SELECT_LOCATION_TABLE = "SELECT "
            + LOCATION_COLUMN_TIME
            + ","
            + LOCATION_COLUMN_PROVIDER
            + ","
            + LOCATION_COLUMN_LATITUDE
            + ","
            + LOCATION_COLUMN_LONGITUDE
            + ","
            + LOCATION_COLUMN_ACCURACY
            + " FROM "
            + LOCATION_TABLE_NAME
            + ";";
    private final String SELECT_WIFI_TABLE = "SELECT "
            + WIFI_COLUMN_TIME
            + ","
            + WIFI_COLUMN_SSID
            + ","
            + WIFI_COLUMN_BSSID
            + ","
            + WIFI_COLUMN_LEVEL
            + " FROM "
            + WIFI_TABLE_NAME
            + ";";
    
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
			ResultSet rs1 = stmt.executeQuery(SELECT_LOCATION_TABLE);
			while (rs1.next()) {
				Location loc = new Location();
				loc.setTimeStamp(Timestamp.valueOf(rs1.getString(LOCATION_COLUMN_TIME)));
				loc.setProvider(rs1.getString(LOCATION_COLUMN_PROVIDER));
				loc.setLatitude(rs1.getString(LOCATION_COLUMN_LATITUDE));
				loc.setLongitude(rs1.getString(LOCATION_COLUMN_LONGITUDE));
				loc.setAccuracy(rs1.getDouble(LOCATION_COLUMN_ACCURACY));

				locationList.add(loc);
			}
			rs1.close();
			Collections.sort(locationList);
//			for (int k = 0; k < locationList.size(); k++) {
//				System.out.println(locationList.get(k).getRoundedTimeStamp() + ", " + locationList.get(k).getTimeStamp());
//			}

			// WiFi table
			ResultSet rs2 = stmt.executeQuery(SELECT_WIFI_TABLE);
			while (rs2.next()) {
				WiFi wifi = new WiFi();
				wifi.setTimeStamp(Timestamp.valueOf(rs1.getString(WIFI_COLUMN_TIME)));
				wifi.setSSID(rs2.getString(WIFI_COLUMN_SSID));
				wifi.setBSSID(rs2.getString(WIFI_COLUMN_BSSID));
				wifi.setLevel(rs2.getInt(WIFI_COLUMN_LEVEL));

				wifiList.add(wifi);
			}
			rs2.close();
			Collections.sort(wifiList);
//			for (int k = 0; k < wifiList.size(); k++) {
//				System.out.println(wifiList.get(k).getRoundedTimeStamp() + ", " + wifiList.get(k).getTimeStamp());
//			}

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

		SensorResponse response = new SensorResponse();

		for (; countLoc < locationList.size() - 1; countLoc++) {
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
			response = new SensorResponse();
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
