package net.kevxu.senselib;

import android.content.Context;
import android.location.LocationManager;

public class LocationService {

	private Context mContext;
	private LocationManager mLocationManager;

	protected LocationService(Context context) {
		mContext = context;
		mLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

	}

}
