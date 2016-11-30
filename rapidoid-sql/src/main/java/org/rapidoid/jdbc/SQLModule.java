package org.rapidoid.jdbc;

/*
 * #%L
 * rapidoid-sql
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidModule;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.log.Log;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class SQLModule extends RapidoidThing implements RapidoidModule {

	private static final String HSQLDB_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

	private static final String HSQLDB_TRUNCATE = "TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK";

	private static final String HSQLDB_DROP_ALL = "DROP SCHEMA public CASCADE";

	private static final String H2_DRIVER = "org.h2.Driver";

	private static final String H2_DROP_ALL = "DROP ALL OBJECTS DELETE FILES";

	@Override
	public String name() {
		return "SQL";
	}

	@Override
	public void beforeTest(Object test, boolean isIntegrationTest) {
		JDBC.reset();
	}

	@Override
	public void afterTest(Object test, boolean isIntegrationTest) {
		cleanInMemDatabases();

		JDBC.reset();
	}

	private void cleanInMemDatabases() {
		JdbcClient jdbc = JDBC.defaultApi();

		if (HSQLDB_DRIVER.equals(jdbc.driver())) {
			Log.info("Dropping all objects in the HSQLDB database");
			JDBC.execute(HSQLDB_TRUNCATE);
			JDBC.execute(HSQLDB_DROP_ALL);
		}

		if (H2_DRIVER.equals(jdbc.driver())) {
			Log.info("Dropping all objects in the H2 database");
			JDBC.execute(H2_DROP_ALL);
		}
	}

}
