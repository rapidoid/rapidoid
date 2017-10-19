package org.rapidoid.arr;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

import org.junit.Test;
import org.rapidoid.commons.Arr;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.u.U;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class ArrTest extends AbstractCommonsTest {

	@Test
	public void testSubarray() {
		String[] arr = {"aa", "bb", "c", "ddd", "e"};

		String[] subarr = Arr.sub(arr, 0, 3);
		eq(subarr, U.array("aa", "bb", "c"));

		subarr = Arr.sub(arr, 2, 5);
		eq(subarr, U.array("c", "ddd", "e"));

		subarr = Arr.sub(arr, 0, 5);
		eq(subarr, U.array("aa", "bb", "c", "ddd", "e"));

		subarr = Arr.sub(arr, 3, 4);
		eq(subarr, U.array("ddd"));

		subarr = Arr.sub(arr, 1, 4);
		eq(subarr, U.array("bb", "c", "ddd"));
	}

	@Test(expected = RuntimeException.class)
	public void testSubarrayException() {
		Arr.sub(U.array("aa", "bb", "c"), 2, 1);
	}

}
