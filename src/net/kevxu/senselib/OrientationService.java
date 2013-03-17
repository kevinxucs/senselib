package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import net.kevxu.senselib.Sense.SenseListener;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class OrientationService {

	private static final String TAG = "SensorService";

	private Context mContext;
	private SensorManager mSensorManager;
	private List<SenseListener> mSenseListeners;

	private OrientationSensorThread mOrientationSensorThread;

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

	/**
	 * Call this when resume.
	 */
	protected void start() {
		if (mOrientationSensorThread == null) {
			mOrientationSensorThread = new OrientationSensorThread();
			mOrientationSensorThread.start();
			Log.i(TAG, "OrientationSensorThread started.");
		}

		if (mSensorManager == null) {
			mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		}
	}

	/**
	 * Call this when pause.
	 */
	protected void stop() {
		mOrientationSensorThread.terminate();
		Log.i(TAG, "Waiting for OrientationSensorThread to stop.");
		try {
			mOrientationSensorThread.join();
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		Log.i(TAG, "OrientationSensorThread stopped.");
		mOrientationSensorThread = null;
	}

	private final class OrientationSensorThread extends AbstractSensorWorkerThread {

		protected OrientationSensorThread() {
			super(DEFAULT_INTERVAL);
		}

		protected OrientationSensorThread(long interval) {
			super(interval);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

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

}
