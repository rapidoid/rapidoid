package org.rapidoid.test;

import org.junit.Before;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.security.Roles;

public abstract class AbstractCommonsTest extends TestCommons {

	@Before
	public void openContext() {
		Roles.resetConfig();
		Log.setLogLevel(LogLevel.INFO);
	}

}
