package net.kevxu.senselib;

import android.content.Context;
import android.location.Location;

public class Sense {

	private static final String TAG = "Sense";

	private Context mContext;
	private LocationService mLocationService;
	private OrientationService mOrientationService;

	// private StepDetector mStepDetector;

	public interface SenseListener {
		public void onLocationChanged(Location location);
	}

	public Sense(Context context) throws SensorNotAvailableException {
		mContext = context;
		mLocationService = new LocationService(mContext);
		mOrientationService = new OrientationService(mContext);
		// mStepDetector = new StepDetector(mContext);

	}

	/**
	 * Call this when pause.
	 */
	public void stop() {
		// mStepDetector.stop();
	}

	/**
	 * Call this when resume.
	 */
	public void start() {
		// mStepDetector.start();
	}

}
