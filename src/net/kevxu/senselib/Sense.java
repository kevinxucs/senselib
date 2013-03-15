package net.kevxu.senselib;

import android.content.Context;

public class Sense {

	private static final String TAG = "Sense";

	private Context mContext;
	private LocationService mLocationService;
	private SensorService mSensorService;

	// private StepDetector mStepDetector;

	public Sense(Context context) throws SensorNotAvailableException {
		mContext = context;
		mLocationService = new LocationService(mContext);
		mSensorService = new SensorService(mContext);
		// mStepDetector = new StepDetector(mContext);

	}

	/**
	 * Call this when pause.
	 */
	public void close() {
		// mStepDetector.close();
	}

	/**
	 * Call this when resume.
	 */
	public void reload() {
		// mStepDetector.reload();
	}

}
