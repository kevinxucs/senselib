package net.kevxu.senselib;

/**
 * Base class for all the sensor services.
 * 
 * @author Kaiwen Xu
 */
public abstract class SensorService {
	
	/**
	 * Call this when start or resume.
	 */
	protected abstract void start();
	
	/**
	 * Call this when pause.
	 */
	protected abstract void stop();

}
