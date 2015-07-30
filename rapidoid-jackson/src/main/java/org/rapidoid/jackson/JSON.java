package org.rapidoid.jackson;

import java.io.OutputStream;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.cls.Cls;
import org.rapidoid.log.Log;
import org.rapidoid.util.U;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

/*
 * #%L
 * rapidoid-jackson
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class JSON {

	public static final ObjectMapper MAPPER = mapper();

	private static ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setBase64Variant(Base64Variants.MODIFIED_FOR_URL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new AfterburnerModule());
		return mapper;
	}

	public static String jacksonStringify(Object value) {
		try {
			return MAPPER.writeValueAsString(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String stringify(Object value) {
		return jacksonStringify(Beany.serialize(value));
	}

	public static void jacksonStringify(Object value, OutputStream out) {
		try {
			MAPPER.writeValue(out, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void stringify(Object value, OutputStream out) {
		jacksonStringify(Beany.serialize(value), out);
	}

	public static <T> T jacksonParse(String json, Class<T> valueType) {
		try {
			return MAPPER.readValue(json, valueType);
		} catch (Exception e) {
			Log.error("Cannot parse JSON!", "json", json, "error", e);
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(String json, Class<T> valueType) {
		return jacksonParse(json, valueType);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseMap(String json) {
		return parse(json, Map.class);
	}

	public static void warmup() {
		JSON.stringify(123);
		JSON.parse("{}", Map.class);
	}

	public static String save(Object value) {
		Object ser = Beany.serialize(value);
		Class<?> cls = value != null ? value.getClass() : null;
		Map<String, Object> map = U.map("_", cls.getCanonicalName(), "v", ser);
		return jacksonStringify(map);
	}

	@SuppressWarnings("unchecked")
	public static Object load(String json) {
		Map<String, Object> map = parseMap(json);
		String clsName = (String) map.get("_");
		Class<Object> type = Cls.getClassIfExists(clsName);
		if (type == null) {
			return null;
		}

		Object ser = map.get("v");

		if (ser instanceof Map) {
			Object value = Cls.newInstance(type);
			Map<String, Object> props = (Map<String, Object>) ser;
			Beany.update(value, props, false);
			return value;
		}

		return ser;
	}

	public static byte[] parseBytes(String json) {
		return parse(json, byte[].class);
	}

}
