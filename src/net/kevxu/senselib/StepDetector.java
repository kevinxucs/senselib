package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.SensorManager;

public class StepDetector {
	private final static String TAG = "StepDetector";

	private Context mContext;
	private SensorManager mSensorManager;
	private List<StepListener> mStepListeners;

	public interface StepListener {
		public void onStep();
	}

	public StepDetector(Context context) {
		mContext = context;
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		mStepListeners = new ArrayList<StepListener>();
	}

	public void addListener(StepListener stepListener) {
		mStepListeners.add(stepListener);
	}

	public void removeListeners() {
		mStepListeners.clear();
	}

}