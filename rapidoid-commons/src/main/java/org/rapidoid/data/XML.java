/*-
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
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

package org.rapidoid.data;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.rapidoid.RapidoidThing;
import org.rapidoid.env.Env;
import org.rapidoid.writable.ReusableWritable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Nikolche Mihajlovski
 * @author Dan Cytermann
 * @since 4.4.0
 */
public class XML extends RapidoidThing {

	private XML() {
	}

	public static XmlMapper newMapper() {
		XmlMapper mapper = new XmlMapper();
		mapper.setBase64Variant(Base64Variants.MODIFIED_FOR_URL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		if (!Env.dev()) {
			mapper.registerModule(new AfterburnerModule());
		}

		return mapper;
	}

	public static String stringify(Object obj) {
		XmlMapper mapper = newMapper();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			mapper.writeValue(out, obj);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return out.toString();
	}

	public static void stringify(Object value, ReusableWritable out) {
		XmlMapper mapper = newMapper();

		try {
			mapper.writeValue(out, value);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static <T> T parse(String xml, Class<T> valueType) {
		return parse(xml.getBytes(), valueType);
	}

	@SuppressWarnings("unchecked")
	public static <T> T parse(byte[] xml, Class<T> valueType) {
		try {

			XmlMapper mapper = newMapper();
			return mapper.readValue(xml, valueType);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
