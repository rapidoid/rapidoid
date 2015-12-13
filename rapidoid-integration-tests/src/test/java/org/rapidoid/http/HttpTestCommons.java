package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
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
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.junit.After;
import org.junit.Before;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.http.fast.On;
import org.rapidoid.http.fast.ReqHandler;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class HttpTestCommons extends TestCommons {

	@Before
	public void openContext() {
		org.rapidoid.web.WebAppGroup.openRootContext();
	}

	@After
	public void closeContext() {
		Ctxs.close();
	}

	protected String localhost(String uri) {
		return "http://localhost:8888" + uri;
	}

	protected void defaultServerSetup() {
		server();

		On.get("/echo").plain(new ReqHandler() {
			@Override
			public Object handle(Req x) throws Exception {
				return x.verb() + ":" + x.path() + ":" + x.query();
			}
		});

		On.get("/hello").html(new ReqHandler() {
			@Override
			public Object handle(Req x) {
				return "Hello";
			}
		});

		On.post("/upload").plain(new ReqHandler() {
			@Override
			public Object handle(Req x) {
				Log.info("Uploaded files", "files", x.files().keySet());
				return U.join(":", x.cookies().get("foo"), x.cookies().get("COOKIE1"), x.posted().get("a"), x.files()
						.size(), Crypto.md5(x.files().get("f1")), Crypto.md5(x.files().get("f2")), Crypto.md5(U.or(x
						.files().get("f3"), new byte[0])));
			}
		});

		On.req(new ReqHandler() {
			@Override
			public Object handle(Req x) {
				return x.response().html(U.join(":", x.verb(), x.path(), x.query()));
			}
		});

		start();
	}

	protected void server() {
		On.getDefaultSetup().http().clearHandlers();
	}

	protected void start() {
		// On.getDefaultSetup().listen();
		// U.sleep(300);
		// System.out.println("----------------------------------------");
	}

	protected void shutdown() {
		// On.getDefaultSetup().shutdown();
		// U.sleep(300);
		// System.out.println("--- SERVER STOPPED ---");
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
