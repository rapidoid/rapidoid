package org.rapidoid.http;

/*
 * #%L
 * rapidoid-rest
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Future;
import org.rapidoid.config.RapidoidInitializer;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HTTP {

	static {
		RapidoidInitializer.initialize();
	}

	public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
	public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_XML = "application/xml";
	public static final String CONTENT_TYPE_BINARY = "application/octet-stream";

	public static final HttpClient DEFAULT_CLIENT = new HttpClient(false, false);
	public static final HttpClient STATEFUL_CLIENT = new HttpClient(true, true);

	/*  GET */

	public static Future<byte[]> get(String uri, Callback<byte[]> callback) {
		return DEFAULT_CLIENT.get(uri, callback);
	}

	public static byte[] get(String uri) {
		return get(uri, null).get();
	}

	/*  DELETE */

	public static Future<byte[]> delete(String uri, Callback<byte[]> callback) {
		return DEFAULT_CLIENT.delete(uri, callback);
	}

	public static byte[] delete(String uri) {
		return delete(uri, null).get();
	}

	/*  OPTIONS */

	public static Future<byte[]> options(String uri, Callback<byte[]> callback) {
		return DEFAULT_CLIENT.options(uri, callback);
	}

	public static byte[] options(String uri) {
		return options(uri, null).get();
	}

	/*  HEAD */

	public static Future<byte[]> head(String uri, Callback<byte[]> callback) {
		return DEFAULT_CLIENT.head(uri, callback);
	}

	public static byte[] head(String uri) {
		return head(uri, null).get();
	}

	/*  TRACE */

	public static Future<byte[]> trace(String uri, Callback<byte[]> callback) {
		return DEFAULT_CLIENT.trace(uri, callback);
	}

	public static byte[] trace(String uri) {
		return trace(uri, null).get();
	}

	/*  POST */

	public static Future<byte[]> post(String uri, Map<String, String> headers, Map<String, String> data,
	                                  Map<String, String> files, Callback<byte[]> callback) {
		return DEFAULT_CLIENT.post(uri, headers, data, files, callback);
	}

	public static byte[] post(String uri, Map<String, String> headers, Map<String, String> data,
	                          Map<String, String> files) {
		return post(uri, headers, data, files, null).get();
	}

	public static Future<byte[]> post(String uri, Map<String, String> headers, byte[] body, String contentType,
	                                  Callback<byte[]> callback) {
		return DEFAULT_CLIENT.post(uri, headers, body, contentType, callback);
	}

	public static byte[] post(String uri, Map<String, String> headers, byte[] body, String contentType) {
		return post(uri, headers, body, contentType, null).get();
	}

	public static byte[] post(String uri) {
		return post(uri, null, (byte[]) null, null, null).get();
	}

	/*  PUT */

	public static Future<byte[]> put(String uri, Map<String, String> headers, Map<String, String> data,
	                                 Map<String, String> files, Callback<byte[]> callback) {
		return DEFAULT_CLIENT.put(uri, headers, data, files, callback);
	}

	public static byte[] put(String uri, Map<String, String> headers, Map<String, String> data,
	                         Map<String, String> files) {
		return put(uri, headers, data, files, null).get();
	}

	public static Future<byte[]> put(String uri, Map<String, String> headers, byte[] body, String contentType,
	                                 Callback<byte[]> callback) {
		return DEFAULT_CLIENT.put(uri, headers, body, contentType, callback);
	}

	public static byte[] put(String uri, Map<String, String> headers, byte[] body, String contentType) {
		return put(uri, headers, body, contentType, null).get();
	}

	public static byte[] put(String uri) {
		return put(uri, null, (byte[]) null, null, null).get();
	}

	/*  PATCH */

	public static Future<byte[]> patch(String uri, Map<String, String> headers, Map<String, String> data,
	                                   Map<String, String> files, Callback<byte[]> callback) {
		return DEFAULT_CLIENT.patch(uri, headers, data, files, callback);
	}

	public static byte[] patch(String uri, Map<String, String> headers, Map<String, String> data,
	                           Map<String, String> files) {
		return patch(uri, headers, data, files, null).get();
	}

	public static Future<byte[]> patch(String uri, Map<String, String> headers, byte[] body, String contentType,
	                                   Callback<byte[]> callback) {
		return DEFAULT_CLIENT.patch(uri, headers, body, contentType, callback);
	}

	public static byte[] patch(String uri, Map<String, String> headers, byte[] body, String contentType) {
		return patch(uri, headers, body, contentType, null).get();
	}

	public static byte[] patch(String uri) {
		return patch(uri, null, (byte[]) null, null, null).get();
	}

}
