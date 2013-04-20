package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

/**
 * Manager class. It makes sure only one instance of LocationService, 
 * OrientationService and/or StepDetector is created. Use get*Instance() methods
 * to get an instance of the service you needed, register your listener, then
 * call start() to start the SenseLib. After SenseLib is no longer needed, 
 * remember to call stop().
 * 
 * @author Kaiwen Xu
 */
public class Sense {
	
	/**
	 * Orientation service.
	 */
	public static final int SERVICE_ORIENTATION = 0x1;
	
	/**
	 * Step detector service. Since it relies on orientation service, orientation
	 * service is implied.
	 */
	public static final int SERVICE_STEP_DETECTOR = 0x3;
	
	/**
	 * Location service. Since it relies on both orientation service and step
	 * detector service, both of them are implied.
	 */
	public static final int SERVICE_LOCATION = 0x7;
	
	/**
	 * All services available.
	 */
	public static final int SERVICE_ALL = 0xFFFFFFFF;

	private static Sense mSense;
	
	private Context mContext;
	
	private List<SensorService> mServices;
	private OrientationService mOrientationService;
	private StepDetector mStepDetector;
	private LocationService mLocationService;
	
	private Sense(Context context, int services) throws SensorNotAvailableException {
		mContext = context;
		mServices = new LinkedList<SensorService>();
		
		initializeServices(services);
	}
	
	private void initializeServices(int services) throws SensorNotAvailableException {
		if ((services & SERVICE_ORIENTATION) == SERVICE_ORIENTATION && mOrientationService == null) {
			// Initialize OrientationService.
			mOrientationService = new OrientationService(mContext);
			mServices.add(mOrientationService);
		} else if ((services & SERVICE_ORIENTATION) != SERVICE_ORIENTATION && mOrientationService != null) {
			// Remove OrientationService.
			mServices.remove(mOrientationService);
			mOrientationService.stop();
			mOrientationService = null;
		}
		
		if ((services & SERVICE_STEP_DETECTOR) == SERVICE_STEP_DETECTOR && mStepDetector == null) {
			// Initialize StepDetector.
			mStepDetector = new StepDetector(mContext, mOrientationService);
			mServices.add(mStepDetector);
		} else if ((services & SERVICE_STEP_DETECTOR) != SERVICE_STEP_DETECTOR && mStepDetector != null) {
			// Remove StepDetector.
			mServices.remove(mStepDetector);
			mStepDetector.stop();
			mStepDetector = null;
		}
		
		if ((services & SERVICE_LOCATION) == SERVICE_LOCATION && mLocationService == null) {
			mLocationService = new LocationService(mContext, mStepDetector);
			mServices.add(mLocationService);
		}
	}
	
	/**
	 * Initialize Sense with all services on.
	 * 
	 * @param context Context of current activity or application.
	 * @return Sense instance.
	 * @throws SensorNotAvailableException If sensor required by a specific 
	 * service is not present, SensorNotAvailableException will be thrown. 
	 */
	public static Sense init(Context context) throws SensorNotAvailableException {
		return init(context, SERVICE_ALL);
	}

	/**
	 * Initialize Sense with services you want to enable. If you have already
	 * initialized Sense, current initialized services will be matched with 
	 * services parameter. So if some services have already been initialized,
	 * it will be disabled if it's not indicated by services.
	 * 
	 * @param context Context of current activity or application.
	 * @param services Bit masked argument. Choose the services you want to 
	 * enable. For example, if you want to enable orientation service and 
	 * location service, you can pass the argument as 
	 * SERVICE_ORIENTATION|SERVICE_LOCATION.
	 * @return Sense instance.
	 * @throws SensorNotAvailableException If sensor required by a specific 
	 * service is not present, SensorNotAvailableException will be thrown. 
	 */
	public static Sense init(Context context, int services) throws SensorNotAvailableException {
		if (mSense == null) {
			mSense = new Sense(context.getApplicationContext(), services);
		} else {
			mSense.initializeServices(services);
		}
		
		return mSense;
	}
	
	/**
	 * Get Sense instance.
	 * 
	 * @return Sense instance.
	 */
	public static Sense getInstance() {
		if (mSense == null) {
			throw new SenseServiceException("init() has to be called.");
		}
		
		return mSense;
	}
	
	/**
	 * Check whether Sense has been initialized.
	 * 
	 * @return true if has been initialized, false otherwise.
	 */
	public static boolean hasInit() {
		return mSense != null;
	}

	/**
	 * Call to start all the services you have initialized.
	 */
	public void start() {
		for (SensorService service : mServices) {
			if (service != null) {
				service.start();
			}
		}
	}

	/**
	 * Call to stop all the services you have initialized.
	 */
	public void stop() {
		for (SensorService service : mServices) {
			if (service != null) {
				service.stop();
			}
		}
	}
	
	/**
	 * Check whether Orientation Service has been initialized.
	 * 
	 * @return true if has been initialized, false otherwise.
	 */
	public boolean hasOrientationServiceInit() {
		return mOrientationService != null;
	}

	public OrientationService getOrientationServiceInstance() {
		if (mOrientationService == null) {
			throw new SenseServiceException("Orientation service has not been initialized.");
		}
		
		return mOrientationService;
	}
	
	/**
	 * Check whether Step Detector has been initialized.
	 * 
	 * @return true if has been initialized, false otherwise.
	 */
	public boolean hasStepDetectorInit() {
		return mStepDetector != null;
	}

	public StepDetector getStepDetectorInstance() {
		if (mStepDetector == null) {
			throw new SenseServiceException("Step detector has not been initialized.");
		}
		
		return mStepDetector;
	}

	/**
	 * Check whether Location Service has been initialized.
	 * 
	 * @return true if has been initialized, false otherwise.
	 */
	public boolean hasLocationServiceInit() {
		return mLocationService != null;
	}
	
	public LocationService getLocationServiceInstance() {
		if (mLocationService == null) {
			throw new SenseServiceException("Location service has not been initialized.");
		}
		
		return mLocationService;
	}
	
	// Clean up if you forgot to do so.
	@Override
	protected void finalize() {
		stop();
	}

}
