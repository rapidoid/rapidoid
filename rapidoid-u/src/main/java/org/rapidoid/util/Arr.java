package org.rapidoid.util;

/*
 * #%L
 * rapidoid-u
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

}
