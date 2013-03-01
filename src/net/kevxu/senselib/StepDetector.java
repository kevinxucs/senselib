package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import net.kevxu.senselib.shared.SensorNotAvailableException;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepDetector {
	private final static String TAG = "StepDetector";

	private Context mContext;
	private SensorManager mSensorManager;
	private List<StepListener> mStepListeners;

	private Sensor mLinearAccelSensor;
	private Sensor mGravitySensor;
	private LinearAccelSensorListener mLinearAccelSensorListener;
	private GravitySensorListener mGravitySensorListener;

	public interface StepListener {
		public void onStep();
	}

	public StepDetector(Context context) throws SensorNotAvailableException {
		mContext = context;
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);

		List<Sensor> liearAccelSensors = mSensorManager
				.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
		List<Sensor> gravitySensors = mSensorManager
				.getSensorList(Sensor.TYPE_GRAVITY);

		if (liearAccelSensors.size() == 0) {
			throw new SensorNotAvailableException(
					"Linear Acceleration sensors not available.");
		} else {
			mLinearAccelSensor = liearAccelSensors.get(0);
		}

		if (gravitySensors.size() == 0) {
			throw new SensorNotAvailableException(
					"Gravity sensors not available.");
		} else {
			mGravitySensor = gravitySensors.get(0);
		}

		mLinearAccelSensorListener = new LinearAccelSensorListener();
		mGravitySensorListener = new GravitySensorListener();
		mSensorManager.registerListener(mLinearAccelSensorListener,
				mLinearAccelSensor, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(mGravitySensorListener, mGravitySensor,
				SensorManager.SENSOR_DELAY_GAME);

		mStepListeners = new ArrayList<StepListener>();
	}

	public void addListener(StepListener stepListener) {
		mStepListeners.add(stepListener);
	}

	public void removeListeners() {
		mStepListeners.clear();
	}

	public void close() {
		mSensorManager.unregisterListener(mLinearAccelSensorListener);
		mSensorManager.unregisterListener(mGravitySensorListener);
	}

	public void reload() {
		mSensorManager.registerListener(mLinearAccelSensorListener,
				mLinearAccelSensor, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(mGravitySensorListener, mGravitySensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	private class LinearAccelSensorListener implements SensorEventListener {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		@Override
		public void onSensorChanged(SensorEvent event) {

		}
	}

	private class GravitySensorListener implements SensorEventListener {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		@Override
		public void onSensorChanged(SensorEvent event) {

		}
	}
}