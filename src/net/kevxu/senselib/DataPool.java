package net.kevxu.senselib;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataPool<T> {

	private int mPoolSize;

	private List<T> mPool;
	private int mStartPos;
	private int mEndPos;
	private int mSize;

	public DataPool(int poolSize) {
		mPoolSize = poolSize;
		mPool = new ArrayList<T>(poolSize);
		mStartPos = 0;
		mEndPos = 0;
		mSize = 0;

		for (int i = 0; i < poolSize; i++) {
			mPool.add(null);
		}
	}

	public int size() {
		return mSize;
	}

	public int getPoolSize() {
		return mPoolSize;
	}

	public void append(T values) {
		if (mSize < mPoolSize) {
			mPool.set(mEndPos, values);
			mEndPos = (mEndPos + 1) % mPoolSize;
			mSize++;
		} else {
			mStartPos = (mStartPos + 1) % mPoolSize;
			mPool.set(mEndPos, values);
			mEndPos = (mEndPos + 1) % mPoolSize;
		}
	}

	public T get(int i) {
		if (i < mSize) {
			return mPool.get((mStartPos + i) % mPoolSize);
		} else {
			throw new IndexOutOfBoundsException("i is larger than DataPool size.");
		}
	}

	public List<T> getPrevious(int n) {
		if (n > mSize) {
			throw new IndexOutOfBoundsException("n is larger than DataPool size.");
		}

		List<T> pd = new ArrayList<T>();
		for (int i = 0; i < n; i++) {
			pd.add(get(mSize - 1 - i));
		}
		return pd;
	}

	@Override
	public String toString() {
		return mPool.toString();
	}

	public static void main(String[] args) {
		Random r = new Random();
		DataPool<float[]> pool = new DataPool<float[]>(5);

		// Appending data
		System.out.println("Appending data:");
		for (int i = 0; i < 10; i++) {
			float[] values = new float[3];
			for (int j = 0; j < 3; j++) {
				values[j] = (float) r.nextInt(10);
			}
			pool.append(values);

			StringBuilder sb = new StringBuilder();
			// sb.append("[");
			for (int idx = 0; idx < pool.size(); idx++) {
				sb.append("[");
				float[] getValues = pool.get(idx);
				for (int fidx = 0; fidx < 3; fidx++) {
					sb.append(getValues[fidx]);
					sb.append(", ");
				}
				sb.delete(sb.length() - 2, sb.length());
				sb.append("], ");
			}
			sb.delete(sb.length() - 2, sb.length());
			// sb.append("]");
			System.out.println(sb.toString());
		}
		System.out.println();

		// Test get
		System.out.println("Testing get:");
		for (int i = 0; i < 20; i++) {
			try {
				float[] value = pool.get(i);
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				for (int j = 0; j < 3; j++) {
					sb.append(value[j]).append(", ");
				}
				sb.delete(sb.length() - 2, sb.length());
				sb.append("]");
				System.out.println(i + ": " + sb.toString());
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		System.out.println();

		// Test getPrevious
		System.out.println("Testing getPrevious:");
		for (int n = 0; n < 10; n++) {
			try {
				List<float[]> pd = pool.getPrevious(n);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < n; i++) {
					sb.append("[");
					for (int j = 0; j < 3; j++) {
						sb.append(pd.get(i)[j]).append(", ");
					}
					sb.delete(sb.length() - 2, sb.length());
					sb.append("], ");
				}
				if (n > 0)
					sb.delete(sb.length() - 2, sb.length());
				System.out.println("previous " + n + ": " + sb.toString());
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		System.out.println();
	}
}