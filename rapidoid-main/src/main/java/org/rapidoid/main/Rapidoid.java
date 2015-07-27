package org.rapidoid.main;

/*
 * #%L
 * rapidoid-main
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.appctx.Application;
import org.rapidoid.appctx.Applications;
import org.rapidoid.log.Log;
import org.rapidoid.quick.Quick;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class Rapidoid {

	private static boolean initialized = false;

	public static synchronized void run(String[] args, Object... config) {
		Application noApp = null;
		run(noApp, args, config);
	}

	public static synchronized void run(Application app, String[] args, Object... config) {
		Log.info("Starting Rapidoid...");
		U.must(!initialized, "Already initialized!");
		initialized = true;

		if (app == null) {
			app = Applications.root();
		}

		MainHelp.processHelp(args);

		Quick.run(app, args, config);
	}

	public static void register(Application app) {
		Applications.main().register(app);
	}

	public static void unregister(Application app) {
		Applications.main().unregister(app);
	}

}
