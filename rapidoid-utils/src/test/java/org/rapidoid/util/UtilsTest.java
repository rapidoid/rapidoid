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
import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
public class UtilsTest extends TestCommons {

	@Test
	public void testExclude() {
		String[] arr = { "a", "b", "c" };
		eq(UTILS.exclude(arr, "a"), U.array("b", "c"));
		eq(UTILS.exclude(arr, "b"), U.array("a", "c"));
		eq(UTILS.exclude(arr, "c"), U.array("a", "b"));
	}

	@Test
	public void testInclude() {
		String[] arr = { "a", "b", "c" };
		eq(UTILS.include(arr, "a"), U.array("a", "b", "c"));
		eq(UTILS.include(arr, "d"), U.array("a", "b", "c", "d"));
	}

}
