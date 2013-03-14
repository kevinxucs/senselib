package net.kevxu.senselib;

import java.util.List;

import android.hardware.Sensor;

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

	protected StepDetectorDataPool addData(int type, float[] values) {
		if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
			mLinearAccelPool.append(values);
		} else if (type == Sensor.TYPE_GRAVITY) {
			mGravityPool.append(values);
		}

		return this;
	}

	protected StepDetectorDataPool addDataList(int type, List<float[]> valuesList) {
		if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
			for (float[] values : valuesList) {
				mLinearAccelPool.append(values);
			}
		} else if (type == Sensor.TYPE_GRAVITY) {
			for (float[] values : valuesList) {
				mGravityPool.append(values);
			}
		}

		return this;
	}
}