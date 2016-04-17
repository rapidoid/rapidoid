package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.commons.Str;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-commons
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

/**
 * @author Nikolche Mihajlovski
 * @since 5.0.2
 */
public class ConfigHelp extends RapidoidThing {

	public static void processHelp(Object[] args) {
		if (args != null && args.length == 1 && args[0].equals("--help")) {
			show("Usage:");
			show("  java -cp <yourapp>.jar com.yourapp.Main [option1 option2 ...]");

			show("\nExample:");
			show("  java -cp <yourapp>.jar com.yourapp.Main app.port=9090 app.address=127.0.0.1 production secret=my-secret");

			show("\nAvailable options:");

			for (ConfigOption opt : ConfigOptions.ALL) {
				String desc = U.frmt("%s (default: %s)", opt.getDesc(), opt.getDefaultValue());
				opt(opt.getName(), desc);
			}

			System.exit(0);
		}
	}

	private static void opt(String opt, String desc) {
		show("  " + opt + Str.mul(" ", 21 - opt.length()) + " - " + desc);
	}

	private static void show(String msg) {
		System.out.println(msg);
	}

}
