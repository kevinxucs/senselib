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

	private Context mContext;
	private SensorManager mSensorManager;
	private List<StepListener> mStepListeners;

	private Sensor mLinearAccelSensor;
	private Sensor mGravitySensor;

	private StepDetectorDataPool mDataPool;

	private StepDetectorCalculationTask mStepDetectorCalculationTask;

	public interface StepListener {
		public void onStep();
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
			throw new SensorNotAvailableException("Linear Acceleration sensors not available.");
		} else {
			mLinearAccelSensor = liearAccelSensors.get(0);
		}

		if (gravitySensors.size() == 0) {
			throw new SensorNotAvailableException("Gravity sensors not available.");
		} else {
			mGravitySensor = gravitySensors.get(0);
		}

		mSensorManager.registerListener(this, mLinearAccelSensor, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_GAME);

		mStepListeners = new ArrayList<StepListener>();

		if (stepListener != null) {
			mStepListeners.add(stepListener);
		}

		mDataPool = new StepDetectorDataPool();

		mStepDetectorCalculationTask = new StepDetectorCalculationTask();
		mStepDetectorCalculationTask.start();
	}

	private class StepDetectorCalculationTask extends Thread {

		private volatile boolean stopped;

		public StepDetectorCalculationTask() {
			stopped = false;
		}

		public void terminate() {
			stopped = true;
		}

		@Override
		public void run() {
			while (!stopped) {
				
			}
		}

	}

	public void addListener(StepListener stepListener) {
		mStepListeners.add(stepListener);
	}

	public void addListeners(List<StepListener> stepListeners) {
		if (stepListeners.size() > 0) {
			mStepListeners.addAll(stepListeners);
		}
	}

	public void removeListeners() {
		mStepListeners.clear();
	}

	/**
	 * Call this when pause.
	 */
	public void close() {
		mStepDetectorCalculationTask.terminate();
		Log.v(TAG, "Waiting for StepDetectorCalculationTask to terminate.");
		try {
			mStepDetectorCalculationTask.join();
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		Log.v(TAG, "StepDetectorCalculationTask terminated.");
		mStepDetectorCalculationTask = null;

		mSensorManager.unregisterListener(this);
	}

	/**
	 * Call this when resume.
	 */
	public void reload() {
		mStepDetectorCalculationTask = new StepDetectorCalculationTask();
		mStepDetectorCalculationTask.start();
		Log.v(TAG, "StepDetectorCalculationTask reloaded.");

		mSensorManager.registerListener(this, mLinearAccelSensor, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Not implemented
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		mDataPool.addData(sensor.getType(), event.values);
	}

}