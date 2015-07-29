package com.aslan.dbclient.model;

import java.sql.Timestamp;

public class WiFi extends Sensor {
	private Timestamp timeStamp;
	private String SSID;
	private String BSSID;
	private int level;

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
		super.setRoundedTimeStamp(timeStamp);
	}
	
	public String getSSID() {
		return SSID;
	}

	public void setSSID(String sSID) {
		SSID = sSID;
	}

	public String getBSSID() {
		return BSSID;
	}

	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return SSID + "\n" + BSSID + "\n" + level + "\n" + super.getRoundedTimeStamp().toString();
	}
}