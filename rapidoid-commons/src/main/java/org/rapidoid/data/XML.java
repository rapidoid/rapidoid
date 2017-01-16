package org.rapidoid.data;

import org.rapidoid.RapidoidThing;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
 * @since 4.4.0
 */
public class XML extends RapidoidThing {

	private XML() {
	}

	public static String stringify(Object obj) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(obj, out);

			return out.toString();

		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(String xml, Class<T> valueType) {
		return parse(xml.getBytes(), valueType);
	}

	@SuppressWarnings("unchecked")
	public static <T> T parse(byte[] xml, Class<T> valueType) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(valueType);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			return (T) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(xml));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
