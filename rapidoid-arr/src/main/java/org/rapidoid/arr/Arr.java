package org.rapidoid.arr;

/*
 * #%L
 * rapidoid-arr
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.util.Arrays;

import org.rapidoid.util.U;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class Arr {

	public static int indexOf(Object[] arr, Object value) {
		for (int i = 0; i < arr.length; i++) {
			if (U.eq(arr[i], value)) {
				return i;
			}
		}
		return -1;
	}

	public static Object[] deleteAt(Object[] arr, int index) {

		Object[] res = new Object[arr.length - 1];

		if (index > 0) {
			System.arraycopy(arr, 0, res, 0, index);
		}

		if (index < arr.length - 1) {
			System.arraycopy(arr, index + 1, res, index, res.length - index);
		}

		return res;
	}

	public static <T> T[] expand(T[] arr, int factor) {
		int len = arr.length;

		arr = Arrays.copyOf(arr, len * factor);

		return arr;
	}

	public static <T> T[] expand(T[] arr, T item) {
		int len = arr.length;

		arr = Arrays.copyOf(arr, len + 1);
		arr[len] = item;

		return arr;
	}

	public static <T> T[] subarray(T[] arr, int from, int to) {
		int start = from >= 0 ? from : arr.length + from;
		int end = to >= 0 ? to : arr.length + to;

		if (start < 0) {
			start = 0;
		}

		if (end > arr.length - 1) {
			end = arr.length - 1;
		}

		U.must(start <= end, "Invalid range: expected form <= to!");

		int size = end - start + 1;

		T[] part = Arrays.copyOf(arr, size);

		System.arraycopy(arr, start, part, 0, size);

		return part;
	}

	public static boolean isArray(Object value) {
		return value != null && value.getClass().isArray();
	}

}
