package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.Arrays;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Arr extends RapidoidThing {

	/* indexOf */

	public static int indexOf(Object[] arr, Object value) {
		for (int i = 0; i < arr.length; i++) {
			if (U.eq(arr[i], value)) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(boolean[] arr, boolean value) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == value) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(byte[] arr, byte value) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == value) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(char[] arr, char value) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == value) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(int[] arr, int value) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == value) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(long[] arr, long value) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == value) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(float[] arr, float value) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == value) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(double[] arr, double value) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == value) {
				return i;
			}
		}
		return -1;
	}

	/* contains */

	public static <T> boolean contains(T[] arr, T value) {
		return indexOf(arr, value) >= 0;
	}

	public static boolean contains(boolean[] arr, boolean value) {
		return indexOf(arr, value) >= 0;
	}

	public static boolean contains(byte[] arr, byte value) {
		return indexOf(arr, value) >= 0;
	}

	public static boolean contains(char[] arr, char value) {
		return indexOf(arr, value) >= 0;
	}

	public static boolean contains(int[] arr, int value) {
		return indexOf(arr, value) >= 0;
	}

	public static boolean contains(long[] arr, long value) {
		return indexOf(arr, value) >= 0;
	}

	public static boolean contains(float[] arr, float value) {
		return indexOf(arr, value) >= 0;
	}

	public static boolean contains(double[] arr, double value) {
		return indexOf(arr, value) >= 0;
	}

	/* sub */

	public static <T> T[] sub(T[] arr, int from, int to) {
		int start = from >= 0 ? from : arr.length + from;
		int end = to >= 0 ? to : arr.length + to;
		return Arrays.copyOfRange(arr, start, end);
	}

	public static boolean[] sub(boolean[] arr, int from, int to) {
		int start = from >= 0 ? from : arr.length + from;
		int end = to >= 0 ? to : arr.length + to;
		return Arrays.copyOfRange(arr, start, end);
	}

	public static byte[] sub(byte[] arr, int from, int to) {
		int start = from >= 0 ? from : arr.length + from;
		int end = to >= 0 ? to : arr.length + to;
		return Arrays.copyOfRange(arr, start, end);
	}

	public static char[] sub(char[] arr, int from, int to) {
		int start = from >= 0 ? from : arr.length + from;
		int end = to >= 0 ? to : arr.length + to;
		return Arrays.copyOfRange(arr, start, end);
	}

	public static int[] sub(int[] arr, int from, int to) {
		int start = from >= 0 ? from : arr.length + from;
		int end = to >= 0 ? to : arr.length + to;
		return Arrays.copyOfRange(arr, start, end);
	}

	public static long[] sub(long[] arr, int from, int to) {
		int start = from >= 0 ? from : arr.length + from;
		int end = to >= 0 ? to : arr.length + to;
		return Arrays.copyOfRange(arr, start, end);
	}

	public static float[] sub(float[] arr, int from, int to) {
		int start = from >= 0 ? from : arr.length + from;
		int end = to >= 0 ? to : arr.length + to;
		return Arrays.copyOfRange(arr, start, end);
	}

	public static double[] sub(double[] arr, int from, int to) {
		int start = from >= 0 ? from : arr.length + from;
		int end = to >= 0 ? to : arr.length + to;
		return Arrays.copyOfRange(arr, start, end);
	}

	/* concat */

	public static <T> T[] concat(T[] left, T... right) {
		T[] concatenated = Arrays.copyOf(left, left.length + right.length);
		System.arraycopy(right, 0, concatenated, left.length, right.length);
		return concatenated;
	}

	public static boolean[] concat(boolean[] left, boolean... right) {
		boolean[] concatenated = Arrays.copyOf(left, left.length + right.length);
		System.arraycopy(right, 0, concatenated, left.length, right.length);
		return concatenated;
	}

	public static byte[] concat(byte[] left, byte... right) {
		byte[] concatenated = Arrays.copyOf(left, left.length + right.length);
		System.arraycopy(right, 0, concatenated, left.length, right.length);
		return concatenated;
	}

	public static char[] concat(char[] left, char... right) {
		char[] concatenated = Arrays.copyOf(left, left.length + right.length);
		System.arraycopy(right, 0, concatenated, left.length, right.length);
		return concatenated;
	}

	public static int[] concat(int[] left, int... right) {
		int[] concatenated = Arrays.copyOf(left, left.length + right.length);
		System.arraycopy(right, 0, concatenated, left.length, right.length);
		return concatenated;
	}

	public static long[] concat(long[] left, long... right) {
		long[] concatenated = Arrays.copyOf(left, left.length + right.length);
		System.arraycopy(right, 0, concatenated, left.length, right.length);
		return concatenated;
	}

	public static float[] concat(float[] left, float... right) {
		float[] concatenated = Arrays.copyOf(left, left.length + right.length);
		System.arraycopy(right, 0, concatenated, left.length, right.length);
		return concatenated;
	}

	public static double[] concat(double[] left, double... right) {
		double[] concatenated = Arrays.copyOf(left, left.length + right.length);
		System.arraycopy(right, 0, concatenated, left.length, right.length);
		return concatenated;
	}


}
