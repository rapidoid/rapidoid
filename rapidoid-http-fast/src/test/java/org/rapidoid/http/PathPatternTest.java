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
import org.rapidoid.http.impl.PathPattern;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class PathPatternTest extends TestCommons {

	@Test
	public void shouldMatchPathPatterns() {
		Map<String, String> empty = U.map();


		match("/abc", "/abc", "/abc", empty);

		match("/.*", "/.*", "/", empty);
		match("/.*", "/.*", "/abc", empty);

		noMatch("/.+", "/.+", "/");
		match("/.+", "/.+", "/abc", empty);

		String anyUri = "/(?<g1>.*)";

		match("/*", anyUri, "/", U.map(PathPattern.ANY, ""));
		match("/*", anyUri, "/xy", U.map(PathPattern.ANY, "xy"));
		match("/*", anyUri, "/a/bb/ccc", U.map(PathPattern.ANY, "a/bb/ccc"));

		String anySuffix1 = "(?:/(?<g1>.*))?";
		String anySuffix2 = "(?:/(?<g2>.*))?";

		match("/msgs/*", "/msgs" + anySuffix1, "/msgs", empty);
		match("/msgs/*", "/msgs" + anySuffix1, "/msgs/abc", U.map(PathPattern.ANY, "abc"));
		match("/msgs/*", "/msgs" + anySuffix1, "/msgs/foo/bar", U.map(PathPattern.ANY, "foo/bar"));

		match("/{cat}", "/" + g(1), "/books", U.map("cat", "books"));
		match("/{cat}/*", "/" + g(1) + anySuffix2, "/books", U.map("cat", "books"));
		match("/{cat}/*", "/" + g(1) + anySuffix2, "/books/x", U.map("cat", "books", PathPattern.ANY, "x"));
		match("/{_}/view", "/" + g(1) + "/view", "/movies/view", U.map("_", "movies"));

		match("/abc/{id}", "/abc/" + g(1), "/abc/123", U.map("id", "123"));
		match("/abc/{_x}", "/abc/" + g(1), "/abc/1-2", U.map("_x", "1-2"));
		match("/x/{a}/{b}", "/x/" + g(1) + "/" + g(2), "/x/ab/CDE", U.map("a", "ab", "b", "CDE"));
		match("/{1}/{2}/{3}", "/" + g(1) + "/" + g(2) + "/" + g(3), "/x/yy/zzz", U.map("1", "x", "2", "yy", "3", "zzz"));

		// custom regex
		match("/x/{abc:[a-zA-Z-]+}-{d}/{some_numbers:\\d+-\\d+}::{x:.*}",

			"/x/"
				+ g(1, "[a-zA-Z-]+")
				+ "-" + g(2)
				+ "/" + g(3, "\\d+-\\d+")
				+ "::" + g(4, ".*"),

			"/x/Hello-World-!!!/123-4567::zzz",

			U.map("abc", "Hello-World",
				"d", "!!!",
				"some_numbers", "123-4567",
				"x", "zzz")
		);

		noMatch("/{cat}", "/" + g(1), "/");
		noMatch("/x/{y}", "/x/" + g(1), "/x/");

		noMatch("/x/{y}", "/x/" + g(1), "/x/");
	}

	private String g(int n) {
		return g(n, "[^/]+");
	}

	private String g(int n, String regex) {
		return "(?<g" + n + ">" + regex + ")";
	}

	private void match(String ptrn, String expectedRegex, String path, Map<String, String> expectedParams) {
		PathPattern pattern = PathPattern.from(ptrn);
		String regex = pattern.getPattern().pattern();
		eq(regex, expectedRegex);

		Map<String, String> params = pattern.match(path);
		notNull(params);

		eq(params, expectedParams);
	}

	private void noMatch(String ptrn, String expectedRegex, String path) {
		PathPattern pattern = PathPattern.from(ptrn);
		String regex = pattern.getPattern().pattern();
		eq(regex, expectedRegex);

		Map<String, String> params = pattern.match(path);
		isNull(params);
	}

	@Test
	public void testPrefix() {
		eq(PathPattern.from("/*").prefix(), "/");
		eq(PathPattern.from("/x*").prefix(), "/x");
		eq(PathPattern.from("/foo/*/bar").prefix(), "/foo/");
		eq(PathPattern.from("/foo/*/bar*/*").prefix(), "/foo/");
		eq(PathPattern.from("/foo/bar*/*").prefix(), "/foo/bar");
		eq(PathPattern.from("/foo/bar/*").prefix(), "/foo/bar/");
	}

}
