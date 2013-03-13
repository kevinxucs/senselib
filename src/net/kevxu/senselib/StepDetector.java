package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepDetector implements SensorEventListener {

	private final static String TAG = "StepDetector";

	private Context mContext;
	private SensorManager mSensorManager;
	private List<StepListener> mStepListeners;

	private Sensor mLinearAccelSensor;
	private Sensor mGravitySensor;

	protected interface StepListener {
		public void onStep();
	}

	protected StepDetector(Context context) throws SensorNotAvailableException {
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
	}

	protected void addListener(StepListener stepListener) {
		mStepListeners.add(stepListener);
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

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

		} else if (sensor.getType() == Sensor.TYPE_GRAVITY) {

		}
	}

}