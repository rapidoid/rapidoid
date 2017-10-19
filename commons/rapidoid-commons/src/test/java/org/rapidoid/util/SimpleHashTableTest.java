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
@Since("5.3.5")
public class SimpleHashTableTest extends TestCommons {

	@Test
	public void testDimensions() {
		checkRealCapacity(1, 1, 1);
		checkRealCapacity(1, 2, 2);
		checkRealCapacity(1, 3, 3);
		checkRealCapacity(1, 5000, 5000);

		checkRealCapacity(2, 1, 2);
		checkRealCapacity(2, 2, 2);
		checkRealCapacity(2, 3, 3);
		checkRealCapacity(2, 3000, 3000);

		checkRealCapacity(3, 1, 4);
		checkRealCapacity(3, 2, 4);
		checkRealCapacity(3, 3, 3);
		checkRealCapacity(3, 4, 4);

		checkRealCapacity(8, 5, 10);
		checkRealCapacity(15, 5, 20);
		checkRealCapacity(18, 5, 20);
		checkRealCapacity(20, 5, 20);
		checkRealCapacity(23, 5, 40);
		checkRealCapacity(23, 5, 40);
		checkRealCapacity(40, 5, 40);
	}

	private void checkRealCapacity(int capacity, int bucketSize, int expectedRealCapacity) {
		SimpleHashTable ht = new SimpleHashTable(capacity, bucketSize);
		eq(ht.bucketSize(), bucketSize);
		eq(ht.capacity(), expectedRealCapacity);
	}

}
