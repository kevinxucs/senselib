package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import net.kevxu.senselib.StepDetector.StepListener;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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

	/**
	 * GPS disabled.
	 */
	public static int LEVEL_GPS_DISABLED = 0x0;
	
	/**
	 * GPS just enabled without status information.
	 */
	public static int LEVEL_GPS_ENABLED = 0x1;
	
	/**
	 * GPS already enabled but out of service, doesn't expect to be available
	 * in the near future.
	 */
	public static int LEVEL_GPS_ENABLED_OUT_OF_SERVICE = 0x3;
	
	/**
	 * GPS already enabled but temporarily unavailable, expect to be available
	 * shortly.
	 */
	public static int LEVEL_GPS_ENABLED_TEMPORARILY_UNAVAILABLE = 0x5;
	
	/**
	 * GPS already enabled and it's available.
	 */
	public static int LEVEL_GPS_ENABLED_AVAILABLE = 0x9;
	
	// Average step distance for human (in meters)
	private static final float CONSTANT_AVERAGE_STEP_DISTANCE = 0.7874F;
	
	// Average step time for human (in milliseconds)
	private static final long CONSTANT_AVERGAE_STEP_TIME = 500L;
	
	private static final int GPS_UPDATE_MULTIPLIER = 1;
	private static final long GPS_UPDATE_MIN_TIME = CONSTANT_AVERGAE_STEP_TIME * GPS_UPDATE_MULTIPLIER;
	private static final float GPS_UPDATE_MIN_DISTANCE = CONSTANT_AVERAGE_STEP_DISTANCE * GPS_UPDATE_MULTIPLIER;

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
		 *            service level.
		 */
		public void onServiceLevelChanged(int level);

		/**
		 * Called when user's location has changed.
		 * 
		 * @param location location.
		 */
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

		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				GPS_UPDATE_MIN_TIME, GPS_UPDATE_MIN_DISTANCE, this);
		Log.i(TAG, "GPS update registered.");

		Log.i(TAG, "LocationService started.");
	}

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

	private final class LocationServiceFusionThread extends AbstractSensorWorkerThread {
		
		private static final float ACCEPTABLE_ACCURACY = 15.0F;

		// Variables for accepting data from outside
		private Location gpsLocation;
		private float[] aiwcs;
		private volatile long steps = 0;
		
		// Internal data
		private boolean initialFix = false;
		private Location locationFix;
		private long previousSteps = 0;

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

		private synchronized Location getGPSLocation() {
			return gpsLocation;
		}
		
		public synchronized void pushStep(float[] aiwcs) {
			steps++;
			
			System.arraycopy(aiwcs, 0, this.aiwcs, 0, 3);
		}

		@Override
		public void run() {
			while (!isTerminated()) {
				Location currentLocation = getGPSLocation();
				if (currentLocation != null && currentLocation.hasAccuracy() && currentLocation.getAccuracy() <= ACCEPTABLE_ACCURACY) {
					// Acceptable GPS data
					
					if (initialFix && locationFix != null && steps - previousSteps > 0) {
						// Steps walked since last fix
						long stepsWalked = steps - previousSteps;
						
						float distanceWalked = stepsWalked * CONSTANT_AVERAGE_STEP_DISTANCE;
						
						if (distanceWalked >= locationFix.getAccuracy()) {
							Log.i(TAG, "Walked out of accuracy");
							
							// Walk out of current location accuracy range
							previousSteps = steps;
							locationFix.set(currentLocation);
							
							// Call listener
							setLocation(locationFix);
						}
					}
					
					// Initial fix
					if (!initialFix && locationFix == null) {
						locationFix = new Location(currentLocation);
						initialFix = true;
						previousSteps = steps;
						
						setLocation(locationFix);
					} else if (!initialFix) {
						locationFix.set(currentLocation);
						initialFix = true;
						previousSteps = steps;
						
						setLocation(locationFix);
					}
				}
				
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
	
	private synchronized void setLocation(Location location) {
		if (location != null) {
			for (LocationServiceListener listener : mLocationServiceListeners) {
				listener.onLocationChanged(location);
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
		synchronized (this) {
			if (provider.equals(LocationManager.GPS_PROVIDER)) {
				switch (status) {
				case LocationProvider.OUT_OF_SERVICE:
					Log.i(TAG, "GPS out of service.");
					setServiceLevel(LEVEL_GPS_ENABLED_OUT_OF_SERVICE);
					break;
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					Log.i(TAG, "GPS temporarily unavailable.");
					setServiceLevel(LEVEL_GPS_ENABLED_TEMPORARILY_UNAVAILABLE);
					break;
				case LocationProvider.AVAILABLE:
					Log.i(TAG, "GPS available.");
					setServiceLevel(LEVEL_GPS_ENABLED_AVAILABLE);
					break;
				default:
					break;
				}
			}
		}
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
				setServiceLevel(LEVEL_GPS_DISABLED);
			}
		}
	}

	@Override
	public void onStep(float[] values) {
		synchronized (this) {
			mLocationServiceFusionThread.pushStep(values);
		}
	}

	@Override
	public void onMovement(float[] values) {
		// Not used
	}

}
