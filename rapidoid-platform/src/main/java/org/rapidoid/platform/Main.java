package org.rapidoid.platform;

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
import org.rapidoid.commons.Arr;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Collections;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Main extends RapidoidThing {

	private static final String[] DEFAULT_ARGS = {
		"admin.services=center"
	};

	private static final String[] DEV_CMD_ARGS = {
		"app.services=center",
		"users.admin.password=admin",
		"secret=NOT-A-REAL-SECRET"
	};

	public static void main(String[] args) {

		boolean noArgs = U.isEmpty(args);
		List<String> opts = U.list();

		if (noArgs) {
			defaultSetup(opts);

		} else if (args[0].equals("dev")) {
			devSetup(args, opts);

		} else {
			Collections.addAll(opts, args);
		}

		run(opts, noArgs);
	}

	private static void defaultSetup(List<String> opts) {
		Collections.addAll(opts, DEFAULT_ARGS);
		optionalAppSetup(opts);
	}

	private static void devSetup(String[] args, List<String> opts) {
		Collections.addAll(opts, DEV_CMD_ARGS);
		optionalAppSetup(opts);
		Collections.addAll(opts, Arr.sub(args, 1, args.length));
	}

	private static void optionalAppSetup(List<String> opts) {
		if (Msc.hasMainApp()) {
			opts.add("/ -> localhost:8080");
		} else {
			opts.add("app.services=welcome");
		}
	}

	private static void run(List<String> opts, boolean defaults) {
		Platform.start(U.arrayOf(String.class, opts), defaults);
	}

}
