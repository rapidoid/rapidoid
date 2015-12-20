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
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.AbstractCommonsTest;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class ResTest extends AbstractCommonsTest {

	@Test
	public void testWhereDefaultsDontExist() {
		Res file = Res.from("abc", true, "abc.html", "abc.txt", "abc.doc");

		eq(file.getShortName(), "abc");
		isTrue(file.exists());
		eq(file.getBytes(), "ABC!".getBytes());
	}

	@Test
	public void testWhereDefaultsAlsoExist() {
		Res file = Res.from("n-m", true, "nm.txt", "abc.txt", "abc.doc");

		eq(file.getShortName(), "n-m");
		isTrue(file.exists());
		eq(file.getContent(), "ABC!");
	}

	@Test
	public void testWhereOnlyDefaultsExist() {
		Res file = Res.from("n-m2", true, "non-existing", "nm.txt", "non-existing2");

		isTrue(file.exists());
		eq(file.getShortName(), "n-m2");
		eq(file.getContent(), "NMDEF");
	}

	// typically 1M reads should take less than a second
	@Test(timeout = 5000)
	public void shouldBeFast() {
		for (int i = 0; i < 900; i++) {
			// fill-in the cache (with non-existing resources)
			Res.from("abc", true, "abc.txt" + i);
		}

		// should be fast
		multiThreaded(100, 1000000, new Runnable() {
			@Override
			public void run() {
				Res file = Res.from("ABC", true, "abc.txt");
				notNull(file.getBytes());
			}
		});
	}

	@Test
	public void testWithNonexistingFiles() {
		Res file = Res.from("?", true, "some-non-existing-file");
		eq(file.getShortName(), "?");
		isFalse(file.exists());
	}

}
