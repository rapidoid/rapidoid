package org.rapidoid.jdbc;

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

import org.rapidoid.AbstractRapidoidModule;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.RapidoidModuleDesc;
import org.rapidoid.log.Log;

@Authors("Nikolche Mihajlovski")
@RapidoidModuleDesc(name = "SQL", order = 500)
public class SQLModule extends AbstractRapidoidModule {

	private static final String HSQLDB_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

	public static final String HSQLDB_TRUNCATE = "TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK";

	public static final String HSQLDB_DROP_ALL = "DROP SCHEMA public CASCADE";

	private static final String H2_DRIVER = "org.h2.Driver";

	public static final String H2_DROP_ALL = "DROP ALL OBJECTS DELETE FILES";

	@Override
	public void afterTest(Object test) {
		cleanInMemDatabases();
		cleanUp();
	}

	@Override
	public void cleanUp() {
		JDBC.reset();
	}

	public static void cleanInMemDatabases() {
		JdbcClient jdbc = JDBC.api().usePool(false);

		if (HSQLDB_DRIVER.equals(jdbc.driver())) {
			Log.info("Dropping all objects in the HSQLDB database");
			jdbc.execute(HSQLDB_TRUNCATE);
			jdbc.execute(HSQLDB_DROP_ALL);
		}

		if (H2_DRIVER.equals(jdbc.driver())) {
			Log.info("Dropping all objects in the H2 database");
			jdbc.execute(H2_DROP_ALL);
		}
	}
}
