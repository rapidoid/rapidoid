package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
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

import org.junit.After;
import org.junit.Before;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Arr;
import org.rapidoid.config.Conf;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.io.IO;
import org.rapidoid.ioc.IoC;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.setup.*;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class HttpTestCommons extends TestCommons {

	// FIXME HEAD
	private static final List<String> HTTP_VERBS = U.list("GET", "DELETE", "OPTIONS", "TRACE", "POST", "PUT", "PATCH");

	public static final int DEFAULT_PORT = 8888;

	@Before
	public void openContext() {
		Log.setLogLevel(LogLevel.INFO);

		ClasspathUtil.setRootPackage("some.nonexisting.app");

		System.out.println("--- STARTING SERVER ---");

		Conf.reset();
		Conf.setPath(getTestName());
		Log.setLogLevel(LogLevel.INFO);
		IoC.defaultContext().reset();

		Setup.resetGlobalState();
		OnChanges.ignore();

		On.setup().http().resetConfig();
		On.setup().listen();

		System.out.println("--- SERVER STARTED ---");

		notFound("/");
		notFound("/a");
		notFound("/b?dgfg");
		notFound("/c?x=123");
		notFound("/else");
		notFound("/echo");
		notFound("/upload");
	}

	@After
	public void closeContext() {
		System.out.println("--- STOPPING SERVER ---");

		Admin.setup().shutdown();
		Dev.setup().shutdown();

		System.out.println("--- SERVER STOPPED ---");
	}

	protected String localhost(String uri) {
		return localhost(DEFAULT_PORT, uri);
	}

	protected String localhost(int port, String uri) {
		return "http://localhost:" + port + uri;
	}

	protected void defaultServerSetup() {
		On.get("/echo").plain((Req x) -> {
			return x.verb() + ":" + x.path() + ":" + x.query();
		});

		On.get("/hello").html("Hello");

		On.post("/upload").plain((Req x) -> {
			Log.info("Uploaded files", "files", x.files().keySet());

			return U.join(":", x.cookies().get("foo"), x.cookies().get("COOKIE1"), x.posted().get("a"), x.files()
					.size(), Crypto.md5(x.files().get("f1")), Crypto.md5(x.files().get("f2")), Crypto.md5(U.or(x
					.files().get("f3"), new byte[0])));
		});

		On.req((Req x) -> x.response().html(U.join(":", x.verb(), x.path(), x.query())));
	}

	protected String resourceMD5(String filename) throws IOException, URISyntaxException {
		return Crypto.md5(IO.loadBytes(filename));
	}

	protected String get(String uri) {
		return HTTP.get(localhost(uri)).fetch();
	}

	protected byte[] getBytes(String uri) {
		return HTTP.get(localhost(uri)).execute();
	}

	protected void onlyGet(String uri) {
		onlyGet(DEFAULT_PORT, uri);
	}

	protected void onlyGet(int port, String uri) {
		onlyReq(port, "GET", uri);
	}

	protected void onlyPost(String uri) {
		onlyPost(DEFAULT_PORT, uri);
	}

	protected void onlyPost(int port, String uri) {
		onlyReq(port, "POST", uri);
	}

	protected void onlyPut(String uri) {
		onlyPut(DEFAULT_PORT, uri);
	}

	protected void onlyPut(int port, String uri) {
		onlyReq(port, "PUT", uri);
	}

	protected void onlyDelete(String uri) {
		onlyDelete(DEFAULT_PORT, uri);
	}

	protected void onlyDelete(int port, String uri) {
		onlyReq(port, "DELETE", uri);
	}

	protected void getAndPost(String uri) {
		getAndPost(DEFAULT_PORT, uri);
	}

	protected void getAndPost(int port, String uri) {
		testReq(port, "GET", uri, null);
		testReq(port, "POST", uri, null);
		notFoundExcept(port, uri, "GET", "POST");
	}

	protected void postData(String uri, Map<String, ?> data) {
		testReq(DEFAULT_PORT, "POST", uri, data);
	}

	protected void postData(int port, String uri, Map<String, ?> data) {
		testReq(port, "POST", uri, data);
	}

	protected void putData(String uri, Map<String, ?> data) {
		testReq(DEFAULT_PORT, "PUT", uri, data);
	}

	protected void putData(int port, String uri, Map<String, ?> data) {
		testReq(port, "PUT", uri, data);
	}

	protected void patchData(String uri, Map<String, ?> data) {
		testReq(DEFAULT_PORT, "PATCH", uri, data);
	}

	protected void patchData(int port, String uri, Map<String, ?> data) {
		testReq(port, "PATCH", uri, data);
	}

	private void onlyReq(int port, String verb, String uri) {
		testReq(port, verb, uri, null);
		notFoundExcept(port, uri, verb);
	}

	protected void notFoundExcept(String uri, String... exceptVerbs) {
		notFoundExcept(DEFAULT_PORT, uri, exceptVerbs);
	}

	protected void notFoundExcept(int port, String uri, String... exceptVerbs) {
		for (String verb : HTTP_VERBS) {
			if (Arr.indexOf(exceptVerbs, verb) < 0) {
				notFound(port, verb, uri);
			}
		}
	}

	protected void notFound(String uri) {
		notFound(DEFAULT_PORT, uri);
	}

	protected void notFound(int port, String uri) {
		notFoundExcept(port, uri);
	}

	protected void notFound(int port, String verb, String uri) {
		String resp = fetch(port, verb, uri, null);
		String notFound = IO.load("404-not-found.txt");
		U.notNull(notFound, "404-not-found");
		check(verb + " " + uri, resp, notFound);
	}

	private void testReq(int port, String verb, String uri, Map<String, ?> data) {
		String resp = fetch(port, verb, uri, data);
		String reqName = reqName(port, verb, uri);

		verifyCase(port + " " + verb + " " + uri, resp, reqName);
	}

	private String fetch(int port, String verb, String uri, Map<String, ?> data) {
		HttpClient client = HTTP.verb(HttpVerb.from(verb)).url(localhost(port, uri)).raw(true);

		if (data != null) {
			client = client.data(data);
		}

		byte[] res = client.execute();
		String resp = new String(res);
		resp = resp.replaceFirst("Date: .*? GMT", "Date: XXXXX GMT");

		return resp;
	}

	protected String fetch(String verb, String uri) {
		return fetch(DEFAULT_PORT, verb, uri, null);
	}

	protected String fetch(String verb, String uri, Map<String, ?> data) {
		return fetch(DEFAULT_PORT, verb, uri, data);
	}

	private String reqName(int port, String verb, String uri) {
		String req = verb + uri.replace("/", "_").replace("?", "-");
		if (port != DEFAULT_PORT) {
			req = port + "__" + req;
		}

		return req;
	}

	protected static Map<String, Object> reqResp(Req req, Resp resp) {
		return U.map("verb", req.verb(), "uri", req.uri(), "data", req.data(), "code", resp.code());
	}

}
