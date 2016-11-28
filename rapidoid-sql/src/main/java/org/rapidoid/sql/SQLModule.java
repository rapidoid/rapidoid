package org.rapidoid.sql;

import org.rapidoid.RapidoidModule;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class SQLModule extends RapidoidThing implements RapidoidModule {

	private static final String HSQL_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

	private static final String HSQL_TRUNCATE = "TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK";

	@Override
	public String name() {
		return "SQL";
	}

	@Override
	public void beforeTest(Object test) {
		JDBC.reset();
	}

	@Override
	public void afterTest(Object test) {

		if (HSQL_DRIVER.equals(JDBC.defaultApi().driver())) {
			Log.info("Dropping all objects in the H2 database");
			JDBC.execute(HSQL_TRUNCATE);
		}

		JDBC.reset();
	}

}
