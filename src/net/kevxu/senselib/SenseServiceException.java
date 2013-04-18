package net.kevxu.senselib;

public class SenseServiceException extends RuntimeException {

	private static final long serialVersionUID = -2588075945458021L;

	public SenseServiceException() {
	}

	public SenseServiceException(String detailMessage) {
		super(detailMessage);
	}

	public SenseServiceException(Throwable throwable) {
		super(throwable);
	}

	public SenseServiceException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
