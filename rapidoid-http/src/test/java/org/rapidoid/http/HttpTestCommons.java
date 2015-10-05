package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.Range;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.U;
import org.rapidoid.webapp.WebApp;
import org.rapidoid.webapp.WebAppGroup;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class HttpTestCommons extends TestCommons {

	protected WebApp app;

	protected HTTPServer server;

	protected Router router;

	@Before
	public void openContext() {
		app = WebAppGroup.openRootContext();
		router = app.getRouter();
		HTTP.DEFAULT_CLIENT.reset();
	}

	@After
	public void closeContext() {
		Ctxs.close();
	}

	protected String localhost(String uri) {
		return "http://localhost:8080" + uri;
	}

	protected void server() {
		server = HTTP.server().build();
	}

	protected void defaultServerSetup() {
		server();

		router.get("/echo", new Handler() {
			@Override
			public Object handle(HttpExchange h) throws Exception {
				LowLevelHttpExchange x = (LowLevelHttpExchange) h;
				x.plain();
				return x.verb_().get() + ":" + x.path_().get() + ":" + x.subpath_().get() + ":" + x.query_().get();
			}
		});

		router.get("/hello", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return "Hello";
			}
		});

		router.post("/upload", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				Log.info("Uploaded files", "files", x.files().keySet());
				x.plain();
				return U.join(":", x.cookies().get("foo"), x.cookies().get("COOKIE1"), x.posted().get("a"), x.files()
						.size(), Crypto.md5(x.files().get("f1")), Crypto.md5(x.files().get("f2")), Crypto.md5(U.or(x
						.files().get("f3"), new byte[0])));
			}
		});

		router.serve(new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				x.html();
				return U.join(":", x.verb(), x.path(), x.subpath(), x.query());
			}
		});

		start();
	}

	protected void start() {
		server.start();

		U.sleep(300);
		System.out.println("----------------------------------------");
	}

	protected void shutdown() {
		server.shutdown();
		U.sleep(300);
		System.out.println("--- SERVER STOPPED ---");
	}

	protected void eq(String whole, Range range, String expected) {
		eq(range.get(whole), expected);
	}

	protected void eqs(String whole, KeyValueRanges ranges, String... keysAndValues) {
		eq(keysAndValues.length % 2, 0);
		eq(ranges.count, keysAndValues.length / 2);
		for (int i = 0; i < ranges.count; i++) {
			Range key = ranges.keys[i];
			Range value = ranges.values[i];
			eq(whole, key, keysAndValues[i * 2]);
			eq(whole, value, keysAndValues[i * 2 + 1]);
		}
	}

	protected void eq(Range range, int start, int length) {
		Assert.assertEquals(range.start, start);
		Assert.assertEquals(range.length, length);
	}

	protected void isNone(Range range) {
		Assert.assertEquals(range.start, -1);
		Assert.assertEquals(range.length, 0);
	}

	protected void eq(Buf buf, String expected) {
		eq(buf.size(), expected.getBytes().length);
		eq(buf.data(), expected);

		byte[] bbytes = new byte[buf.size()];
		ByteBuffer bufy = ByteBuffer.wrap(bbytes);
		buf.writeTo(bufy);
		eq(new String(bbytes), expected);

		int size = (int) Math.ceil(expected.length() * 1.0 / buf.unitSize());
		isTrue(buf.unitCount() == size || buf.unitCount() == size + 1);

		byte[] bytes = expected.getBytes();
		synchronized (buf) {
			for (int i = 0; i < bytes.length; i++) {
				eq((char) buf.get(i), (char) bytes[i]);
			}
		}

		for (int len = 2; len < 10; len++) {
			for (int p = 0; p <= buf.size() - len; p++) {
				String sub = buf.get(new Range(p, len));
				eq(sub, expected.substring(p, p + len));
			}
		}
	}

	protected String resourceMD5(String filename) throws IOException, URISyntaxException {
		return Crypto.md5(FileUtils.readFileToByteArray(new File(IO.resource(filename).toURI())));
	}

	protected String upload(String uri, Map<String, String> params, Map<String, String> files) throws IOException,
			ClientProtocolException {
		Map<String, String> headers = U.map("Cookie", "COOKIE1=a", "COOKIE", "foo=bar");
		return new String(HTTP.post(localhost(uri), headers, params, files));
	}

	protected String get(String uri) {
		return new String(HTTP.get(localhost(uri)));
	}

	protected byte[] getBytes(String uri) {
		return HTTP.get(localhost(uri));
	}

}
