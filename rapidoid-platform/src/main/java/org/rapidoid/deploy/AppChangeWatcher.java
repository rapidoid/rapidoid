package org.rapidoid.deploy;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.watch.Watch;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Objects;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class AppChangeWatcher extends RapidoidThing implements Operation<String> {

	private volatile boolean active = false;

	private volatile AppDeployment app;

	public void register(AppDeployment app) {
		this.app = Objects.requireNonNull(app);
	}

	public void watch() {
		U.must(app != null, "The app hasn't been registered!");

		active(true);

		if (app.exists()) {
			Log.info("Watching app root for changes...", "root", app.path());
			Watch.dir(app.path(), Watch.simpleListener(this));
		}
	}

	@Override
	public void execute(String filename) throws Exception {
		if (Msc.isAppResource(filename)) {
			onAppChanged(filename);
		}
	}

	public AppChangeWatcher active(boolean active) {
		this.active = active;
		return this;
	}

	private synchronized void onAppChanged(String filename) {
		if (active) {
			Log.info("Detected file system changes of the application", "filename", filename);

			app.onAppChanged(filename);
		}
	}

}
