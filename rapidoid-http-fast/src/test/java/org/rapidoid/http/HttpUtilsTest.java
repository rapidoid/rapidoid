package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-fast
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
@Since("5.1.0")
public class HttpUtilsTest extends TestCommons {

	String[] invalidResNames = {
		null, "",
		"\\r", "\\n", "/h\\r", "/\\n", "/\\t",
		"/\\x0", "д", "\\x200",
		"?", "/?", "/f?2",
		"*", "/f*fg",
		"..", "/g..html", "", "/xy\\z", "../aa", "..\\ff",
		"/a\\b", "/a\\b/c", "/a/xx/\\b/c", "/\\", "/afbbb.asd/ff\\b/c",
		"/..", "/../", "/../ad", "/xx/../ad", "/../11/g.ad"
	};

	@Test
	public void testView() {
		eq(HttpUtils.resName("/"), "index");
		eq(HttpUtils.resName("/abc"), "abc");
		eq(HttpUtils.resName("/x/y/z"), "x/y/z");

		eq(HttpUtils.resName("/foo.html"), "foo");
		eq(HttpUtils.resName("/aa/bb.html"), "aa/bb");
		eq(HttpUtils.resName("/aa/bb-c_d11.txt"), "aa/bb-c_d11.txt");

//		eq(HttpUtils.inferViewNameFromRoutePath("/books/{x}"), "books/x");
//		eq(HttpUtils.inferViewNameFromRoutePath("/books/{id:\\d+}"), "books/id");
//		eq(HttpUtils.inferViewNameFromRoutePath("/books/{a:.*}-{b}/view"), "books/a-b/view");
	}

	@Test
	public void testUnicodeResourceNames() {
		eq(HttpUtils.resName("/Николче"), "Николче");
		eq(HttpUtils.resName("/咖啡"), "咖啡");
		eq(HttpUtils.resName("/咖啡.html"), "咖啡");
		eq(HttpUtils.resName("/foo/咖啡/bar.html"), "foo/咖啡/bar");
		eq(HttpUtils.resName("/編程/编程.html"), "編程/编程");

//		eq(HttpUtils.inferViewNameFromRoutePath("/Николче/{x}"), "Николче/x");
//		eq(HttpUtils.inferViewNameFromRoutePath("/Dlf3фок/{id:\\d+}"), "Dlf3фок/id");
//		eq(HttpUtils.inferViewNameFromRoutePath("/咖啡/{a:.*}-{b}/foo"), "咖啡/a-b/foo");
	}

	@Test
	public void testInvalidResources() {
		for (String resName : invalidResNames) {

			try {
				HttpUtils.resName(resName);
			} catch (Exception e) {
				continue;
			}

			fail("Expected error!");
		}
	}

}
