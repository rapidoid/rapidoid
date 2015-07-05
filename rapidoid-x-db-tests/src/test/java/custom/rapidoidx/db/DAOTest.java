package custom.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.dao.DAO;
import org.rapidoidx.db.XDB;
import org.testng.annotations.Test;

import custom.rapidoidx.db.model.IPerson;
import custom.rapidoidx.db.model.Person;

class PersonService extends DAO<Person> {}

class PersonDAO extends DAO<IPerson> {}

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DAOTest extends DbTestCommons {

	@Test(enabled = false)
	public void testDAOWithClassEntity() {

		PersonService service = new PersonService();

		// exercise the entity type inference
		eq(service.getEntityType(), Person.class);

		eq(XDB.size(), 0);
		String id = service.insert(new Person("aa", 123));
		eq(XDB.size(), 1);

		Person p = service.get(id);
		service.delete(p);
		eq(XDB.size(), 0);
	}

	@Test(enabled = false)
	public void testDAOWithInterfaceEntity() {

		PersonDAO dao = new PersonDAO();

		// exercise the entity type inference
		eq(dao.getEntityType(), IPerson.class);

		eq(XDB.size(), 0);
		String id = dao.insert(XDB.entity(IPerson.class));
		eq(XDB.size(), 1);

		IPerson p = dao.get(id);
		dao.delete(p);
		eq(XDB.size(), 0);
	}

}
