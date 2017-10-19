package org.rapidoid.data;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.rapidoid.RapidoidThing;

import java.io.OutputStream;
import java.util.Map;

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

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class YAML extends RapidoidThing {

	public static final ObjectMapper MAPPER = mapper();

	private static ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.setBase64Variant(Base64Variants.MODIFIED_FOR_URL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new AfterburnerModule());
		return mapper;
	}

	public static String stringify(Object value) {
		try {
			return MAPPER.writeValueAsString(value);
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

	public static <T> T parse(String yaml) {
		try {
			return (T) MAPPER.readValue(yaml, Object.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(String yaml, Class<T> valueType) {
		try {
			return MAPPER.readValue(yaml, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(byte[] yaml, Class<T> valueType) {
		try {
			return MAPPER.readValue(yaml, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(String yaml, TypeReference<T> valueType) {
		try {
			return MAPPER.readValue(yaml, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(byte[] yaml, TypeReference<T> valueType) {
		try {
			return MAPPER.readValue(yaml, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseMap(String yaml) {
		return parse(yaml, Map.class);
	}

}
