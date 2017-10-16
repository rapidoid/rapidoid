package org.rapidoid.platform;

/*-
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
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.4.5")
public class CmdArgs extends RapidoidThing {

	static final String CMD_PLATFORM = "platform";

	static final String[] DEV_CMD_ARGS = {
		CMD_PLATFORM,
		"mode=dev",
		"app.services=center",
		"users.admin.password=admin",
		"secret=NONE",
		"/ -> localhost:8080"
	};

	public final List<String> all;
	public final String command;
	public final List<String> args;
	public final List<String> options;
	public final List<String> refs;
	public final List<String> special;

	private CmdArgs(List<String> all, String command, List<String> args, List<String> options, List<String> refs, List<String> special) {
		this.all = all;
		this.command = command;
		this.args = args;
		this.options = options;
		this.refs = refs;
		this.special = special;
	}

	public static CmdArgs from(List<String> all) {

		// replace shortcuts like "dev"
		findAndReplaceDevArg(all);

		List<String> args = U.list(all);

		// extract command
		String command = null;
		if (U.notEmpty(args) && isCommand(args.get(0))) {
			command = args.remove(0);
		}

		// extract options, references and special args
		List<String> opts = U.list();
		List<String> refs = U.list();
		List<String> special = U.list();
		extractOptionsAndRefs(args, opts, refs, special);

		return new CmdArgs(all, command, args, opts, refs, special);
	}

	public static CmdArgs from(String... args) {
		return from(U.list(args));
	}

	private static boolean isCommand(String s) {
		return s.matches("[a-z]+");
	}

	private static void findAndReplaceDevArg(List<String> args) {
		int pos = args.indexOf("dev");

		if (pos >= 0) {
			// replace "dev" with multiple args
			args.remove(pos);
			args.addAll(pos, U.list(DEV_CMD_ARGS));
		}
	}

	private static void extractOptionsAndRefs(List<String> args, List<String> opts, List<String> refs, List<String> special) {
		for (String arg : args) {

			if (arg.startsWith("@")) {
				refs.add(arg.substring(1));

			} else if (arg.contains("=")) {
				opts.add(arg);

			} else if (Msc.isSpecialArg(arg)) {
				special.add(arg);

			} else {
				throw U.rte("Invalid argument: '%s'!", arg);
			}
		}
	}

	public void print() {
		Log.info("Command-line arguments:");

		for (String arg : all) {
			Log.info("  " + arg);
		}

		Log.info("");
	}

}
