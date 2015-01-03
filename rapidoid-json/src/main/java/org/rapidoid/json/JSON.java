package org.rapidoid.json;

import java.io.OutputStream;
import java.util.Map;

import org.rapidoid.util.U;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/*
 * #%L
 * rapidoid-json
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

	public static final ObjectMapper MAPPER = mapper();

	private static ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}

	public static String stringify(Object value) {
		try {
			return MAPPER.writeValueAsString(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param extras
	 *            extra JSON attributes in format (key1, value1, key2, value2...)
	 */
	public static String stringifyWithExtras(Object value, Object... extras) {
		if (extras.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Expected even number of extras (key1, value1, key2, value2...), but found: " + extras.length);
		}

		try {
			JsonNode node = MAPPER.valueToTree(value);

			if (!(node instanceof ObjectNode)) {
				throw new RuntimeException("Cannot add extra attributes on a non-object value: " + value);
			}

			ObjectNode obj = (ObjectNode) node;

			int extrasN = extras.length / 2;
			for (int i = 0; i < extrasN; i++) {
				Object key = extras[2 * i];
				if (key instanceof String) {
					obj.put((String) key, String.valueOf(extras[2 * i + 1]));
				} else {
					throw new RuntimeException("Expected extra key of type String, but found: " + key);
				}
			}

			return MAPPER.writeValueAsString(node);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void stringify(Object value, OutputStream out) {
		try {
			MAPPER.writeValue(out, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(String json, Class<T> valueType) {
		try {
			return MAPPER.readValue(json, valueType);
		} catch (Exception e) {
			U.error("Cannot parse JSON!", "json", json, "error", e);
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseMap(String json) {
		return parse(json, Map.class);
	}

	public static void warmup() {
		JSON.stringify(123);
		JSON.parse("{}", Map.class);
	}

}
