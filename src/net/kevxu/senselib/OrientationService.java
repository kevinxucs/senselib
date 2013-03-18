package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;

public class OrientationService {

	private static final String TAG = "SensorService";

	private Context mContext;
	private SensorManager mSensorManager;
	private List<OrientationServiceListener> mOrientationServiceListeners;

	private OrientationSensorThread mOrientationSensorThread;

	public interface OrientationServiceListener {
		public void onOrientationChanged(float[] R, float[] values);

	}

	public OrientationService(Context context) {
		this(context, null);
	}

	public OrientationService(Context context, OrientationServiceListener orientationServiceListener) {
		mContext = context;
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

		mOrientationServiceListeners = new ArrayList<OrientationServiceListener>();

		if (orientationServiceListener != null) {
			mOrientationServiceListeners.add(orientationServiceListener);
		}
	}

	/**
	 * Call this when resume.
	 */
	public void start() {
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
	public void stop() {
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

		private float[] R;
		private float[] orientation;

		protected OrientationSensorThread() {
			this(DEFAULT_INTERVAL);
		}

		protected OrientationSensorThread(long interval) {
			super(interval);

			R = new float[9];
			orientation = new float[3];
		}

		@Override
		public void run() {
			while (!isTerminated()) {
				mSensorManager.getOrientation(R, orientation);

				try {
					Thread.sleep(getInterval());
				} catch (InterruptedException e) {
					Log.w(TAG, e.getMessage(), e);
				}
			}

		}

	}

	public void addListener(OrientationServiceListener orientationServiceListener) {
		if (orientationServiceListener != null) {
			mOrientationServiceListeners.add(orientationServiceListener);
		} else {
			throw new NullPointerException("OrientationServiceListener is null.");
		}
	}

	public void removeListeners() {
		mOrientationServiceListeners.clear();
	}
}
