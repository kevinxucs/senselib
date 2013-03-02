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

	protected DataPool(int poolSize) {
		mPoolSize = poolSize;
		mPool = new double[3][mPoolSize];
		mStartPos = 0;
		mEndPos = 0;
	}

	

}