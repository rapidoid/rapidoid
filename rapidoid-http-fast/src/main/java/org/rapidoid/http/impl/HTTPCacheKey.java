package org.rapidoid.http.impl;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class HTTPCacheKey extends RapidoidThing {

	private final String host;

	private final String uri;

	public HTTPCacheKey(String host, String uri) {
		this.host = host;
		this.uri = uri;
	}

	@Override
	public String toString() {
		return "HTTPCacheKey{" +
			"host='" + host + '\'' +
			", uri='" + uri + '\'' +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HTTPCacheKey that = (HTTPCacheKey) o;

		if (host != null ? !host.equals(that.host) : that.host != null) return false;
		return uri != null ? uri.equals(that.uri) : that.uri == null;
	}

	@Override
	public int hashCode() {
		int result = host != null ? host.hashCode() : 0;
		result = 31 * result + (uri != null ? uri.hashCode() : 0);
		return result;
	}

}
