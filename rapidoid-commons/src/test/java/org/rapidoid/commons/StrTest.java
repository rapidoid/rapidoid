package org.rapidoid.commons;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
@Since("5.1.0")
public class StrTest extends AbstractCommonsTest {

	@Test
	public void testTriml() {
		eq(Str.triml("/abc/", "/"), "abc/");
		eq(Str.triml("/abc/", "/a"), "bc/");
		eq(Str.triml("/abc/", "/abc/"), "");
		eq(Str.triml(".abc.", '.'), "abc.");
		eq(Str.triml("/abc/", '.'), "/abc/");
	}

	@Test
	public void testTrimr() {
		eq(Str.trimr("/abc/", "/"), "/abc");
		eq(Str.trimr("/abc/", "c/"), "/ab");
		eq(Str.trimr("/abc/", "/abc/"), "");
		eq(Str.trimr(".abc.", '.'), ".abc");
		eq(Str.trimr("/abc/", '.'), "/abc/");
	}

	@Test
	public void testInsert() {
		eq(Str.insert("", 0, "ab"), "ab");
		eq(Str.insert("a", 0, "b"), "ba");
		eq(Str.insert("a", 1, "b"), "ab");
		eq(Str.insert("abc", 2, "123"), "ab123c");
	}

}
