package org.rapidoid.http;

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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HTTP extends RapidoidInitializer {

	private static volatile HttpClient client = client();

	public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
	public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_XML = "application/xml";
	public static final String CONTENT_TYPE_BINARY = "application/octet-stream";

	public static HttpClient client() {
		return new HttpClient();
	}

	public static HttpReq req() {
		return client.req();
	}

	public static HttpReq get(String url) {
		return req().verb(HttpVerb.GET).url(url);
	}

	public static HttpReq post(String url) {
		return req().verb(HttpVerb.POST).url(url);
	}

	public static HttpReq put(String url) {
		return req().verb(HttpVerb.PUT).url(url);
	}

	public static HttpReq delete(String url) {
		return req().verb(HttpVerb.DELETE).url(url);
	}

	public static HttpReq patch(String url) {
		return req().verb(HttpVerb.PATCH).url(url);
	}

	public static HttpReq options(String url) {
		return req().verb(HttpVerb.OPTIONS).url(url);
	}

	public static HttpReq head(String url) {
		return req().verb(HttpVerb.HEAD).url(url);
	}

	public static HttpReq trace(String url) {
		return req().verb(HttpVerb.TRACE).url(url);
	}

	public static HttpReq verb(org.rapidoid.http.HttpVerb verb) {
		return req().verb(verb);
	}

	public static HttpReq url(String url) {
		return req().url(url);
	}

	public static HttpReq body(byte[] body) {
		return req().body(body);
	}

	public static HttpReq headers(Map<String, String> headers) {
		return req().headers(headers);
	}

	public static HttpReq data(Map<String, String> data) {
		return req().data(data);
	}

	public static HttpReq files(Map<String, List<Upload>> files) {
		return req().files(files);
	}

	public static HttpReq contentType(String contentType) {
		return req().contentType(contentType);
	}

	public static HttpReq socketTimeout(int socketTimeout) {
		return req().socketTimeout(socketTimeout);
	}

	public static HttpReq connectTimeout(int connectTimeout) {
		return req().connectTimeout(connectTimeout);
	}

	public static HttpReq connectionRequestTimeout(int connectionRequestTimeout) {
		return req().connectionRequestTimeout(connectionRequestTimeout);
	}

	public static HttpReq raw(boolean raw) {
		return req().raw(raw);
	}

	public static synchronized void close() {
		HttpClient oldClient = client;
		client = client();
		oldClient.close();
	}

}
