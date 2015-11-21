package org.rapidoid.http;

/*
 * #%L
 * rapidoid-rest
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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Future;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HTTP {

	public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
	public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_XML = "application/xml";
	public static final String CONTENT_TYPE_BINARY = "application/octet-stream";

	public static final HttpClient DEFAULT_CLIENT = new HttpClient();

	/********************************** GET **********************************/

	public static Future<byte[]> get(String uri, Callback<byte[]> callback) {
		return DEFAULT_CLIENT.get(uri, callback);
	}

	public static byte[] get(String uri) {
		return get(uri, null).get();
	}

	/********************************** POST **********************************/

	public static Future<byte[]> post(String uri, Map<String, String> headers, Map<String, String> data,
			Map<String, String> files, Callback<byte[]> callback) {
		return DEFAULT_CLIENT.post(uri, headers, data, files, callback);
	}

	public static byte[] post(String uri, Map<String, String> headers, Map<String, String> data,
			Map<String, String> files) {
		return post(uri, headers, data, files, null).get();
	}

	public static Future<byte[]> post(String uri, Map<String, String> headers, byte[] postData, String contentType,
			Callback<byte[]> callback) {
		return DEFAULT_CLIENT.post(uri, headers, postData, contentType, callback);
	}

	public static byte[] post(String uri, Map<String, String> headers, byte[] postData, String contentType) {
		return post(uri, headers, postData, contentType, null).get();
	}

}
