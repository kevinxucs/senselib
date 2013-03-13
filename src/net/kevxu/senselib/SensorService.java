package net.kevxu.senselib;

import android.content.Context;
import android.hardware.SensorManager;

public class SensorService {

	private Context mContext;
	private SensorManager mSensorManager;

	protected interface SensorServiceListener {

	}

	protected SensorService(Context context) {
		mContext = context;
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

	}

}
