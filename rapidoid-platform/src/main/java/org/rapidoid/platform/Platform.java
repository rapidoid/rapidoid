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
import org.rapidoid.deploy.AppDeployer;
import org.rapidoid.deploy.AppDownloader;
import org.rapidoid.log.Log;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.rapidoid.setup.PreApp;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;

import java.io.File;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class Platform extends RapidoidThing {

	static void start(CmdArgs options) {
		Msc.printRapidoidBanner();

		initializePlatform();

		startPlatformAndProcessOptions(options);

		AppDeployer.bootstrap();

		if (!Setup.isAnyRunning()) {
			On.setup().activate();
		}
	}

	private static void initializePlatform() {
		Msc.setPlatform(true);

		Log.options().inferCaller(false);
		Log.options().showThread(false);
	}

	private static void startPlatformAndProcessOptions(CmdArgs cmdArgs) {
		PreApp.args(U.arrayOf(String.class, cmdArgs.args));

		App.boot().services();

		for (String appRef : cmdArgs.refs) {
			new File(MscOpts.appsPath()).mkdirs();
			AppDownloader.download(appRef, MscOpts.appsPath());
			MavenUtil.findAndBuildAndDeploy(MscOpts.appsPath());
		}
	}

}
