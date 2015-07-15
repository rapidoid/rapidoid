package org.rapidoid.io;

/*
 * #%L
 * rapidoid-io
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

import org.junit.Test;
import org.rapidoid.test.TestCommons;

public class CachedResourceTest extends TestCommons {

	@Test
	public void testWithExistingFiles() {
		CachedResource file = CachedResource.from("abc.txt");
		isTrue(file.exists());
		eq(file.getContent(), "ABC!".getBytes());
	}

	// typically 1M reads should take less than a second
	@Test(timeout = 5000)
	public void shouldBeFast() {
		for (int i = 0; i < 900; i++) {
			// fill-in the cache (with non-existing resources)
			CachedResource.from("abc.txt" + i);
		}

		// should be fast
		for (int i = 0; i < 1000000; i++) {
			CachedResource file = CachedResource.from("abc.txt");
			notNull(file.getContent());
		}
	}

	@Test
	public void testWithNonexistingFiles() {
		CachedResource file = CachedResource.from("asfgsafd");
		isFalse(file.exists());
		isNull(file.getContent());
	}

}
