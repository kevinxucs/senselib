package net.kevxu.senselib;

public class SensorNotAvailableException extends Exception {

	private static final long serialVersionUID = -6765170814665943123L;

	private int mSensorType;

	public SensorNotAvailableException() {
		super();
	}

	public SensorNotAvailableException(int sensorType) {
		this.mSensorType = sensorType;
	}

	public SensorNotAvailableException(String detailMessage) {
		super(detailMessage);
	}

	public SensorNotAvailableException(Throwable throwable) {
		super(throwable);
	}

	public SensorNotAvailableException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public int getSensorType() {
		return mSensorType;
	}

}
