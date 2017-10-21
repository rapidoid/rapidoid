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

import org.junit.After;
import org.junit.Before;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Arr;
import org.rapidoid.config.Conf;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.io.IO;
import org.rapidoid.ioc.IoC;
import org.rapidoid.jpa.JPAUtil;
import org.rapidoid.net.util.NetUtil;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.setup.Admin;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class HttpTestCommons extends TestCommons {

	// FIXME HEAD
	private static final List<String> HTTP_VERBS = U.list("GET", "DELETE", "OPTIONS", "TRACE", "POST", "PUT", "PATCH");

	public static final int DEFAULT_PORT = 8080;

	@Before
	public void openContext() {
		Msc.reset();

		ClasspathUtil.setRootPackage("some.nonexisting.app");

		JPAUtil.reset();
		Conf.ROOT.setPath(getTestNamespace());
		IoC.reset();

		App.resetGlobalState();
		On.changes().ignore();

		On.setup().activate();
		On.setup().reload();

		verifyNoRoutes();
	}

	@After
	public void closeContext() {
		if (Admin.setup().isRunning()) {
			if (Admin.setup().port() == On.setup().port()) {
				Admin.setup().reset();
			} else {
				Admin.setup().shutdown();
			}
		}
	}

	protected String localhost(String uri) {
		return localhost(DEFAULT_PORT, uri);
	}

	protected String localhost(int port, String uri) {
		return Msc.http() + "://localhost:" + port + uri;
	}

	protected String get(String uri) {
		return HTTP.req().get(localhost(uri)).fetch();
	}

	protected byte[] getBytes(String uri) {
		return HTTP.get(localhost(uri)).execute().bodyBytes();
	}

	protected void onlyGet(String uri) {
		onlyGet(DEFAULT_PORT, uri);
	}

	protected void onlyGet(int port, String uri) {
		onlyReq(port, "GET", uri, null);
	}

	protected void onlyPost(String uri) {
		onlyPost(DEFAULT_PORT, uri, null);
	}

	protected void onlyPost(String uri, Map<String, ?> data) {
		onlyPost(DEFAULT_PORT, uri, data);
	}

	protected void onlyPost(int port, String uri, Map<String, ?> data) {
		onlyReq(port, "POST", uri, data);
	}

	protected void onlyPut(String uri) {
		onlyPut(DEFAULT_PORT, uri, null);
	}

	protected void onlyPut(int port, String uri, Map<String, ?> data) {
		onlyReq(port, "PUT", uri, data);
	}

	protected void onlyPut(String uri, Map<String, ?> data) {
		onlyPut(DEFAULT_PORT, uri, data);
	}

	protected void onlyDelete(String uri) {
		onlyDelete(DEFAULT_PORT, uri);
	}

	protected void onlyDelete(int port, String uri) {
		onlyReq(port, "DELETE", uri, null);
	}

	protected void getAndPost(String uri) {
		getAndPost(DEFAULT_PORT, uri);
	}

	protected void getAndPost(int port, String uri) {
		testReq(port, "GET", uri, null);
		testReq(port, "POST", uri, null);
		notFoundExcept(port, uri, "GET", "POST");
	}

	protected void getReq(String uri) {
		testReq(DEFAULT_PORT, "GET", uri, null);
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

	private void onlyReq(int port, String verb, String uri, Map<String, ?> data) {
		testReq(port, verb, uri, data);
		notFoundExcept(port, uri, verb);
	}

	protected void deleteReq(String uri) {
		testReq(DEFAULT_PORT, "DELETE", uri, null);
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

	protected String fetch(int port, String verb, String uri, Map<String, ?> data) {
		HttpReq req = HTTP.verb(HttpVerb.from(verb)).url(localhost(port, uri)).data(data);
		String result = exec(req);
		return result;
	}

	protected String fetch(HttpReq client, int port, String verb, String uri, Map<String, ?> data) {
		client.verb(HttpVerb.from(verb)).url(localhost(port, uri)).data(data);
		return exec(client);
	}

	private String exec(HttpReq client) {
		client.raw(true);

		String resp = new String(client.execute().raw());
		resp = resp.replaceFirst("Date: .*? GMT", "Date: XXXXX GMT");

		client.raw(false);
		return resp;
	}

	protected void raw(byte[] req) {
		String resp = new String(NetUtil.writeAndRead("localhost", 8080, req, 1000));
		resp = resp.replaceFirst("Date: .*? GMT", "Date: XXXXX GMT");

		resp = new String(req) + "--------------------------------------------------------\n" + resp + "<END>";

		String hash = Crypto.md5(req);
		String reqName = reqName(DEFAULT_PORT, "", hash);

		verifyCase(DEFAULT_PORT + "-" + hash, resp, reqName);
	}

	protected void raw(String req) {
		raw(req.getBytes());
	}

	protected void raw(List<String> requestLines) {
		raw((U.join("\r\n", requestLines) + "\r\n").getBytes());
		raw((U.join("\n", requestLines) + "\n").getBytes());
	}

	protected String fetch(HttpReq client, String verb, String uri, Map<String, ?> data) {
		return fetch(client, DEFAULT_PORT, verb, uri, data);
	}

	protected String fetch(HttpReq client, String verb, String uri) {
		return fetch(client, DEFAULT_PORT, verb, uri, null);
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

	protected String appRoutes() {
		return U.join("\n", On.routes().all());
	}

	protected void verifyRoutes() {
		verify("routes", appRoutes());
	}

	protected void verifyRoutes(String name) {
		verify("routes-" + name, appRoutes());
	}

	protected void verifyNoRoutes() {
		isTrue(On.routes().all().isEmpty());
	}

}
