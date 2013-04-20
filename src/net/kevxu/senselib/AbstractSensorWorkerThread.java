/*
 * Copyright (c) 2013 Kaiwen Xu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to 
 * deal in the Software without restriction, including without limitation the 
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or 
 * sell copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 * 
 */

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
