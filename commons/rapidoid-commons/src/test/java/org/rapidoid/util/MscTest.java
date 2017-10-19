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

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class MscTest extends TestCommons {

	@Test
	public void testExists() {
		isFalse(Msc.exists(null));

		isFalse(Msc.exists(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return null;
			}
		}));

		isFalse(Msc.exists(new Callable<Object>() {
			@SuppressWarnings("null")
			@Override
			public Object call() throws Exception {
				String s = null;
				return s.length(); // throws NPE!
			}
		}));

		isTrue(Msc.exists(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				String s = "abc";
				return s.length();
			}
		}));
	}

	@Test
	public void testUri() {
		eq(Msc.uri(""), "/");
		eq(Msc.uri("", "a"), "/a");
		eq(Msc.uri("b", ""), "/b");
		eq(Msc.uri("/", "x"), "/x");
		eq(Msc.uri("/", "/x"), "/x");
		eq(Msc.uri("/ab\\", "cd\\"), "/ab/cd");
		eq(Msc.uri("/ab", "/cd/"), "/ab/cd");
		eq(Msc.uri("/ab/", "/cd/"), "/ab/cd");
		eq(Msc.uri("x", "123", "w"), "/x/123/w");
	}

	@Test
	public void testPath() {
		eq(Msc.path(""), "");
		eq(Msc.path("", "a"), "a");
		eq(Msc.path("b", ""), "b");
		eq(Msc.path("x", "y"), "x" + File.separator + "y");

		String abcd = "/ab" + File.separator + "cd";
		eq(Msc.path("/ab\\", "cd\\"), abcd);
		eq(Msc.path("/ab/", "cd"), abcd);
	}

	@Test
	public void testInsideDocker() {
		isFalse(Msc.dockerized());
	}

	@Test
	public void testUuidBytes() {
		UUID uuid = UUID.randomUUID();
		UUID uuid2 = Msc.bytesToUUID(Msc.uuidToBytes(uuid));
		eq(uuid2, uuid);
	}

	@Test
	public void testLog2() {
		eq(Msc.log2(1), 0);
		eq(Msc.log2(2), 1);

		eq(Msc.log2(3), 2);
		eq(Msc.log2(4), 2);

		eq(Msc.log2(5), 3);
		eq(Msc.log2(8), 3);

		eq(Msc.log2(9), 4);
		eq(Msc.log2(16), 4);

		eq(Msc.log2(1024), 10);
		eq(Msc.log2(65536), 16);
		eq(Msc.log2(65536 * 1024), 26);

		eq(Msc.log2(Integer.MAX_VALUE), 31);
	}

}
