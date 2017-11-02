package org.rapidoid.deploy;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.env.Env;
import org.rapidoid.log.Log;
import org.rapidoid.util.LazyInit;

/*
 * #%L
 * rapidoid-platform
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

@Authors("Nikolche Mihajlovski")
@Since("5.4.7")
public class SingleApp extends RapidoidThing {

	private static final LazyInit<AppDeployment> APP = new LazyInit<>(SingleApp::create);

	private static AppDeployment create() {
		AppDeployment app = AppDeployment.create("app", Env.root(), 8080);
		Apps.deployments().add(new ManageableApp(app));
		return app;
	}

	public static AppDeployment get() {
		return APP.get();
	}

	public static void deploy() {
		AppDeployment app = get();

		if (!app.isEmpty()) {
			Log.info("Deploying the main application");
			app.start();
		}

		app.watch();
	}

}
