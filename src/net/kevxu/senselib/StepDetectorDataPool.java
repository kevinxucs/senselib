package net.kevxu.senselib;

import java.util.List;

import android.hardware.Sensor;
import android.util.Log;

public class StepDetectorDataPool {

	private static final String TAG = "StepDetectorDataPool";

	private final static int DEFAULT_POOL_SIZE = 500;

	private int mPoolSize;
	private DataPool mLinearAccelPool;
	private DataPool mGravityPool;

	protected StepDetectorDataPool() {
		this(DEFAULT_POOL_SIZE);
	}

	protected StepDetectorDataPool(int poolSize) {
		mPoolSize = poolSize;
		mLinearAccelPool = new DataPool(mPoolSize);
		mGravityPool = new DataPool(mPoolSize);
	}

	protected DataPool getDataPool(int type) {
		if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
			return mLinearAccelPool;
		} else if (type == Sensor.TYPE_GRAVITY) {
			return mGravityPool;
		} else {
			IllegalArgumentException e = new IllegalArgumentException("No such type "
					+ type + ".");
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	protected StepDetectorDataPool addData(int type, float[] values) {
		DataPool dataPool = getDataPool(type);
		dataPool.append(values);

		return this;
	}

	protected StepDetectorDataPool addDataList(int type, List<float[]> valuesList) {
		DataPool dataPool = getDataPool(type);
		for (float[] values : valuesList) {
			dataPool.append(values);
		}

		return this;
	}

	protected int getSize(int type) {
		DataPool dataPool = getDataPool(type);
		return dataPool.size();
	}

	protected float[] get(int type, int i) {
		DataPool dataPool = getDataPool(type);
		return dataPool.get(i);
	}

	protected List<float[]> getPrevious(int type, int n) {
		DataPool dataPool = getDataPool(type);
		return dataPool.getPrevious(n);
	}

}