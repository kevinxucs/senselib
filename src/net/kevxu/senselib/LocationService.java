package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import net.kevxu.senselib.StepDetector.StepListener;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationService implements LocationListener, StepListener {

	private static final String TAG = "LocationService";

	public static int LEVEL_GPS_NOT_ENABLED = 0;

	private Context mContext;
	private LocationManager mLocationManager;
	private List<LocationServiceListener> mLocationServiceListeners;

	private StepDetector mStepDetector;

	private volatile int mServiceLevel;

	public interface LocationServiceListener {

		public void onServiceLevelChanged(int level);

		public void onLocationChanged(Location location);

	}

	protected LocationService(Context context, StepDetector stepDetector) throws SensorNotAvailableException {
		this(context, stepDetector, null);
	}

	protected LocationService(Context context, StepDetector stepDetector, LocationServiceListener locationServiceListener) throws SensorNotAvailableException {
		mContext = context;
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		mLocationServiceListeners = new ArrayList<LocationServiceListener>();

		if (locationServiceListener != null) {
			mLocationServiceListeners.add(locationServiceListener);
		}

		mStepDetector = stepDetector;
		mStepDetector.addListener(this);
	}

	/**
	 * Call this when resume.
	 */
	protected void start() {
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0.0F, this);
		Log.i(TAG, "GPS update registered.");

		Log.i(TAG, "LocationService started.");
	}

	/**
	 * Call this when pause.
	 */
	protected void stop() {
		mLocationManager.removeUpdates(this);
		Log.i(TAG, "GPS update unregistered.");

		Log.i(TAG, "LocationService stopped.");
	}

	public void addListener(LocationServiceListener locationServiceListener) {
		if (locationServiceListener != null) {
			mLocationServiceListeners.add(locationServiceListener);
		} else {
			throw new NullPointerException("LocationServiceListener is null.");
		}
	}

	protected void removeListeners() {
		mLocationServiceListeners.clear();
	}

	private synchronized void setServiceLevel(int serviceLevel) {
		if (serviceLevel != mServiceLevel) {
			mServiceLevel = serviceLevel;
			for (LocationServiceListener listener : mLocationServiceListeners) {
				listener.onServiceLevelChanged(mServiceLevel);
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			setServiceLevel(LEVEL_GPS_NOT_ENABLED);
		}
	}

	@Override
	public void onStep() {
		// TODO

	}

	@Override
	public void onMovement(float[] values) {
		// TODO Auto-generated method stub

	}

}
