/*
 * Copyright (c) 2013 Kaiwen Xu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to 
 * deal in the Software without restriction, including without limitation the 
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or 
 * sell copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 * 
 */

package net.kevxu.senselib.util;

import java.util.ArrayList;
import java.util.Arrays;
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
		DataPool<float[]> dataPool = getDataPool(type);
		float[] copyValues = copyValues(values);
		dataPool.append(copyValues);

		return this;
	}

	protected synchronized StepDetectorDataPool addDataList(int type, List<float[]> valuesList) {
		DataPool<float[]> dataPool = getDataPool(type);
		for (float[] values : valuesList) {
			float[] copyValues = copyValues(values);
			dataPool.append(copyValues);
		}

		return this;
	}

	protected synchronized int getSize(int type) {
		DataPool<float[]> dataPool = getDataPool(type);
		int size = dataPool.size();

		return size;
	}

	protected synchronized int getPoolSize(int type) {
		DataPool<float[]> dataPool = getDataPool(type);
		int poolSize = dataPool.getPoolSize();

		return poolSize;
	}

	protected synchronized float[] get(int type, int i) {
		DataPool<float[]> dataPool = getDataPool(type);
		float[] values = dataPool.get(i);
		float[] copyValues = copyValues(values);

		return copyValues;
	}

	protected synchronized List<float[]> getList(int type, int n) {
		DataPool<float[]> dataPool = getDataPool(type);
		List<float[]> listOfValues = dataPool.getList(n);
		List<float[]> copyListOfValues = copyList(listOfValues);

		return copyListOfValues;
	}

	protected synchronized float[] getFromBack(int type, int i) {
		DataPool<float[]> dataPool = getDataPool(type);
		float[] values = dataPool.getFromBack(i);
		float[] copyValues = copyValues(values);

		return copyValues;
	}

	protected synchronized List<float[]> getListFromBack(int type, int n) {
		DataPool<float[]> dataPool = getDataPool(type);
		List<float[]> listOfValues = dataPool.getListFromBack(n);
		List<float[]> copyListOfValues = copyList(listOfValues);

		return copyListOfValues;
	}

	protected synchronized float[] getLatest(int type) {
		DataPool<float[]> dataPool = getDataPool(type);
		float[] values = dataPool.getFromBack(0);
		float[] copyValues = copyValues(values);

		return copyValues;
	}

	private float[] copyValues(float[] values) {
		float[] valuesCopy = new float[values.length];
		System.arraycopy(values, 0, valuesCopy, 0, values.length);

		return valuesCopy;
	}

	private List<float[]> copyList(List<float[]> listOfValues) {
		List<float[]> copyListOfValues = new ArrayList<float[]>(listOfValues.size());
		for (float[] values : listOfValues) {
			copyListOfValues.add(copyValues(values));
		}

		return copyListOfValues;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Linear Acceleration:\n");
		sb.append("[");
		for (int i = 0; i < mLinearAccelPool.size(); i++) {
			sb.append(Arrays.toString(mLinearAccelPool.get(i)));
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]\n");
		sb.append("Gravity:\n");
		sb.append("[");
		for (int i = 0; i < mGravityPool.size(); i++) {
			sb.append(Arrays.toString(mGravityPool.get(i)));
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]\n");

		return sb.toString();
	}

}