package org.rapidoid.sql.test;

/*
 * #%L
 * rapidoid-sql
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
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.data.JSON;
import org.rapidoid.orm.DbEntity;
import org.rapidoid.orm.DbId;
import org.rapidoid.orm.ORM;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class ORMTest extends SQLTestCommons {

	static class Booky extends DbEntity {
		@DbId
		public long id;

		public String title;
	}

	@Test
	public void testORM() {
		ORM.bootstrap(Booky.class, Person.class);

		Booky book = new Booky();
		book.title = "asd";
		book.id = 123;

		book.save();

		Booky b2 = new Booky();
		b2.load(123);

		U.print(JSON.stringify(b2));
	}

}

