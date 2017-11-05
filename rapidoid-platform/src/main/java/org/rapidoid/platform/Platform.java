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
import org.rapidoid.config.Conf;
import org.rapidoid.deploy.AppDownloader;
import org.rapidoid.deploy.SingleApp;
import org.rapidoid.env.Env;
import org.rapidoid.log.Log;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;
import org.rapidoid.util.AnsiColor;
import org.rapidoid.util.Msc;

import java.io.File;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class Platform extends RapidoidThing {

	static void start(CmdArgs cmdArgs) {
		Msc.printRapidoidBanner();

		U.must(Msc.isPlatform());

		// prepare args
		List<String> args = U.list(cmdArgs.args);
		args.add("default_root=" + defaultRoot());

		// start application
		App.init(U.arrayOf(String.class, args));

		// verify the platform was initialized correctly
		verifyPlatformSetup();

		// [The application has started!]
		String mode = PlatformOpts.isMultiProcess() ? "multi-process" : "single-process";
		Log.info("The platform has started", "!mode", mode);

		// bootstrap services
		App.boot().services();

		if (PlatformOpts.isMultiProcess()) {
			// multi-process mode

			printAdminCenterURL();
			processExternalApps(cmdArgs.refs);

			if (PlatformOpts.isSingleApp()) {
				SingleApp.deploy();
			}

		} else {
			// single-process mode
			// the app was already bootstrapped on App.run(...)
		}

		App.ready();

		if (!Setup.isAnyRunning()) {
			On.setup().activate();
		}
	}

	private static String defaultRoot() {
		if (PlatformOpts.hasAppFolder()) {
			return PlatformOpts.singleAppPath();

		} else {
			return PlatformOpts.platformPath();
		}
	}

	private static void verifyPlatformSetup() {
		U.must(Msc.isPlatform());
		U.must(Env.isInitialized(), "The environment must already be initialized!");
		U.must(Conf.isInitialized(), "The configuration must already be initialized!");
		U.notNull(Env.root(), "root path");
	}

	private static void printAdminCenterURL() {
		if (Env.dev()) {
			long port = Conf.RAPIDOID.entry("port").num().get();
			String url = "http://localhost:" + port + "/rapidoid";
			Msc.logSection(AnsiColor.lightBlue("Rapidoid Admin Center: ") + url);
		}
	}

	private static void processExternalApps(List<String> refs) {
		for (String appRef : refs) {
			new File(PlatformOpts.appsPath()).mkdirs();
			AppDownloader.download(appRef, PlatformOpts.appsPath());
		}
	}

}
