package net.kevxu.senselib;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Class for providing orientation information. start() and stop() must be 
 * explicitly called to start and stop the internal thread.
 * 
 * @author Kaiwen Xu
 */
public class OrientationService extends SensorService implements SensorEventListener {

	private static final String TAG = "SensorService";

	private Context mContext;
	private SensorManager mSensorManager;
	private List<OrientationServiceListener> mOrientationServiceListeners;

	private Sensor mGravitySensor;
	private Sensor mMagneticFieldSensor;

	private OrientationSensorThread mOrientationSensorThread;

	public interface OrientationServiceListener {
		
		/**
		 * Called when rotation changes.
		 * <p>
		 * Values are defined as following:<br>
		 * values[0]: azimuth, rotation around the Z axis.<br>
		 * values[1]: pitch, rotation around the X axis.<br>
		 * values[2]: roll, rotation around the Y axis.
		 * <p>
		 * The reference coordinate system is defined as following:<br>
		 * X is defined as the vector product Y·Z.<br>
		 * Y is tangential to the ground at the device's current location and 
		 * points towards the magnetic North Pole.<br>
		 * Z points towards the center of the Earth and is perpendicular to the 
		 * ground.
		 * <p>
		 * All three angles above are in radians and positive in the 
		 * counter-clockwise direction.
		 * <p>
		 * Notice that the reference coordinate system is different from the one
		 * used in 
		 * {@link OrientationServiceListener#onRotationMatrixChanged(float[], float[])}.
		 * 
		 * @param values array of float with length 3.
		 */
		public void onOrientationChanged(float[] values);

		/**
		 * Called when rotation changes.
		 * <p>
		 * R is rotation matrix, I is inclination 
		 * matrix. Both matrices transform a vector into world coordinate 
		 * system which defined as following. X is defined as the vector product
		 * Y·Z, Y is tangential to the ground at the device's current location 
		 * and points towards the magnetic North Pole and Z points towards the 
		 * sky and is perpendicular to the ground.
		 * <p>
		 * By definition, <br>
		 * [0 0 g] = R * gravity (g = magnitude of gravity)<br>
		 * [0 m 0] = I * R * geomagnetic (m = magnitude of geomagnetic * field)
		 * <p>
		 * R is the identity matrix when the device is aligned with the world's 
		 * coordinate system, that is, when the device's X axis points toward 
		 * East, the Y axis points to the North Pole and the device is facing 
		 * the sky.
		 * <p>
		 * I is a rotation matrix transforming the geomagnetic vector into the 
		 * same coordinate space as gravity (the world's coordinate space). 
		 * I is a simple rotation around the X axis.
		 * <p>
		 * Notice that the world coordinate system is different from the one 
		 * used in {@link OrientationServiceListener#onOrientationChanged(float[])}.
		 * 
		 * @param R 3 x 3 rotation matrix.
		 * @param I 3 x 3 inclination matrix.
		 */
		public void onRotationMatrixChanged(float[] R, float[] I);

		/**
		 * Called when magnetic field changes.
		 * <p>
		 * values[0], values[1] and values[2] represents the magnetic field 
		 * along the x, y and z axis correspondingly. The unit is uT.
		 * 
		 * @param values array of float length of 3.
		 */
		public void onMagneticFieldChanged(float[] values);

	}

	protected OrientationService(Context context) throws SensorNotAvailableException {
		this(context, null);
	}

	protected OrientationService(Context context, OrientationServiceListener orientationServiceListener) throws SensorNotAvailableException {
		mContext = context;
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

		List<Sensor> gravitySensors = mSensorManager.getSensorList(Sensor.TYPE_GRAVITY);
		List<Sensor> magneticFieldSensor = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		
		int notAvailabelSensors = 0;

		if (gravitySensors.size() == 0) {
			// Gravity sensor not available
			notAvailabelSensors = notAvailabelSensors | Sensor.TYPE_GRAVITY;
		} else {
			// Assume the first in the list is the default sensor
			// Assumption may not be true though
			mGravitySensor = gravitySensors.get(0);
		}

		if (magneticFieldSensor.size() == 0) {
			// Magnetic Field sensor not available
			notAvailabelSensors = notAvailabelSensors | Sensor.TYPE_MAGNETIC_FIELD;
		} else {
			// Assume the first in the list is the default sensor
			// Assumption may not be true though
			mMagneticFieldSensor = magneticFieldSensor.get(0);
		}
		
		if (notAvailabelSensors != 0) {
			// Some sensors are not available
			throw new SensorNotAvailableException(notAvailabelSensors, "Orientation Service");
		}

		mOrientationServiceListeners = new LinkedList<OrientationServiceListener>();

		if (orientationServiceListener != null) {
			mOrientationServiceListeners.add(orientationServiceListener);
		}
	}

	@Override
	protected void start() {
		if (mOrientationSensorThread == null) {
			mOrientationSensorThread = new OrientationSensorThread();
			mOrientationSensorThread.start();
			Log.i(TAG, "OrientationSensorThread started.");
		}

		if (mSensorManager == null) {
			mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		}

		mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_GAME);
		Log.i(TAG, "Gravity sensor registered.");

		mSensorManager.registerListener(this, mMagneticFieldSensor, SensorManager.SENSOR_DELAY_GAME);
		Log.i(TAG, "Magnetic field sensor registered.");

		Log.i(TAG, "OrientationService started.");
	}

	@Override
	protected void stop() {
		if (mOrientationSensorThread != null) {
			mOrientationSensorThread.terminate();
			Log.i(TAG, "Waiting for OrientationSensorThread to stop.");
			try {
				mOrientationSensorThread.join();
			} catch (InterruptedException e) {
				Log.w(TAG, e.getMessage(), e);
			}
			Log.i(TAG, "OrientationSensorThread stopped.");
			mOrientationSensorThread = null;
		}

		mSensorManager.unregisterListener(this);
		Log.i(TAG, "Sensors unregistered.");

		Log.i(TAG, "OrientationService stopped.");
	}

	private final class OrientationSensorThread extends AbstractSensorWorkerThread {

		private float[] gravity;
		private float[] geomagnetic;
		
		private float[] orientation;
		private float[] R;
		private float[] I;

		public OrientationSensorThread() {
			this(DEFAULT_INTERVAL);
		}

		public OrientationSensorThread(long interval) {
			super(interval);

			orientation = new float[3];
			R = new float[9];
			I = new float[9];
		}

		public synchronized void pushGravity(float[] values) {
			if (gravity == null) {
				gravity = new float[3];
			}

			System.arraycopy(values, 0, gravity, 0, 3);
		}

		public synchronized void pushGeomagnetic(float[] values) {
			if (geomagnetic == null) {
				geomagnetic = new float[3];
			}

			System.arraycopy(values, 0, geomagnetic, 0, 3);
		}

		public synchronized float[] getGravity() {
			return gravity;
		}

		public synchronized float[] getGeomagnetic() {
			return geomagnetic;
		}

		@Override
		public void run() {
			while (!isTerminated()) {
				if (getGravity() != null && getGeomagnetic() != null) {
					SensorManager.getRotationMatrix(R, I, getGravity(), getGeomagnetic());
					SensorManager.getOrientation(R, orientation);
				}

				for (OrientationServiceListener listener : mOrientationServiceListeners) {
					listener.onOrientationChanged(orientation);
					listener.onRotationMatrixChanged(R, I);

					if (getGeomagnetic() != null) {
						listener.onMagneticFieldChanged(getGeomagnetic());
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

	public OrientationService addListener(OrientationServiceListener orientationServiceListener) {
		if (orientationServiceListener != null) {
			mOrientationServiceListeners.add(orientationServiceListener);
			
			return this;
		} else {
			throw new NullPointerException("OrientationServiceListener is null.");
		}
	}

	protected OrientationService removeListeners() {
		mOrientationServiceListeners.clear();
		
		return this;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (mOrientationSensorThread != null) {
				Sensor sensor = event.sensor;
				int type = sensor.getType();
				if (type == Sensor.TYPE_GRAVITY) {
					mOrientationSensorThread.pushGravity(event.values);
				} else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
					mOrientationSensorThread.pushGeomagnetic(event.values);
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Not used
	}
}
