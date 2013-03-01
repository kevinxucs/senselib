package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class StepDetector {
	private final static String TAG = "StepDetector";

	private Context mContext;
	private SensorManager mSensorManager;
	private List<StepListener> mStepListeners;

	private Sensor mLinearAccelSensor;
	private Sensor mGravitySensor;

	public interface StepListener {
		public void onStep();
	}

	public StepDetector(Context context) {
		mContext = context;
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);

		List<Sensor> liearAccelSensors = mSensorManager
				.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);

		mStepListeners = new ArrayList<StepListener>();
	}

	public void addListener(StepListener stepListener) {
		mStepListeners.add(stepListener);
	}

	public void removeListeners() {
		mStepListeners.clear();
	}

}