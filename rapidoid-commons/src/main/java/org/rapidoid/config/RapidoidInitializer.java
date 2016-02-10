package org.rapidoid.config;

import org.rapidoid.cls.Cls;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.log.Log;

public class RapidoidInitializer {

	private static boolean initialized;

	public static synchronized void initialize() {
		if (!initialized) {
			Log.info("Starting Rapidoid...", "version", RapidoidInfo.version());

			Log.info("Working directory is: " + System.getProperty("user.dir"));

			Cls.getClassIfExists("org.rapidoid.web.RapidoidWebModule");

			Log.info("Rapidoid is ready.");

			initialized = true;
		}
	}

}
