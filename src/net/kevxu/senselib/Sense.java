package net.kevxu.senselib;

import android.content.Context;

public class Sense {

	private static final String TAG = "Sense";

	private Context mContext;
	private LocationService mLocationService;
	private SensorService mSensorService;

	public Sense(Context context) {
		mContext = context;
		mLocationService = new LocationService(mContext);
		mSensorService = new SensorService(mContext);

	}

}
