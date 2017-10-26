package org.rapidoid.platform;

import org.rapidoid.config.Conf;
import org.rapidoid.env.Env;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.performance.BenchmarkCenter;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

class PlatformCommands {

	static void interpretCommand(CmdArgs args) {
		switch (args.command) {

			case "password":
				// generate new password

				expectNoArgs(args);
				PasswordHashTool.generatePasswordHash();

				System.exit(0);
				break;

			case "installer":
				// interpret the "installer" command

				expectNoArgs(args);
				installer();

				System.exit(0);
				break;

			case "benchmark":
				// benchmark - only available in "dev" builds

				BenchmarkCenter.run();

				System.exit(0);
				break;

			case "verify":
				// verify the dockerization

				expectNoArgs(args);
				verify();

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

	private static void installer() {
		U.print(IO.load("install.sh"));
	}

	private static void verify() {
		U.must(Msc.dockerized(), "Docker environment couldn't be detected!");
		U.must(!Conf.isInitialized(), "The configuration shouldn't be initialized yet!");
		U.must(!Env.isInitialized(), "The environment shouldn't be initialized yet!");

		Log.info("Docker environment was verified!");
	}

	private static void expectNoArgs(CmdArgs args) {
		if (U.notEmpty(args.args)) {
			fail(U.frmt("No arguments are expected for the '%s' command!", args.command));
		}
	}

	private static void fail(String msg) {
		Log.error(msg);
		System.exit(1);
	}

}
