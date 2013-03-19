package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationService implements LocationListener {

	private static final String TAG = "LocationService";

	private Context mContext;
	private LocationManager mLocationManager;
	private List<LocationServiceListener> mLocationServiceListeners;

	public interface LocationServiceListener {

	}

	public LocationService(Context context) {
		this(context, null);
	}

	public LocationService(Context context, LocationServiceListener locationServiceListener) {
		mContext = context;
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		mLocationServiceListeners = new ArrayList<LocationServiceListener>();

		if (locationServiceListener != null) {
			mLocationServiceListeners.add(locationServiceListener);
		}
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}
