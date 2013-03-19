package net.kevxu.senselib;

abstract class AbstractSensorWorkerThread extends Thread {

	protected static final long DEFAULT_INTERVAL = 50;

	private volatile boolean terminated;

	private long interval;

	protected AbstractSensorWorkerThread(long interval) {
		this.terminated = false;
		this.interval = interval;
	}

	protected boolean isTerminated() {
		return terminated;
	}

	protected long getInterval() {
		return interval;
	}

	protected void terminate() {
		this.terminated = true;
	}

	@Override
	public abstract void run();

}
