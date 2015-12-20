package org.rapidoid.measure;

/*
 * #%L
 * rapidoid-commons
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Measures {

	private final Map<String, Measure> measures = new HashMap<String, Measure>();

	@SuppressWarnings("unchecked")
	public synchronized <T extends Measure> T measure(String name, Class<T> clazz) {
		T m = (T) measures.get(name);

		if (m == null) {
			try {
				m = clazz.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException("Cannot create measure", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Cannot create measure", e);
			}
			measures.put(name, m);
		}

		return m;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T extends Measure> T measure(String name) {
		T m = (T) measures.get(name);

		if (m == null) {
			throw new IllegalArgumentException("Cannot find measure: " + name);
		}

		return m;
	}

	public synchronized String info() {
		StringBuffer sb = new StringBuffer();

		boolean first = true;

		synchronized (measures) {
			for (Entry<String, Measure> entry : measures.entrySet()) {
				if (!first) {
					sb.append(", ");
				}

				sb.append(entry.getKey());
				sb.append("=");

				Measure measure = entry.getValue();

				String val = measure.get();
				if (val == null) {
					val = "N/A";
				}

				sb.append(val);
				measure.reset();

				first = false;
			}
		}

		return sb.toString();
	}

	public String get(String name) {
		return measure(name).get();
	}

}
