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

import org.rapidoid.annotation.Authors;
import org.rapidoid.db.model.IPerson;
import org.rapidoid.db.model.Person;
import org.testng.annotations.Test;

class PersonService extends DAO<Person> {
}

class PersonDAO extends DAO<IPerson> {
}

@Authors("Nikolche Mihajlovski")
public class DAOTest extends DbTestCommons {

	@Test
	public void testDAOWithClassEntity() {

		PersonService service = new PersonService();

		// exercise the entity type inference
		eq(service.getEntityType(), Person.class);

		eq(DB.size(), 0);
		long id = service.insert(new Person("aa", 123));
		eq(DB.size(), 1);

		Person p = service.get(id);
		service.delete(p);
		eq(DB.size(), 0);
	}

	@Test
	public void testDAOWithInterfaceEntity() {

		PersonDAO dao = new PersonDAO();

		// exercise the entity type inference
		eq(dao.getEntityType(), IPerson.class);

		eq(DB.size(), 0);
		long id = dao.insert(DB.entity(IPerson.class));
		eq(DB.size(), 1);

		IPerson p = dao.get(id);
		dao.delete(p);
		eq(DB.size(), 0);
	}

}
