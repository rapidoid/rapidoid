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

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

class Persons extends TypeReference<List<User>> {
}

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class YAMLTest extends TestCommons {

	private final TypeReference<List<User>> personList = new TypeReference<List<User>>() {
	};

	@Test
	public void parseMap() {
		String yaml = new String(loadRes("test.yaml"));
		Map<String, Object> data = YAML.parseMap(yaml);
		eq(data, U.map("aa", 1, "bb", "2am", "cc", U.map("x", true, "z", false)));
	}

	@Test
	public void parseBeans() {
		String yaml = new String(loadRes("persons.yaml"));

		List<User> persons = YAML.parse(yaml, personList);
		eq(persons.size(), 2);

		User p1 = persons.get(0);
		eq(p1.id, 123);
		eq(p1.name, "John Doe");
		eq(p1.age, 50);

		User p2 = persons.get(1);
		eq(p2.name, "Highlander");
		eq(p2.age, 900);
	}
}
