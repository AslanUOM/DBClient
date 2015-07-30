package com.aslan.dbclient.model;

import java.sql.Timestamp;

public class Sensor implements Comparable<Sensor> {
	private Timestamp timeStamp;
	private Timestamp roundedTimeStamp = new Timestamp(System.currentTimeMillis());

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
		setRoundedTimeStamp(timeStamp);
	}

	public Timestamp getRoundedTimeStamp() {
		return roundedTimeStamp;
	}

	public void setRoundedTimeStamp(Timestamp timeStamp) {
		roundedTimeStamp.setTime(timeStamp.getTime());
		if ((timeStamp.getMinutes() % 10) > 5) {
			roundedTimeStamp.setMinutes((((int)(timeStamp.getMinutes() / 10)) + 1) * 10);
		} else {
			roundedTimeStamp.setMinutes(((int)(timeStamp.getMinutes() / 10)) * 10);
		}
		roundedTimeStamp.setSeconds(0);
		roundedTimeStamp.setNanos(0);
	}

	@Override
	public boolean equals(Object obj) {
		Sensor sensor = (Sensor) obj;
		Long time1 = roundedTimeStamp.getTime();
		Long time2 = sensor.getRoundedTimeStamp().getTime();
		if (time1.equals(time2)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(Sensor sensor) {
		Long time1 = timeStamp.getTime();
		Long time2 = sensor.getTimeStamp().getTime();
		return time1.compareTo(time2);
	}

}
