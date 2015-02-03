package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class ArrTest extends TestCommons {

	@Test
	public void testSubarray() {
		String[] arr = new String[] { "aa", "bb", "c", "ddd", "e" };

		String[] subarr = Arr.subarray(arr, 0, 2);
		eq(subarr, new String[] { "aa", "bb", "c" });

		subarr = Arr.subarray(arr, 2, 4);
		eq(subarr, new String[] { "c", "ddd", "e" });

		subarr = Arr.subarray(arr, 0, 4);
		eq(subarr, new String[] { "aa", "bb", "c", "ddd", "e" });

		subarr = Arr.subarray(arr, 3, 3);
		eq(subarr, new String[] { "ddd" });

		subarr = Arr.subarray(arr, 1, 3);
		eq(subarr, new String[] { "bb", "c", "ddd" });
	}

	@Test(expectedExceptions = { RuntimeException.class })
	public void testSubarrayException() {
		Arr.subarray(new String[] { "aa", "bb", "c" }, 2, 1);
	}

}
