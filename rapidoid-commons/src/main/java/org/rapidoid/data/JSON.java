package org.rapidoid.data;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.rapidoid.RapidoidThing;

import java.io.OutputStream;
import java.util.Map;

/*
 * #%L
 * rapidoid-commons
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

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class JSON extends RapidoidThing {

	public static final ObjectMapper MAPPER = mapper();

	public static final ObjectMapper PRETTY_MAPPER = prettyMapper();

	private static ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setBase64Variant(Base64Variants.MODIFIED_FOR_URL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new AfterburnerModule());
		return mapper;
	}

	private static ObjectMapper prettyMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setBase64Variant(Base64Variants.MODIFIED_FOR_URL);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.registerModule(new AfterburnerModule());

		DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
		pp = pp.withObjectIndenter(new DefaultIndenter("  ", "\n"));

		mapper.setDefaultPrettyPrinter(pp);

		return mapper;
	}

	public static String stringify(Object value) {
		try {
			return MAPPER.writeValueAsString(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] stringifyToBytes(Object value) {
		try {
			return MAPPER.writeValueAsBytes(value);
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

	public static String prettify(Object value) {
		try {
			return PRETTY_MAPPER.writeValueAsString(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void prettify(Object value, OutputStream out) {
		try {
			PRETTY_MAPPER.writeValue(out, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T parse(byte[] json) {
		try {
			return (T) MAPPER.readValue(json, Object.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T parse(String json) {
		try {
			return (T) MAPPER.readValue(json, Object.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(String json, Class<T> valueType) {
		try {
			return MAPPER.readValue(json, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(byte[] json, Class<T> valueType) {
		try {
			return MAPPER.readValue(json, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(String json, TypeReference<T> valueType) {
		try {
			return MAPPER.readValue(json, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(byte[] json, TypeReference<T> valueType) {
		try {
			return MAPPER.readValue(json, valueType);
		} catch (Exception e) {
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

	public static byte[] parseBytes(String json) {
		return parse(json, byte[].class);
	}

}
