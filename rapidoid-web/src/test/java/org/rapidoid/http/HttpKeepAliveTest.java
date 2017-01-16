package org.rapidoid.http;

/*
 * #%L
 * rapidoid-web
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
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.2.6")
public class HttpKeepAliveTest extends HttpTestCommons {

	@Test
	public void testHttp1_0Default() {
		On.get("/").plain("Hello");

		raw(U.list("GET / HTTP/1.0", ""));
	}

	@Test
	public void testHttp1_1Default() {
		On.get("/").plain("Hello");

		raw(U.list("GET / HTTP/1.1", ""));
	}

	@Test
	public void testHttp1_0Default2() {
		On.get("/").plain("Hello");

		raw(U.list("GET / HTTP/1.0", "Conn: keep-alive", ""));
	}

	@Test
	public void testHttp1_1Default2() {
		On.get("/").plain("Hello");

		raw(U.list("GET / HTTP/1.1", "Conn: close", ""));
	}

	@Test
	public void testConnKeepAlive() {
		On.get("/").plain("Hello");

		raw(U.list("GET / HTTP/1.1", "Connection: keep-alive", ""));
	}

	@Test
	public void testConnClose() {
		On.get("/").plain("Hello");

		raw(U.list("GET / HTTP/1.1", "Connection: close", ""));
	}

	@Test
	public void testConnKeepAlive2() {
		On.get("/").plain("Hello");

		raw(U.list("GET / HTTP/1.1", "x: keep-alive", "y: close", "Connection: keep-alive", ""));
	}

	@Test
	public void testConnClose2() {
		On.get("/").plain("Hello");

		raw(U.list("GET / HTTP/1.1", "x: keep-alive", "y: close", "Connection: close", ""));
	}

	@Test
	public void testConnKeepAlive3() {
		On.get("/").plain("Hello");

		raw(U.list("GET / HTTP/1.1", "connection:keep-alive", ""));
	}

	@Test
	public void testConnClose3() {
		On.get("/").plain("Hello");

		raw(U.list("GET / HTTP/1.1", "connection:close", ""));
	}

}
