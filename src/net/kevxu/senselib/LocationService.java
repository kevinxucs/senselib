package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import net.kevxu.senselib.Sense.SenseListener;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationService implements LocationListener {

	private static final String TAG = "LocationService";

	private Context mContext;
	private LocationManager mLocationManager;
	private List<SenseListener> mSenseListeners;

	protected LocationService(Context context) {
		this(context, null);
	}

	protected LocationService(Context context, SenseListener senseListener) {
		mContext = context;
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		mSenseListeners = new ArrayList<SenseListener>();

		if (senseListener != null) {
			mSenseListeners.add(senseListener);
		}
	}

	protected void addListener(SenseListener senseListener) {
		if (senseListener != null) {
			mSenseListeners.add(senseListener);
		} else {
			throw new NullPointerException("SenseListener is null.");
		}
	}

	protected void removeListeners() {
		mSenseListeners.clear();
	}

	/**
	 * Call this when pause.
	 */
	protected void stop() {

	}

	/**
	 * Call this when resume.
	 */
	protected void start() {

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
