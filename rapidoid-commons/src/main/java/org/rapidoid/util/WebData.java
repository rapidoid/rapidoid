package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-commons
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
@Since("5.3.3")
public final class WebData extends RapidoidThing implements Comparable<WebData> {

	private final String data;

	public WebData(String data) {
		U.notNull(data, "web data");
		this.data = data;
	}

	public String data() {
		return data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WebData webData = (WebData) o;

		return data.equals(webData.data);
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public int compareTo(WebData that) {
		return this.data.compareTo(that.data);
	}

	/**
	 * Unwraps the data to the real value represented by it.
	 */
	public Object unwrap() {
		if (Str.isWebSafeBinary(data)) {
			return Str.fromWebSafeBase64(data);

		} else {
			return data;
		}
	}

}
