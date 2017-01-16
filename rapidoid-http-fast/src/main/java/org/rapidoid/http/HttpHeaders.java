package org.rapidoid.http;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/*
 * #%L
 * rapidoid-http-fast
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
@Since("5.0.0")
public class HttpHeaders extends RapidoidThing {

	public static final HttpHeaders SET_COOKIE = new HttpHeaders("Set-Cookie");

	public static final HttpHeaders CONTENT_TYPE = new HttpHeaders("Content-Type");

	public static final HttpHeaders CONTENT_DISPOSITION = new HttpHeaders("Content-Disposition");

	public static final HttpHeaders CACHE_CONTROL = new HttpHeaders("Cache-Control");

	public static final HttpHeaders LOCATION = new HttpHeaders("Location");

	public static final HttpHeaders HOST = new HttpHeaders("Host");

	public static final HttpHeaders X_FORWARDED_FOR = new HttpHeaders("X-Forwarded-For");

	private final byte[] bytes;

	private final String name;

	public HttpHeaders(String name) {
		this.name = name;
		this.bytes = name.getBytes();
	}

	public byte[] getBytes() {
		return bytes;
	}

	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
