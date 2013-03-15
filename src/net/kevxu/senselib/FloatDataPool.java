package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Data container which stores last n data, n is defined by poolSize. It uses
 * the concept of circular array, but the underlining data container uses
 * ArrayList due to the need of support generic. This data container is NOT
 * thread-safe, use with ABSOLUTE caution.
 * 
 * @author Kaiwen Xu
 * 
 * @param <T>
 *            Type of data to be stored.
 */
public class FloatDataPool {

	private int mPoolSize;

	private float[] mPool;
	private int mStartPos;
	private int mEndPos;
	private int mSize;

	public FloatDataPool(int poolSize) {
		mPoolSize = poolSize;
		mPool = new float[poolSize];
		mStartPos = 0;
		mEndPos = 0;
		mSize = 0;
	}

	public int size() {
		return mSize;
	}

	public int getPoolSize() {
		return mPoolSize;
	}

	public void append(float value) {
		if (mSize < mPoolSize) {
			mPool[mEndPos] = value;
			mEndPos = (mEndPos + 1) % mPoolSize;
			mSize++;
		} else {
			mStartPos = (mStartPos + 1) % mPoolSize;
			mPool[mEndPos] = value;
			mEndPos = (mEndPos + 1) % mPoolSize;
		}
	}

	public float get(int i) {
		if (i < mSize) {
			return mPool[(mStartPos + i) % mPoolSize];
		} else {
			throw new IndexOutOfBoundsException("i is larger than DataPool size.");
		}
	}

	public float[] getList(int n) {
		if (n > mSize) {
			throw new IndexOutOfBoundsException("n is larger than DataPool size.");
		}

		float[] fd = new float[n];
		for (int i = 0; i < n; i++) {
			fd[i] = get(i);
		}

		return fd;
	}

	public float getFromBack(int i) {
		return get(mSize - 1 - i);
	}

	public float[] getListFromBack(int n) {
		if (n > mSize) {
			throw new IndexOutOfBoundsException("n is larger than DataPool size.");
		}

		float[] pd = new float[n];
		for (int i = 0; i < n; i++) {
			pd[i] = getFromBack(i);
		}

		return pd;
	}

	@Override
	public String toString() {
		return Arrays.toString(mPool);
	}

}