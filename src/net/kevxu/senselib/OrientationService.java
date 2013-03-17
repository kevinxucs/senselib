package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import net.kevxu.senselib.Sense.SenseListener;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OrientationService {

	private static final String TAG = "SensorService";

	private Context mContext;
	private SensorManager mSensorManager;
	private List<SenseListener> mSenseListeners;

	protected OrientationService(Context context) {
		this(context, null);
	}

	protected OrientationService(Context context, SenseListener senseListener) {
		mContext = context;
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

		mSenseListeners = new ArrayList<SenseListener>();

		if (senseListener != null) {
			mSenseListeners.add(senseListener);
		}
	}

	protected void addListener(SenseListener senseListener) {
		if (senseListener != null) {
			mSenseListeners.add(senseListener);
		} else {
			throw new NullPointerException("SenseListener is null.");
		}
	}

	protected void removeListeners() {
		mSenseListeners.clear();
	}

	private final class OrientationSensorThread extends Thread {
		
		private volatile boolean terminated;
		
		
		
		
	}

	/**
	 * Call this when pause.
	 */
	protected void stop() {

	}

	/**
	 * Call this when resume.
	 */
	protected void start() {

	}

}
