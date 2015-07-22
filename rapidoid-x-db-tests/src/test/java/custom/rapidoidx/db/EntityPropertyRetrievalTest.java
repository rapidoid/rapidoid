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

import java.util.Set;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.util.U;

import custom.rapidoidx.db.model.IPerson;
import custom.rapidoidx.db.model.Person;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class EntityPropertyRetrievalTest extends DbTestCommons {

	@Test
	public void testInterfaceProperties() {
		Set<String> names = U.set(Beany.propertiesOf(IPerson.class).names);
		eq(names, U.set("id", "version", "createdBy", "createdOn", "lastUpdatedBy", "lastUpdatedOn", "name", "age",
				"title", "address"));
	}

	@Test
	public void testClassProperties() {
		Set<String> names = U.set(Beany.propertiesOf(Person.class).names);
		eq(names, U.set("id", "version", "createdBy", "createdOn", "lastUpdatedBy", "lastUpdatedOn", "name", "age",
				"title", "address", "worksAt", "profile"));
	}

}
