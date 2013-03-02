package net.kevxu.senselib;

public class SensorNotAvailableException extends Exception {

	private static final long serialVersionUID = 4885953180798436336L;

	public SensorNotAvailableException() {

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

}
