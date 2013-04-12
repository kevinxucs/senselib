package net.kevxu.senselib;

/**
 * Base class for worker thread inside sensor service class.
 * 
 * @author Kaiwen Xu
 */
abstract class AbstractSensorWorkerThread extends Thread {

	/**
	 * Default pause interval for run loop is 50 milliseconds.
	 */
	protected static final long DEFAULT_INTERVAL = 50;

	private volatile boolean terminated;

	private final long interval;

	protected AbstractSensorWorkerThread(long interval) {
		this.terminated = false;
		this.interval = interval;
	}

	/**
	 * Check whether terminated flag is set.
	 * 
	 * @return terminated flag.
	 */
	protected boolean isTerminated() {
		return terminated;
	}

	/**
	 * Get run loop pause interval.
	 * 
	 * @return pause interval.
	 */
	protected long getInterval() {
		return interval;
	}

	/**
	 * Terminate the thread.
	 */
	protected void terminate() {
		this.terminated = true;
	}

	@Override
	public abstract void run();

}
