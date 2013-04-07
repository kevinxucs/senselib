package net.kevxu.senselib;

public class SensorNotAvailableException extends Exception {

	private static final long serialVersionUID = 478622764394890148L;
	
	private int mSensorType;

	public SensorNotAvailableException(int sensorType) {
		this.mSensorType = sensorType;
	}

	public SensorNotAvailableException(int sensorType, String detailMessage) {
		super(detailMessage);
		
		this.mSensorType = sensorType;
	}

	public int getSensorType() {
		return mSensorType;
	}

}
