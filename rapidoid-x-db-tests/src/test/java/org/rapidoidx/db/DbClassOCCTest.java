package org.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import org.rapidoid.util.OptimisticConcurrencyControlException;
import org.rapidoidx.db.DB;
import org.rapidoidx.db.model.Person;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbClassOCCTest extends DbTestCommons {

	@Test(expectedExceptions = OptimisticConcurrencyControlException.class)
	public void testOCCFailure() {
		Person p1 = new Person();
		DB.persist(p1);

		eq(p1.version(), 1);

		Person p2 = new Person();
		p2.id(p1.id());

		DB.persist(p2);
	}

	@Test
	public void testOCC() {
		Person p1 = new Person();
		DB.persist(p1);

		eq(p1.version(), 1);

		Person p2 = new Person();
		p2.id(p1.id());

		DB.refresh(p2);
		eq(p2.version(), 1);

		DB.persist(p2);
	}

}
