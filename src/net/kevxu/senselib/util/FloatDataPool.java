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

import java.util.Arrays;

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

	public float getSum() {
		float sum = 0.0F;

		for (int i = 0; i < mSize; i++) {
			sum += mPool[(mStartPos + i) % mPoolSize];
		}

		return sum;
	}

	@Override
	public String toString() {
		return Arrays.toString(mPool);
	}

}