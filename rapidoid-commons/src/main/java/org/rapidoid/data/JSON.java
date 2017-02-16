package org.rapidoid.data;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializerCache;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.rapidoid.RapidoidThing;
import org.rapidoid.cls.Cls;
import org.rapidoid.env.Env;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.TUUID;

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
 * @since 2.0.0
 */
public class JSON extends RapidoidThing {

	public static final ObjectMapper MAPPER = newMapper();

	public static final ObjectMapper PRETTY_MAPPER = prettyMapper();

	public static ObjectMapper newMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setBase64Variant(Base64Variants.MODIFIED_FOR_URL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		mapper.registerModule(tuuidModule());

		if (!Env.dev()) {
			mapper.registerModule(new AfterburnerModule());
		}

		return mapper;
	}

	private static ObjectMapper prettyMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setBase64Variant(Base64Variants.MODIFIED_FOR_URL);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		mapper.registerModule(tuuidModule());

		if (!Env.dev()) {
			mapper.registerModule(new AfterburnerModule());
		}

		DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
		pp = pp.withObjectIndenter(new DefaultIndenter("  ", "\n"));

		mapper.setDefaultPrettyPrinter(pp);

		return mapper;
	}

	public static SimpleModule tuuidModule() {
		SimpleModule tuuidModule = new SimpleModule("TUUIDModule", new Version(1, 0, 0, null, "org.rapidoid", "rapidoid-commons"));

		tuuidModule.addSerializer(TUUID.class, new TUUIDSerializer());
		tuuidModule.addDeserializer(TUUID.class, new TUUIDDeserializer());

		return tuuidModule;
	}

	public static synchronized void reset() {
		for (ObjectMapper mapper : U.list(MAPPER, PRETTY_MAPPER)) {

			SerializerProvider serializerProvider = mapper.getSerializerProvider();

			if (serializerProvider instanceof DefaultSerializerProvider) {
				DefaultSerializerProvider provider = (DefaultSerializerProvider) serializerProvider;
				provider.flushCachedSerializers();
			} else {
				Log.warn("Couldn't clear the cache of Jackson serializers!", "class", Cls.of(serializerProvider));
			}

			DeserializationContext deserializationContext = mapper.getDeserializationContext();
			Object cache = Cls.getFieldValue(deserializationContext, "_cache");

			if (cache instanceof DeserializerCache) {
				DeserializerCache deserializerCache = (DeserializerCache) cache;
				deserializerCache.flushCachedDeserializers();
			} else {
				Log.warn("Couldn't clear the cache of Jackson deserializers!", "class", Cls.of(cache));
			}
		}
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

	public static void warmUp() {
		Msc.thread(new Runnable() {
			@Override
			public void run() {
				JSON.stringify(123);
				JSON.parse("{}", Map.class);
			}
		});
	}

	public static byte[] parseBytes(String json) {
		return parse(json, byte[].class);
	}

}
