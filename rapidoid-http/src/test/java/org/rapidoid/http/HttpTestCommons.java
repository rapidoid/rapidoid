package org.rapidoid.http;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.rapidoid.buffer.Buf;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.Range;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.U;
import org.testng.Assert;

public abstract class HttpTestCommons extends TestCommons {

	protected HTTPServer server;

	protected String localhost(String url) {
		return "http://localhost:8080" + url;
	}

	protected void server() {
		server = HTTP.server().build();
	}

	protected void defaultServerSetup() {
		server();

		server.get("/echo", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return x.verb_().get() + ":" + x.path_().get() + ":" + x.subpath_().get() + ":" + x.query_().get();
			}
		});

		server.get("/hello", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return "Hello";
			}
		});

		server.post("/upload", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return U.join(":", x.cookies().get("foo"), x.cookies().get("COOKIE1"), x.data().get("a"), x.files()
						.size(), U.md5(x.files().get("f1")), U.md5(x.files().get("f2")), U.md5(U.or(
						x.files().get("f3"), new byte[0])));
			}
		});

		server.serve(new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return U.join(":", x.verb(), x.path(), x.subpath(), x.query());
			}
		});

		start();
	}

	protected void start() {
		server.start();

		U.sleep(300);
		U.print("----------------------------------------");
	}

	protected void shutdown() {
		server.shutdown();
		U.sleep(300);
		U.print("--- SERVER STOPPED ---");
	}

	protected String upload(String path, Map<String, String> params, Map<String, String> files) throws IOException,
			ClientProtocolException {

		CloseableHttpClient client = HttpClientBuilder.create().disableAutomaticRetries().build();

		try {
			HttpPost httppost = new HttpPost(localhost(path));

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			for (Entry<String, String> entry : files.entrySet()) {
				ContentType contentType = ContentType.create("application/octet-stream");
				String filename = entry.getValue();
				builder = builder.addBinaryBody(entry.getKey(), resourceFile(filename), contentType, filename);
			}

			for (Entry<String, String> entry : params.entrySet()) {
				ContentType contentType = ContentType.create("text/plain", "UTF-8");
				builder = builder.addTextBody(entry.getKey(), entry.getValue(), contentType);
			}

			httppost.setEntity(builder.build());

			httppost.addHeader("Cookie", "COOKIE1=a");
			httppost.addHeader("COOKIE", "foo=bar");

			U.print("REQUEST " + httppost.getRequestLine());

			U.startMeasure();
			CloseableHttpResponse response = client.execute(httppost);
			U.endMeasure();

			try {
				Assert.assertEquals(200, response.getStatusLine().getStatusCode());

				InputStream resp = response.getEntity().getContent();
				String decoded = IOUtils.toString(resp, "UTF-8");

				return decoded;
			} finally {
				response.close();
			}
		} finally {
			client.close();
		}
	}

	protected String get(String url) {
		try {
			CloseableHttpClient client = HttpClientBuilder.create().disableAutomaticRetries().build();

			HttpGet get = new HttpGet(localhost(url));

			CloseableHttpResponse result = client.execute(get);

			Assert.assertEquals(200, result.getStatusLine().getStatusCode());

			InputStream resp = result.getEntity().getContent();

			return IOUtils.toString(resp, "UTF-8");
		} catch (Throwable e) {
			throw U.rte(e);
		}
	}

	protected byte[] getBytes(String url) {
		try {
			CloseableHttpClient client = HttpClientBuilder.create().disableAutomaticRetries().build();

			HttpGet get = new HttpGet(localhost(url));

			CloseableHttpResponse result = client.execute(get);

			Assert.assertEquals(200, result.getStatusLine().getStatusCode());

			InputStream resp = result.getEntity().getContent();

			return IOUtils.toByteArray(resp);
		} catch (Throwable e) {
			throw U.rte(e);
		}
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
		return U.md5(FileUtils.readFileToByteArray(new File(U.resource(filename).toURI())));
	}

}
