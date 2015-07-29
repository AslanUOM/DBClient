package com.aslan.dbclient.model;

import java.sql.Timestamp;

public class Sensor {
	private Timestamp roundedTimeStamp;

	public Timestamp getRoundedTimeStamp() {
		return roundedTimeStamp;
	}

	public void setRoundedTimeStamp(Timestamp timeStamp) {
		roundedTimeStamp = timeStamp;
		roundedTimeStamp.setMinutes(timeStamp.getSeconds() > 30 ? timeStamp.getMinutes() + 1 : timeStamp.getMinutes());
		roundedTimeStamp.setSeconds(0);
		roundedTimeStamp.setNanos(0);
	}

	@Override
	public boolean equals(Object obj) {
		Sensor sensor = (Sensor) obj;
		if (roundedTimeStamp.equals(sensor.getRoundedTimeStamp())) {
			return true;
		} else {
			return false;
		}
	}

}
