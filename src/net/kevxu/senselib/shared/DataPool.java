package net.kevxu.senselib.shared;

import java.util.Random;

public class DataPool {

	private int mPoolSize;

	private double[][] mPool;
	private int mStartPos;
	private int mEndPos;
	private int mSize;

	public DataPool(int poolSize) {
		mPoolSize = poolSize;
		mPool = new double[mPoolSize][];
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

	public void append(double[] values) {
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

	public double[] get(int i) {
		if (i < mSize) {
			return mPool[(mStartPos + i) % mPoolSize];
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("[");
		for (int i = 0; i < mSize; i++) {
			double[] values = get(i);
			str.append("[");
			for (double value : values) {
				str.append(value);
				str.append(", ");
			}
			str.delete(str.length() - 2, str.length());
			str.append("], ");
		}
		str.delete(str.length() - 2, str.length());
		str.append("]");

		return str.toString();
	}

	public static void main(String[] args) {
		Random r = new Random();
		DataPool pool = new DataPool(7);
		for (int i = 0; i < 10; i++) {
			double[] values = new double[3];
			for (int j = 0; j < 3; j++) {
				values[j] = (double) r.nextInt(10);
			}
			pool.append(values);
			System.out.println(pool);
		}
	}

}