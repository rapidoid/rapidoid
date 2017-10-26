package org.rapidoid.setup;

/*
 * #%L
 * rapidoid-http-server
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
import org.rapidoid.commons.Arr;
import org.rapidoid.config.Conf;
import org.rapidoid.config.ConfigHelp;
import org.rapidoid.env.Env;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.4.6")
class AppStarter extends RapidoidThing {

	private static boolean started = false;

	static synchronized void reset() {
		started = false;
	}

	static synchronized void startUp(String[] args, String... extraArgs) {

		U.must(!started, "The application was already started!");
		started = true;

		args = Arr.concat(extraArgs, args);

		ConfigHelp.processHelp(args);

		Env.setArgs(args);

		U.must(!Conf.isInitialized(), "The configuration shouldn't be initialized yet!");
	}

}
