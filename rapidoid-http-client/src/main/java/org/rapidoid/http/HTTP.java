package org.rapidoid.http;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.io.Upload;

import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-http-client
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HTTP extends RapidoidThing {

	static {
		RapidoidInitializer.initialize();
	}

	public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
	public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_XML = "application/xml";
	public static final String CONTENT_TYPE_BINARY = "application/octet-stream";

	public static HttpClient get(String url) {
		return new HttpClient().verb(HttpVerb.GET).url(url);
	}

	public static HttpClient post(String url) {
		return new HttpClient().verb(HttpVerb.POST).url(url);
	}

	public static HttpClient put(String url) {
		return new HttpClient().verb(HttpVerb.PUT).url(url);
	}

	public static HttpClient delete(String url) {
		return new HttpClient().verb(HttpVerb.DELETE).url(url);
	}

	public static HttpClient patch(String url) {
		return new HttpClient().verb(HttpVerb.PATCH).url(url);
	}

	public static HttpClient options(String url) {
		return new HttpClient().verb(HttpVerb.OPTIONS).url(url);
	}

	public static HttpClient head(String url) {
		return new HttpClient().verb(HttpVerb.HEAD).url(url);
	}

	public static HttpClient trace(String url) {
		return new HttpClient().verb(HttpVerb.TRACE).url(url);
	}

	public static HttpClient verb(org.rapidoid.http.HttpVerb verb) {
		return new HttpClient().verb(verb);
	}

	public static HttpClient url(String url) {
		return new HttpClient().url(url);
	}

	public static HttpClient body(byte[] body) {
		return new HttpClient().body(body);
	}

	public static HttpClient cookies(Map<String, String> cookies) {
		return new HttpClient().cookies(cookies);
	}

	public static HttpClient headers(Map<String, String> headers) {
		return new HttpClient().headers(headers);
	}

	public static HttpClient data(Map<String, String> data) {
		return new HttpClient().data(data);
	}

	public static HttpClient files(Map<String, List<Upload>> files) {
		return new HttpClient().files(files);
	}

	public static HttpClient contentType(String contentType) {
		return new HttpClient().contentType(contentType);
	}

	public static HttpClient userAgent(String userAgent) {
		return new HttpClient().userAgent(userAgent);
	}

	public static HttpClient followRedirects(boolean followRedirects) {
		return new HttpClient().followRedirects(followRedirects);
	}

	public static HttpClient keepAlive(boolean keepAlive) {
		return new HttpClient().keepAlive(keepAlive);
	}

	public static HttpClient keepCookies(boolean keepCookies) {
		return new HttpClient().keepCookies(keepCookies);
	}

	public static HttpClient reuseConnections(boolean reuseConnections) {
		return new HttpClient().reuseConnections(reuseConnections);
	}

	public static HttpClient decompress(boolean decompress) {
		return new HttpClient().decompress(decompress);
	}

	public static HttpClient maxConnPerRoute(int maxConnPerRoute) {
		return new HttpClient().maxConnPerRoute(maxConnPerRoute);
	}

	public static HttpClient maxConnTotal(int maxConnTotal) {
		return new HttpClient().maxConnTotal(maxConnTotal);
	}

	public static HttpClient socketTimeout(int socketTimeout) {
		return new HttpClient().socketTimeout(socketTimeout);
	}

	public static HttpClient connectTimeout(int connectTimeout) {
		return new HttpClient().connectTimeout(connectTimeout);
	}

	public static HttpClient connectionRequestTimeout(int connectionRequestTimeout) {
		return new HttpClient().connectionRequestTimeout(connectionRequestTimeout);
	}

	public static HttpClient maxRedirects(int maxRedirects) {
		return new HttpClient().maxRedirects(maxRedirects);
	}

	public static HttpClient raw(boolean raw) {
		return new HttpClient().raw(raw);
	}

	public static HttpClient dontClose() {
		return new HttpClient().dontClose();
	}

}
