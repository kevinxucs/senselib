package net.kevxu.senselib;

public class StepDetectorDataPool {

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

}

class DataPool {

	private int mPoolSize;

	private double[][] mPool;
	int mStartPos;
	int mEndPos;
	int mSize;

	protected DataPool(int poolSize) {
		mPoolSize = poolSize;
		mPool = new double[mPoolSize][];
		mStartPos = 0;
		mEndPos = 0;
		mSize = 0;
	}

	int getSize() {
		return mSize;
	}

	int getPoolSize() {
		return mPoolSize;
	}

	void offer(double[] values) {
		if (mSize < mPoolSize) {
			mPool[mEndPos] = values;
			mEndPos = (mEndPos + 1) % mPoolSize;
			mSize++;
		} else {
			mStartPos = (mStartPos + 1) % mPoolSize;
			mPool[mEndPos] = values;
			mEndPos = (mEndPos + 1) % mPoolSize;
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[");

		return str.toString();
	}

}