package net.kevxu.senselib;

import android.content.Context;
import android.hardware.SensorManager;

public class SensorService {

	private static final String TAG = "SensorService";

	private Context mContext;
	private SensorManager mSensorManager;

	protected SensorService(Context context) {
		mContext = context;
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
	}

	/**
	 * Call this when pause.
	 */
	protected void close() {

	}

	/**
	 * Call this when resume.
	 */
	protected void reload() {

	}

}
