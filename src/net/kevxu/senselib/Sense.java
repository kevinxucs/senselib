package net.kevxu.senselib;

import java.util.ArrayList;
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

	public static Sense mSense;
	
	private Context mContext;
	
	private List<SensorService> mServices;
	private OrientationService mOrientationService;
	private StepDetector mStepDetector;
	private LocationService mLocationService;
	
	/**
	 * Constructor for SenseLib. Enable all available services.
	 * 
	 * @param context Context.
	 * @throws SensorNotAvailableException
	 * 				If sensor required by a specific service is not present,
	 * 				SensorNotAvailableException will be thrown. 
	 */
	private Sense(Context context) throws SensorNotAvailableException {
		this(context, SERVICE_ALL);
	}
	
	/**
	 * Constructor for SenseLib. Choose the services you want to enable.
	 * 
	 * @param context Context.
	 * @param services
	 * 				Bit masked argument. Choose the services you want to enable.
	 * 				For example, if you want to enable orientation service and
	 * 				location service, you can pass the argument as
	 * 				SERVICE_ORIENTATION|SERVICE_LOCATION.
	 * @throws SensorNotAvailableException
	 * 				If sensor required by a specific service is not present,
	 * 				SensorNotAvailableException will be thrown. 
	 */
	private Sense(Context context, int services) throws SensorNotAvailableException {
		mContext = context;
		mServices = new ArrayList<SensorService>();

		if ((services & SERVICE_ORIENTATION) == SERVICE_ORIENTATION && mOrientationService == null) {
			mOrientationService = new OrientationService(mContext);
			mServices.add(mOrientationService);
		}
		
		if ((services & SERVICE_STEP_DETECTOR) == SERVICE_STEP_DETECTOR && mStepDetector == null) {
			mStepDetector = new StepDetector(mContext, mOrientationService);
			mServices.add(mStepDetector);
		}
		
		if ((services & SERVICE_LOCATION) == SERVICE_LOCATION && mLocationService == null) {
			mLocationService = new LocationService(mContext, mStepDetector);
			mServices.add(mLocationService);
		}
	}
	
	public static void init(Context context) throws SensorNotAvailableException {
		mSense = new Sense(context);
	}
	
	public static void init(Context context, int services) throws SensorNotAvailableException {
		mSense = new Sense(context, services);
	}
	
	public static Sense getInstance() {
		if (mSense == null) {
			throw new SenseServiceException("init() has to be called.");
		}
		
		return mSense;
	}
	
	/**
	 * Check whether Sense has been initialized.
	 * 
	 * @return
	 */
	public static boolean hasInit() {
		return mSense != null;
	}

	/**
	 * Call to start all the services you chose.
	 */
	public void start() {
		for (SensorService service : mServices) {
			if (service != null) {
				service.start();
			}
		}
	}

	/**
	 * Call to stop all the services you chose.
	 */
	public void stop() {
		for (SensorService service : mServices) {
			if (service != null) {
				service.stop();
			}
		}
	}

	public OrientationService getOrientationServiceInstance() {
		if (mOrientationService == null) {
			throw new SenseServiceException("Orientation service has not been initialized.");
		}
		
		return mOrientationService;
	}

	public StepDetector getStepDetectorInstance() {
		if (mStepDetector == null) {
			throw new SenseServiceException("Step detector has not been initialized.");
		}
		
		return mStepDetector;
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
