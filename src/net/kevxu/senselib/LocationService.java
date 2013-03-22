package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import net.kevxu.senselib.StepDetector.StepListener;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

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

	public LocationService(Context context) throws SensorNotAvailableException {
		this(context, null);
	}

	public LocationService(Context context, LocationServiceListener locationServiceListener) throws SensorNotAvailableException {
		mContext = context;
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		mLocationServiceListeners = new ArrayList<LocationServiceListener>();

		if (locationServiceListener != null) {
			mLocationServiceListeners.add(locationServiceListener);
		}

		mStepDetector = new StepDetector(mContext, this);
	}

	/**
	 * Call this when resume.
	 */
	public void start() {
		mStepDetector.start();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0.0F, this);
	}

	/**
	 * Call this when pause.
	 */
	public void stop() {
		mStepDetector.stop();
		mLocationManager.removeUpdates(this);
	}

	public void addListener(LocationServiceListener locationServiceListener) {
		if (locationServiceListener != null) {
			mLocationServiceListeners.add(locationServiceListener);
		} else {
			throw new NullPointerException("LocationServiceListener is null.");
		}
	}

	public void removeListeners() {
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
