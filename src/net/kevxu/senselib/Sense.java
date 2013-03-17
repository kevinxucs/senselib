package net.kevxu.senselib;

import android.content.Context;
import android.location.Location;

public class Sense {

	private static final String TAG = "Sense";

	private Context mContext;
	private LocationService mLocationService;
	private OrientationService mOrientationService;

	public interface SenseListener {
		public void onLocationChanged(Location location);
	}

	public Sense(Context context) throws SensorNotAvailableException {
		mContext = context;
		mLocationService = new LocationService(mContext);
		mOrientationService = new OrientationService(mContext);

	}

	/**
	 * Call this when pause.
	 */
	public void stop() {

	}

	/**
	 * Call this when resume.
	 */
	public void start() {

	}

}
