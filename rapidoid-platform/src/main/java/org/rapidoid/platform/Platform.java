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

import org.rapidoid.AuthBootstrap;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Arr;
import org.rapidoid.deploy.AppDeployer;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;
import org.rapidoid.util.AppInfo;
import org.rapidoid.util.Msc;

import java.awt.*;
import java.net.URI;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class Platform extends RapidoidThing {

	private static AppChangeWatcher appChangeWatcher = new AppChangeWatcher();

	static void start(String[] args, @SuppressWarnings("unused") boolean defaults) {

		initializePlatform();

		interceptSpecialCommands(args);

		// Rapidoid banner
		U.print(IO.load("rapidoid.txt"));

		startPlatformAndProcessArgs(args);

		AppDeployer.bootstrap();

		if (!Setup.isAnyRunning()) {
			On.setup().activate();
		}

		AuthBootstrap.bootstrapAdminCredentials();

		appChangeWatcher.watch("/app", "app");

		openInBrowser();
	}

	private static void interceptSpecialCommands(String[] args) {
		// interpret Maven command
		if (U.notEmpty(args) && args[0].equals("mvn")) {
			List<String> mvnArgs = U.list(Arr.sub(args, 1, args.length));
			int result = MavenUtil.build("/app", "/data/.m2/repository", mvnArgs);
			System.exit(result);
		}
	}

	private static void initializePlatform() {
		Msc.setPlatform(true);

		Log.options().prefix("[PLATFORM] ");
		Log.options().inferCaller(false);
		Log.options().showThread(false);
	}

	private static void startPlatformAndProcessArgs(String[] args) {
		List<String> normalArgs = U.list();
		List<String> appRefs = U.list();

		separateArgs(args, normalArgs, appRefs);

		App.run(U.arrayOf(String.class, normalArgs));

		for (String appRef : appRefs) {
			AppDownloader.download(appRef, "/apps");
			MavenUtil.findAndBuildAndDeploy("/apps");
		}
	}

	private static void separateArgs(String[] args, List<String> normalArgs, List<String> appRefs) {
		for (String arg : args) {
			if (arg.startsWith("@")) {
				String appRef = arg.substring(1);
				appRefs.add(appRef);
			} else {
				normalArgs.add(arg);
			}
		}
	}

	private static void openInBrowser() {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(new URI(U.frmt("http://localhost:%s/", AppInfo.appPort)));
			}
		} catch (Exception e) {
			// do nothing
		}
	}

}
