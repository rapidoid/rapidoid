package com.rapidoid.http;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class HttpServerHeadersTest extends HttpTestCommons {

	@Test
	public void shouldHandleHttpRequests() throws IOException, URISyntaxException {

		Web.get("/file", new Handler() {
			@Override
			public Object handle(WebExchange x) {
				return x.download(x.subpath().substring(1) + ".txt").write("ab").write("cde").done();
			}
		});

		Web.get("/bin", new Handler() {
			@Override
			public Object handle(WebExchange x) {
				return x.binary().write("bin").done();
			}
		});

		Web.get("/session", new Handler() {
			@Override
			public Object handle(WebExchange x) {
				if (x.cookie("ses") == null) {
					x.setCookie("ses", "023B");
				}
				x.setCookie("key" + U.rnd(100), "val" + U.rnd(100));

				return x.writeJSON(x.cookies()).done();
			}
		});

		Web.get("/async", new Handler() {
			@Override
			public Object handle(WebExchange x) {
				return x.async().write("now").done();
			}
		});

		Web.get("/testfile1", new Handler() {
			@Override
			public Object handle(WebExchange x) {
				return U.file("test1.txt");
			}
		});

		Web.get("/rabbit.jpg", new Handler() {
			@Override
			public Object handle(WebExchange x) {
				return x.sendFile(U.file("rabbit.jpg")).done();
			}
		});

		Web.get("/ab", new Handler() {
			@Override
			public Object handle(WebExchange x) {
				return x.sendFile(U.file("ab.html")).done();
			}
		});

		Web.handle(new Handler() {
			@Override
			public Object handle(WebExchange x) {
				return x.setCookie("asd", "f").html().write("a<b>b</b>c");
			}
		});

		server = Web.start();

		U.print("----------------------------------------");

		byte[] rabbit = FileUtils.readFileToByteArray(U.file("rabbit.jpg"));
		byte[] ab = FileUtils.readFileToByteArray(U.file("ab.html"));

		for (int i = 0; i < 100; i++) {
			eq(get("/"), "a<b>b</b>c");
			eq(get("/xy"), "a<b>b</b>c");
			eq(get("/async"), "now");
			eq(get("/session"), "{}");
			eq(get("/bin"), "bin");
			eq(get("/file/foo"), "abcde");
			eq(get("/testfile1"), "TEST1");
			eq(getBytes("/rabbit.jpg"), rabbit);
			eq(getBytes("/ab"), ab);
		}

		shutdown();
	}

}
