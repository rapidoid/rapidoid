package org.rapidoid.data;

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

import org.junit.Test;
import org.rapidoid.data.Parse.DataFormat;
import org.rapidoid.test.TestCommons;

/**
 * @author Nikolche Mihajlovski
 * @since 4.4.0
 */
public class ParseTest extends TestCommons {

	@Test
	public void testXMLParse() {
		String xml = XML.stringify(new Person("abc", 123));
		System.out.println(xml);

		Person p = Parse.data(xml, Person.class);

		eq(p.getName(), "abc");
		eq(p.getAge(), 123);
	}

	@Test
	public void testJSONParse() {
		String json = JSON.stringify(new Person("abc", 123));
		System.out.println(json);

		Person p = Parse.data(json, Person.class);

		eq(p.getName(), "abc");
		eq(p.getAge(), 123);

		isNull(Parse.data("null", Person.class));
		eq(Parse.data("3", Integer.class).intValue(), 3);
		isTrue(Parse.data("true", Boolean.class).booleanValue());
		isFalse(Parse.data("false", Boolean.class).booleanValue());
		eq(Parse.data("\"123\"", String.class), "123");
	}

	@Test
	public void testYAMLParse() {
		String yaml = YAML.stringify(new Person("abc", 123));
		System.out.println(yaml);

		Person p = Parse.data(yaml, Person.class);

		eq(p.getName(), "abc");
		eq(p.getAge(), 123);
	}

	@Test
	public void testDataFormatAutoDetect() {
		eq(Parse.detectDataFormat("---\n".getBytes())[0], DataFormat.YAML);
		eq(Parse.detectDataFormat("".getBytes())[0], DataFormat.YAML);
		eq(Parse.detectDataFormat("<abc>".getBytes())[0], DataFormat.XML);
		eq(Parse.detectDataFormat("-1".getBytes())[0], DataFormat.JSON);
		eq(Parse.detectDataFormat("-12345".getBytes())[0], DataFormat.JSON);
		eq(Parse.detectDataFormat("null".getBytes())[0], DataFormat.JSON);
		eq(Parse.detectDataFormat("\"fff\"".getBytes())[0], DataFormat.JSON);
	}

}
