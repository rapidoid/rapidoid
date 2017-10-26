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
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.config.ConfigHelp;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.performance.BenchmarkCenter;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Main extends RapidoidThing {

	public static void main(String[] args) {
		// configure platform mode
		initPlatform();

		// just print basic info if no args were specified
		if (U.isEmpty(args) && !Msc.isSingleApp()) {
			printWelcome();

		} else {
			ConfigHelp.processHelp(args);

			runMain(args);
		}
	}

	private static void initPlatform() {
		Msc.setPlatform(true);

		Log.options().fancy(!Msc.dockerized());
		Log.options().inferCaller(false);
		Log.options().showThread(false);
	}

	private static void runMain(String[] cliArgs) {
		CmdArgs args = CmdArgs.from(U.list(cliArgs));

		if (args.command == null) {
			Platform.start(args);

		} else {
			interpretCommand(args);
		}
	}

	private static void interpretCommand(CmdArgs args) {
		switch (args.command) {

			case "password":
				// generate new password
				PasswordHashTool.generatePasswordHash(args);
				System.exit(0);
				break;

			case "installer":
				// interpret the "installer" command
				U.must(U.isEmpty(args.args), "No arguments are expected for the 'installer' command!");
				U.print(IO.load("install.sh"));
				System.exit(0);
				break;

			case "benchmark":
				// benchmark - only available in "dev" builds
				BenchmarkCenter.run();
				System.exit(0);
				break;

			case "platform":
				// start the platform
				Platform.start(args);
				break;

			default:
				fail("Unknown command: " + args.command);
		}
	}

	static void fail(String msg) {
		Log.error(msg);
		System.exit(1);
	}

	private static void printWelcome() {
		U.print(RapidoidInfo.nameAndInfo() + "\n");

		U.print(IO.load("welcome.txt"));

		System.exit(0);
	}

}
