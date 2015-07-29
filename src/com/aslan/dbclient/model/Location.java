package com.aslan.dbclient.model;

import java.sql.Timestamp;

public class Location extends Sensor {
	private Timestamp timeStamp;
	private String provider;
	private String latitude;
	private String longitude;
	private double accuracy;

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
		super.setRoundedTimeStamp(timeStamp);
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	@Override
	public String toString() {
		return provider + "\n" + latitude+ ", " + longitude + "\n" + accuracy + "\n" + timeStamp.toString();
	}

}
