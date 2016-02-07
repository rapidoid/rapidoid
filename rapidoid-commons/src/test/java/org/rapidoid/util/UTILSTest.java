package org.rapidoid.util;

import org.junit.Test;
import org.rapidoid.test.TestCommons;

import java.io.File;
import java.util.concurrent.Callable;

public class UTILSTest extends TestCommons {

	@Test
	public void testExists() {
		isFalse(UTILS.exists(null));

		isFalse(UTILS.exists(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return null;
			}
		}));

		isFalse(UTILS.exists(new Callable<Object>() {
			@SuppressWarnings("null")
			@Override
			public Object call() throws Exception {
				String s = null;
				return s.length(); // throws NPE!
			}
		}));

		isTrue(UTILS.exists(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				String s = "abc";
				return s.length();
			}
		}));
	}

	@Test
	public void testUri() {
		eq(UTILS.uri(""), "/");
		eq(UTILS.uri("", "a"), "/a");
		eq(UTILS.uri("b", ""), "/b");
		eq(UTILS.uri("/", "x"), "/x");
		eq(UTILS.uri("/", "/x"), "/x");
		eq(UTILS.uri("/ab\\", "cd\\"), "/ab/cd");
		eq(UTILS.uri("/ab", "/cd/"), "/ab/cd");
		eq(UTILS.uri("/ab/", "/cd/"), "/ab/cd");
		eq(UTILS.uri("x", "123", "w"), "/x/123/w");
	}

	@Test
	public void testPath() {
		eq(UTILS.path(""), "");
		eq(UTILS.path("", "a"), "a");
		eq(UTILS.path("b", ""), "b");
		eq(UTILS.path("x", "y"), "x" + File.separator + "y");

		String abcd = "/ab" + File.separator + "cd";
		eq(UTILS.path("/ab\\", "cd\\"), abcd);
		eq(UTILS.path("/ab/", "cd"), abcd);
	}

}
