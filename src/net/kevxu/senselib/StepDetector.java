package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepDetector implements SensorEventListener {

	private static final String TAG = "StepDetector";

	private Context mContext;
	private SensorManager mSensorManager;
	private List<StepListener> mStepListeners;

	private Sensor mLinearAccelSensor;
	private Sensor mGravitySensor;

	private StepDetectorDataPool mDataPool;

	protected interface StepListener {
		public void onStep();
	}

	protected StepDetector(Context context) throws SensorNotAvailableException {
		this(context, null);
	}

	protected StepDetector(Context context, StepListener stepListener) throws SensorNotAvailableException {
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
	}

	protected void addListener(StepListener stepListener) {
		if (stepListener != null)
			mStepListeners.add(stepListener);
		else
			throw new NullPointerException("stepListener can not be null.");
	}

	protected void addListeners(List<StepListener> stepListeners) {
		if (stepListeners.size() > 0) {
			mStepListeners.addAll(stepListeners);
		}
	}

	protected void removeListeners() {
		mStepListeners.clear();
	}

	protected void close() {
		mSensorManager.unregisterListener(this);
	}

	protected void reload() {
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
		if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

		} else if (sensor.getType() == Sensor.TYPE_GRAVITY) {

		}
	}

}