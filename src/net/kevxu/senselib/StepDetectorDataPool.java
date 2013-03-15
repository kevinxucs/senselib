package net.kevxu.senselib;

import java.util.List;

import android.hardware.Sensor;
import android.util.Log;

public class StepDetectorDataPool {

	private static final String TAG = "StepDetectorDataPool";

	private final static int DEFAULT_POOL_SIZE = 500;

	private int mPoolSize;
	private DataPool<float[]> mLinearAccelPool;
	private DataPool<float[]> mGravityPool;

	protected StepDetectorDataPool() {
		this(DEFAULT_POOL_SIZE);
	}

	protected StepDetectorDataPool(int poolSize) {
		mPoolSize = poolSize;
		mLinearAccelPool = new DataPool<float[]>(mPoolSize);
		mGravityPool = new DataPool<float[]>(mPoolSize);
	}

	protected synchronized DataPool<float[]> getDataPool(int type) {
		if (type == Sensor.TYPE_LINEAR_ACCELERATION)
			return mLinearAccelPool;
		else if (type == Sensor.TYPE_GRAVITY)
			return mGravityPool;
		else {
			IllegalArgumentException e = new IllegalArgumentException("No such type "
					+ type + ".");
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	protected synchronized StepDetectorDataPool addData(int type, float[] values) {
		getDataPool(type).append(values);

		return this;
	}

	protected synchronized StepDetectorDataPool addDataList(int type, List<float[]> valuesList) {
		DataPool<float[]> dataPool = getDataPool(type);
		for (float[] values : valuesList) {
			dataPool.append(values);
		}

		return this;
	}

	protected synchronized int getSize(int type) {
		return getDataPool(type).size();
	}

	protected synchronized int getPoolSize(int type) {
		return getDataPool(type).getPoolSize();
	}

	protected synchronized float[] get(int type, int i) {
		return getDataPool(type).get(i);
	}

	protected synchronized List<float[]> getList(int type, int n) {
		return getDataPool(type).getList(n);
	}

	protected synchronized float[] getFromBack(int type, int i) {
		return getDataPool(type).getFromBack(i);
	}

	protected synchronized List<float[]> getListFromBack(int type, int n) {
		return getDataPool(type).getListFromBack(n);
	}

	protected synchronized float[] getLatest(int type) {
		return getDataPool(type).getFromBack(0);
	}

}