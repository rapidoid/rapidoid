package org.rapidoid.db;

/*
 * #%L
 * rapidoid-db-tests
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

import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.db.model.IPerson;
import org.rapidoid.db.model.Person;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class EntityPropertyRetrievalTest extends DbTestCommons {

	@Test
	public void testInterfaceProperties() {
		Set<String> names = U.set(Beany.propertiesOf(IPerson.class).names);
		eq(names, U.set("id", "version", "name", "age", "title", "address"));
	}

	@Test
	public void testClassProperties() {
		Set<String> names = U.set(Beany.propertiesOf(Person.class).names);
		eq(names, U.set("id", "version", "name", "age", "title", "address", "worksAt", "profile"));
	}

}
