package org.rapidoid.http;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.Arrays;
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
@Since("5.1.2")
public class HttpResp extends RapidoidThing {

	private final byte[] raw;

	private final int code;

	private final Map<String, String> headers;

	private final byte[] body;

	public HttpResp(byte[] raw, int code, Map<String, String> headers, byte[] body) {
		this.raw = raw;
		this.code = code;
		this.headers = headers;
		this.body = body;
	}

	public byte[] raw() {
		return raw;
	}

	public int code() {
		return code;
	}

	public Map<String, String> headers() {
		return headers;
	}

	public byte[] bodyBytes() {
		return body;
	}

	public String body() {
		return new String(body);
	}

	public String result() {
		U.must(code >= 200 && code < 300, "Expected 20x response code, but got: " + code);
		return new String(body);
	}

	@Override
	public String toString() {
		return "HttpResp{" +
			"raw=" + Arrays.toString(raw) +
			", code=" + code +
			", headers=" + headers +
			", body=" + Arrays.toString(body) +
			'}';
	}
}
