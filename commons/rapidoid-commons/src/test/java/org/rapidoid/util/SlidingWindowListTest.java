package org.rapidoid.util;

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
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.TestCommons;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class SlidingWindowListTest extends TestCommons {

	@Test
	public void testWindowStretchingAndShrinking() {
		int capacity = 1000;
		double stretchingFactor = 0.25;  // up to 125 %
		int extra = (int) (capacity * stretchingFactor);

		SlidingWindowList<Integer> list = new SlidingWindowList<>(capacity, stretchingFactor);

		for (int i = 1; i <= 100 * capacity; i++) {
			list.add(i);

			if (i <= capacity) {
				eq(list.size(), i);

			} else {
				int size = capacity + i % extra;
				eq(list.size(), size);
			}
		}
	}

}
