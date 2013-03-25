package net.kevxu.senselib;

import android.content.Context;

/**
 * Manager class. Makes sure only one instance of LocationService, 
 * OrientationService and StepDetector is created. Use get*Instance() methods
 * to get an instance of the service you needed, register your listener, then
 * call start() to start the SenseLib. After SenseLib is no longer needed, 
 * remember to call stop().
 * 
 * @author Kaiwen Xu
 */
public class Sense {

	private Context mContext;

	private OrientationService mOrientationService;
	private StepDetector mStepDetector;
	private LocationService mLocationService;

	public Sense(Context context) throws SensorNotAvailableException {
		mContext = context;

		mOrientationService = new OrientationService(mContext);
		mStepDetector = new StepDetector(mContext, mOrientationService);
		mLocationService = new LocationService(mContext, mStepDetector);
	}

	public void start() {
		mOrientationService.start();
		mStepDetector.start();
		mLocationService.start();
	}

	public void stop() {
		mLocationService.stop();
		mStepDetector.stop();
		mOrientationService.stop();
	}

	public OrientationService getOrientationServiceInstance() {
		return mOrientationService;
	}

	public StepDetector getStepDetectorInstance() {
		return mStepDetector;
	}

	public LocationService getLocationServiceInstance() {
		return mLocationService;
	}

}
