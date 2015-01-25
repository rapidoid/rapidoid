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

import org.rapidoid.db.model.IPerson;
import org.rapidoid.util.OptimisticConcurrencyControlException;
import org.testng.annotations.Test;

public class DbInterfaceOCCTest extends DbTestCommons {

	@Test(expectedExceptions = OptimisticConcurrencyControlException.class)
	public void testOCCFailure() {
		IPerson p1 = DB.entity(IPerson.class);
		DB.persist(p1);

		eq(p1.version().get().longValue(), 1);

		IPerson p2 = DB.entity(IPerson.class);
		p2.id().set(p1.id().get());

		DB.persist(p2);
	}

	@Test
	public void testOCC() {
		IPerson p1 = DB.entity(IPerson.class);
		DB.persist(p1);

		eq(p1.version().get().longValue(), 1);

		IPerson p2 = DB.entity(IPerson.class);
		p2.id().set(p1.id().get());

		DB.refresh(p2);
		eq(p2.version().get().longValue(), 1);

		DB.persist(p2);
	}

}
