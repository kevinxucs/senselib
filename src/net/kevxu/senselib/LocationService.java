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

/**
 * Class for providing location information. start() and stop() must be
 * explicitly called to start and stop the internal thread.
 * 
 * @author Kaiwen Xu
 */
public class LocationService extends SensorService implements LocationListener, StepListener {

	private static final String TAG = "LocationService";

	public static int LEVEL_GPS_NOT_ENABLED = 0;
	public static int LEVEL_GPS_ENABLED = 1;

	private Context mContext;
	private LocationManager mLocationManager;
	private List<LocationServiceListener> mLocationServiceListeners;

	private StepDetector mStepDetector;

	private LocationServiceFusionThread mLocationServiceFusionThread;

	private volatile int mServiceLevel;

	public interface LocationServiceListener {

		/**
		 * Called when service level changed. Service level includes
		 * LocationService.LEVEL_*.
		 * 
		 * @param level
		 *            Service level.
		 */
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
	@Override
	protected void start() {
		if (mLocationServiceFusionThread == null) {
			mLocationServiceFusionThread = new LocationServiceFusionThread();
			mLocationServiceFusionThread.start();
			Log.i(TAG, "LocationServiceFusionThread started.");
		}

		if (mLocationManager == null) {
			mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		}

		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0.0F, this);
		Log.i(TAG, "GPS update registered.");

		Log.i(TAG, "LocationService started.");
	}

	/**
	 * Call this when pause.
	 */
	@Override
	protected void stop() {
		mLocationServiceFusionThread.terminate();
		Log.i(TAG, "Waiting for LocationServiceFusionThread to stop.");
		try {
			mLocationServiceFusionThread.join();
		} catch (InterruptedException e) {
			Log.w(TAG, e.getMessage(), e);
		}
		Log.i(TAG, "LocationServiceFusionThread stoppped.");
		mLocationServiceFusionThread = null;

		mLocationManager.removeUpdates(this);
		Log.i(TAG, "GPS update unregistered.");

		Log.i(TAG, "LocationService stopped.");
	}

	@SuppressWarnings("unused")
	private final class LocationServiceFusionThread extends AbstractSensorWorkerThread {

		private Location gpsLocation;
		private float[] aiwcs;

		public LocationServiceFusionThread() {
			this(DEFAULT_INTERVAL);
		}

		public LocationServiceFusionThread(long interval) {
			super(interval);
			
			aiwcs = new float[3];
		}

		public synchronized void pushGPSLocation(Location location) {
			if (gpsLocation == null) {
				gpsLocation = new Location(location);
			} else {
				gpsLocation.set(location);
			}
			
			// Debug
			for (LocationServiceListener listener : mLocationServiceListeners) {
				listener.onLocationChanged(gpsLocation);
			}
		}

		public synchronized Location getGPSLocation() {
			return gpsLocation;
		}
		
		public synchronized void pushStep() {
			
		}
		
		public synchronized void pushMovement(float[] aiwcs) {
			System.arraycopy(aiwcs, 0, this.aiwcs, 0, 3);
		}

		@Override
		public void run() {
			while (!isTerminated()) {
				try {
					Thread.sleep(getInterval());
				} catch (InterruptedException e) {
					Log.w(TAG, e.getMessage(), e);
				}
			}
		}

	}

	public LocationService addListener(LocationServiceListener locationServiceListener) {
		if (locationServiceListener != null) {
			mLocationServiceListeners.add(locationServiceListener);
			
			return this;
		} else {
			throw new NullPointerException("LocationServiceListener is null.");
		}
	}

	protected LocationService removeListeners() {
		mLocationServiceListeners.clear();
		
		return this;
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
		synchronized (this) {
			mLocationServiceFusionThread.pushGPSLocation(location);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		

	}

	@Override
	public void onProviderEnabled(String provider) {
		synchronized (this) {
			if (provider.equals(LocationManager.GPS_PROVIDER)) {
				Log.i(TAG, "GPS enabled.");
				setServiceLevel(LEVEL_GPS_ENABLED);
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		synchronized (this) {
			if (provider.equals(LocationManager.GPS_PROVIDER)) {
				Log.i(TAG, "GPS disabled.");
				setServiceLevel(LEVEL_GPS_NOT_ENABLED);
			}
		}
	}

	@Override
	public void onStep(float[] values) {
		synchronized (this) {
			
		}
	}

	@Override
	public void onMovement(float[] values) {
		// Not used
	}

}
