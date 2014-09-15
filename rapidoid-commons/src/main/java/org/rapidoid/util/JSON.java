package org.rapidoid.util;

import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

public class JSON {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static String stringify(Object value) {
		try {
			return MAPPER.writeValueAsString(value);
		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	public static void stringify(Object value, OutputStream out) {
		try {
			MAPPER.writeValue(out, value);
		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	public static <T> T parse(String json, Class<T> valueType) {
		try {
			return MAPPER.readValue(json, valueType);
		} catch (Exception e) {
			throw U.rte(e);
		}
	}

}
