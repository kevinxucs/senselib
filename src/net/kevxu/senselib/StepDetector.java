package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class StepDetector implements SensorEventListener {

	private static final String TAG = "StepDetector";

	private static final int POOL_SIZE = 500;

	private Context mContext;
	private SensorManager mSensorManager;
	private List<StepListener> mStepListeners;

	private Sensor mLinearAccelSensor;
	private Sensor mGravitySensor;

	private StepDetectorDataPool mDataPool;

	private StepDetectorCalculationThread mStepDetectorCalculationThread;

	public interface StepListener {

		public void onStep();

		// Debug purpose
		public void onValue(float value);

	}

	public StepDetector(Context context) throws SensorNotAvailableException {
		this(context, null);
	}

	public StepDetector(Context context, StepListener stepListener) throws SensorNotAvailableException {
		mContext = context;
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

		List<Sensor> liearAccelSensors = mSensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
		List<Sensor> gravitySensors = mSensorManager.getSensorList(Sensor.TYPE_GRAVITY);

		if (liearAccelSensors.size() == 0) {
			throw new SensorNotAvailableException(Sensor.TYPE_LINEAR_ACCELERATION);
		} else {
			// Assume the first in the list is the default sensor
			// Assumption may not be true though
			mLinearAccelSensor = liearAccelSensors.get(0);
		}

		if (gravitySensors.size() == 0) {
			throw new SensorNotAvailableException(Sensor.TYPE_GRAVITY);
		} else {
			// Assume the first in the list is the default sensor
			// Assumption may not be true though
			mGravitySensor = gravitySensors.get(0);
		}

		mStepListeners = new ArrayList<StepListener>();

		if (stepListener != null) {
			mStepListeners.add(stepListener);
		}

		mDataPool = new StepDetectorDataPool(POOL_SIZE);
	}

	/**
	 * Call this when resume.
	 */
	public void start() {
		if (mStepDetectorCalculationThread == null) {
			mStepDetectorCalculationThread = new StepDetectorCalculationThread();
			mStepDetectorCalculationThread.start();
			Log.i(TAG, "StepDetectorCalculationThread started.");
		}

		if (mSensorManager == null) {
			mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		}

		mSensorManager.registerListener(this, mLinearAccelSensor, SensorManager.SENSOR_DELAY_GAME);
		Log.i(TAG, "Linear acceleration sensor registered.");

		mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_GAME);
		Log.i(TAG, "Gravity sesnor registered.");
	}

	/**
	 * Call this when pause.
	 */
	public void stop() {
		mStepDetectorCalculationThread.terminate();
		Log.i(TAG, "Waiting for StepDetectorCalculationThread to stop.");
		try {
			mStepDetectorCalculationThread.join();
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		Log.i(TAG, "StepDetectorCalculationThread stopped.");
		mStepDetectorCalculationThread = null;

		mSensorManager.unregisterListener(this);
		Log.i(TAG, "Sensors unregistered.");
	}

	private final class StepDetectorCalculationThread extends AbstractSensorWorkerThread {

		private float[] linearAccel;
		private float[] gravity;

		protected StepDetectorCalculationThread() {
			this(DEFAULT_INTERVAL);
		}

		protected StepDetectorCalculationThread(long interval) {
			super(interval);
		}

		public synchronized void pushGravity(float[] values) {
			if (gravity == null) {
				gravity = new float[3];
			}

			System.arraycopy(values, 0, gravity, 0, 3);
		}

		public synchronized void pushLinearAccel(float[] values) {
			if (linearAccel == null) {
				linearAccel = new float[3];
			}

			System.arraycopy(values, 0, linearAccel, 0, 3);
		}

		public synchronized float[] getGravity() {
			return gravity;
		}

		public synchronized float[] getLinearAccel() {
			return linearAccel;
		}

		private float getAccelInGravityDirection(float[] linearAccel, float[] gravity) {
			// float gravityScalar = SensorManager.GRAVITY_EARTH;
			float gravityScalar = (float) Math.sqrt(gravity[0] * gravity[0]
					+ gravity[1] * gravity[1] + gravity[2] * gravity[2]);
			float dotProduct = linearAccel[0] * gravity[0] + linearAccel[1]
					* gravity[1] + linearAccel[2] * gravity[2];

			float aigd = (float) (dotProduct / gravityScalar);

			return aigd;
		}

		@Override
		public void run() {
			while (!isTerminated()) {
				if (mDataPool.getSize(Sensor.TYPE_LINEAR_ACCELERATION) > 0
						&& mDataPool.getSize(Sensor.TYPE_GRAVITY) > 0) {
					float[] linearAccel = mDataPool.getLatest(Sensor.TYPE_LINEAR_ACCELERATION);
					float[] gravity = mDataPool.getLatest(Sensor.TYPE_GRAVITY);

					float accelInGravityDirection = getAccelInGravityDirection(linearAccel, gravity);

					for (StepListener listener : mStepListeners) {
						listener.onValue(accelInGravityDirection);
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

	public void addListener(StepListener stepListener) {
		if (stepListener != null) {
			mStepListeners.add(stepListener);
		} else {
			throw new NullPointerException("StepListener is null.");
		}
	}

	public void addListeners(List<StepListener> stepListeners) {
		if (stepListeners.size() > 0) {
			mStepListeners.addAll(stepListeners);
		}
	}

	public void removeListeners() {
		mStepListeners.clear();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Not implemented
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			Sensor sensor = event.sensor;
			if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
				mStepDetectorCalculationThread.pushLinearAccel(event.values);
			} else if (sensor.getType() == Sensor.TYPE_GRAVITY) {
				mStepDetectorCalculationThread.pushGravity(event.values);
			}
		}
	}

}