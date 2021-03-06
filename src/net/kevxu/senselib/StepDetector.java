/*
 * Copyright (c) 2013 Kaiwen Xu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to 
 * deal in the Software without restriction, including without limitation the 
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or 
 * sell copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 * 
 */

package net.kevxu.senselib;

import java.util.LinkedList;
import java.util.List;

import net.kevxu.senselib.OrientationService.OrientationServiceListener;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Class for detecting user steps. start() and stop() must be explicitly called
 * to start and stop the internal thread.
 * 
 * @author Kaiwen Xu
 */
public class StepDetector extends SensorService implements SensorEventListener, OrientationServiceListener {

	private static final String TAG = "StepDetector";

	private Context mContext;
	private SensorManager mSensorManager;
	private List<StepListener> mStepListeners;

	private Sensor mLinearAccelSensor;
	private Sensor mGravitySensor;

	private OrientationService mOrientationService;

	private StepDetectorCalculationThread mStepDetectorCalculationThread;

	/**
	 * Used for receiving step information.
	 */
	public interface StepListener {

		/**
		 * Called when a step is detected. Movement values is passed exactly
		 * same as those in onMovement.
		 * 
		 * @param values same values passed in 
		 * {@link StepListener#onMovement(float[])}.
		 */
		public void onStep(float[] values);

		/**
		 * Called when there is a new movement data (not necessarily a step).
		 * Values passed are in world's coordinate system, where Y is tangential
		 * to the ground at the device's current location and points towards the
		 * magnetic North Pole, Z points towards the sky and is perpendicular to
		 * the ground, and X is defined as the vector product Y�Z.
		 * 
		 * @param values
		 *            array of float with length 3. X has index 0, Y has index
		 *            1, and Z has index 2.
		 */
		public void onMovement(float[] values);

	}

	protected StepDetector(Context context, OrientationService orientationService) throws SensorNotAvailableException {
		this(context, orientationService, null);
	}

	protected StepDetector(Context context, OrientationService orientationService, StepListener stepListener) throws SensorNotAvailableException {
		mContext = context;
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

		mOrientationService = orientationService;
		mOrientationService.addListener(this);

		List<Sensor> liearAccelSensors = mSensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
		List<Sensor> gravitySensors = mSensorManager.getSensorList(Sensor.TYPE_GRAVITY);
		
		int notAvailabelSensors = 0;

		if (liearAccelSensors.size() == 0) {
			// Linear Acceleration sensor not available
			notAvailabelSensors = notAvailabelSensors | Sensor.TYPE_LINEAR_ACCELERATION;
		} else {
			// Assume the first in the list is the default sensor
			// Assumption may not be true though
			mLinearAccelSensor = liearAccelSensors.get(0);
		}

		if (gravitySensors.size() == 0) {
			// Gravity sensor not available
			notAvailabelSensors = notAvailabelSensors | Sensor.TYPE_GRAVITY;
		} else {
			// Assume the first in the list is the default sensor
			// Assumption may not be true though
			mGravitySensor = gravitySensors.get(0);
		}
		
		if (notAvailabelSensors != 0) {
			// Some sensors are not available
			throw new SensorNotAvailableException(notAvailabelSensors, "StepDetector");
		}

		mStepListeners = new LinkedList<StepListener>();

		if (stepListener != null) {
			mStepListeners.add(stepListener);
		}
	}

	@Override
	protected void start() {
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

		Log.i(TAG, "StepDetector started.");
	}

	@Override
	protected void stop() {
		if (mStepDetectorCalculationThread != null) {
			mStepDetectorCalculationThread.terminate();
			Log.i(TAG, "Waiting for StepDetectorCalculationThread to stop.");
			try {
				mStepDetectorCalculationThread.join();
			} catch (InterruptedException e) {
				Log.w(TAG, e.getMessage(), e);
			}
			Log.i(TAG, "StepDetectorCalculationThread stopped.");
			mStepDetectorCalculationThread = null;
		}

		mSensorManager.unregisterListener(this);
		Log.i(TAG, "Sensors unregistered.");

		Log.i(TAG, "StepDetector stopped.");
	}

	private final class StepDetectorCalculationThread extends AbstractSensorWorkerThread {

		private static final long DEFAULT_INTERVAL = 80;
		private static final float DEFAULT_LIMIT = 0.87F;

		private final float limit;

		private float[] linearAccel;
		private float[] gravity;
		private float[] rotationMatrix;

		public StepDetectorCalculationThread() {
			this(DEFAULT_INTERVAL, DEFAULT_LIMIT);
		}

		public StepDetectorCalculationThread(long interval) {
			this(interval, DEFAULT_LIMIT);
		}

		public StepDetectorCalculationThread(long interval, float limit) {
			super(interval);

			this.limit = limit;
			this.linearAccel = new float[3];
			this.gravity = new float[3];
			this.rotationMatrix = new float[9];
		}

		public synchronized void pushLinearAccel(float[] values) {
			System.arraycopy(values, 0, linearAccel, 0, 3);
		}

		public synchronized void pushGravity(float[] values) {
			System.arraycopy(values, 0, gravity, 0, 3);
		}

		public synchronized void pushRotationMatrix(float[] R) {
			System.arraycopy(R, 0, rotationMatrix, 0, 9);
		}

		public synchronized float[] getLinearAccel() {
			return linearAccel;
		}

		public synchronized float[] getGravity() {
			return gravity;
		}

		public synchronized float[] getRotationMatrix() {
			return rotationMatrix;
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

		private void getAccelInWorldCoordinateSystem(float[] aiwcs, float[] linearAccel, float[] rotationMatrix) {
			aiwcs[0] = linearAccel[0] * rotationMatrix[0] + linearAccel[1]
					* rotationMatrix[1] + linearAccel[2] * rotationMatrix[2];
			aiwcs[1] = linearAccel[0] * rotationMatrix[3] + linearAccel[1]
					* rotationMatrix[4] + linearAccel[2] * rotationMatrix[5];
			aiwcs[2] = linearAccel[0] * rotationMatrix[6] + linearAccel[1]
					* rotationMatrix[7] + linearAccel[2] * rotationMatrix[8];
		}

		@Override
		public void run() {
			boolean readyForStep = false;
			float previousForReadyValue = 0.0F;

			float[] aiwcs = new float[3];

			while (!isTerminated()) {
				if (getGravity() != null && getLinearAccel() != null) {
					// if (getLinearAccel() != null) {
					boolean step = false;

					float[] linearAccel = getLinearAccel();
					// float[] gravity = getGravity();
					float[] rotationMatrix = getRotationMatrix();
					getAccelInWorldCoordinateSystem(aiwcs, linearAccel, rotationMatrix);

					float accelInGravityDirection = getAccelInGravityDirection(linearAccel, gravity);
					// float accelInGravityDirection = aiwcs[2];

					if (!readyForStep) {
						if (Math.abs(accelInGravityDirection) > limit) {
							previousForReadyValue = accelInGravityDirection;
							readyForStep = true;
						}
					} else {
						if ((previousForReadyValue < 0 && accelInGravityDirection > limit)
								|| (previousForReadyValue > 0 && accelInGravityDirection < -limit)) {
							step = true;
							readyForStep = false;
						}
					}

					for (StepListener listener : mStepListeners) {
						if (step) {
							listener.onStep(aiwcs);
						}
						
						listener.onMovement(aiwcs);
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

	public StepDetector addListener(StepListener stepListener) {
		if (stepListener != null) {
			mStepListeners.add(stepListener);
			
			return this;
		} else {
			throw new NullPointerException("StepListener is null.");
		}
	}

	protected StepDetector removeListeners() {
		mStepListeners.clear();
		
		return this;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Not used.
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (mStepDetectorCalculationThread != null) {
				Sensor sensor = event.sensor;
				if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
					mStepDetectorCalculationThread.pushLinearAccel(event.values);
				} else if (sensor.getType() == Sensor.TYPE_GRAVITY) {
					mStepDetectorCalculationThread.pushGravity(event.values);
				}
			}
		}
	}

	@Override
	public void onOrientationChanged(float[] values) {
		// Not used.
	}
	
	@Override
	public void onRotationMatrixChanged(float[] R, float[] I) {
		synchronized (this) {
			if (mStepDetectorCalculationThread != null) {
				mStepDetectorCalculationThread.pushRotationMatrix(R);
			}
		}
	}
	
	@Override
	public void onMagneticFieldChanged(float[] values) {
		// Not used.
	}

}