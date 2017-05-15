package org.rapidoid.deploy;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.process.Proc;
import org.rapidoid.process.ProcessHandle;
import org.rapidoid.process.Processes;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class AppDeployer extends RapidoidThing {

	private static final Processes DEPLOYED = Processes.GROUP;

	private static final String CLASSPATH = System.getProperty("java.class.path");

	private static final AppChangeWatcher APP_CHANGE_WATCHER = new AppChangeWatcher("/app", "app");

	private static void runIfExists(String appId, String appJar) {
		if (Msc.hasMainApp()) {
			Log.info("Deploying pre-existing application", "id", appId);

			runAppJar(appId);
		}
	}

	private static void runAppJar(String appId) {
		String appJar = Msc.mainAppJar();

		String[] appJarCmd = {"java", "-jar", appJar, "root=/app"};
		String[] defaultAppCmd = {"java", "-cp", CLASSPATH, "org.rapidoid.platform.DefaultApp", "root=/app"};

		String[] cmd = new File(appJar).exists() ? appJarCmd : defaultAppCmd;

		Proc.group(DEPLOYED)
			.id(appId)
			.printingOutput(true)
			.run(cmd);
	}

	public static void deploy(String stagedAppJar, String appJar) {
		U.must(U.notEmpty(stagedAppJar), "Empty application jar name was provided!");

		Log.info("Deploying staged JAR...", "filename", stagedAppJar);

		U.must(new File(stagedAppJar).exists(), "Cannot deploy, the application needs to be staged first, cannot find: %s", stagedAppJar);

		APP_CHANGE_WATCHER.active(false);

		try {
			Files.move(Paths.get(stagedAppJar), Paths.get(appJar), StandardCopyOption.REPLACE_EXISTING);
			startOrRestartApp("app");

		} catch (IOException e) {
			throw U.rte("Deployment error!", e);
		} finally {
			APP_CHANGE_WATCHER.active(true);
		}

		Log.info("Deployed JAR", "filename", appJar);
	}

	static void startOrRestartApp(String appId) {
		Msc.logSection("Restarting the application");

		ProcessHandle proc = DEPLOYED.find(appId);

		if (proc != null) {
			Log.info("Stopping the previously deployed application", "id", proc.id(), "process", proc.params().command());
			proc.terminate();
			DEPLOYED.remove(proc);
		}

		Log.info("Starting the deployed application");
		runAppJar(appId);
	}

	static void stageJar(String appJar, byte[] content) {
		String stagedAppJar = appJar + ".staged";

		U.must(U.notEmpty(appJar), "Empty application jar name was provided!");

		IO.save(stagedAppJar, content);

		Log.info("Staged application jar", "size", content.length, "destination", appJar);
	}

	static Processes processes() {
		return DEPLOYED;
	}

	public static void bootstrap() {
		String appJar = Msc.mainAppJar();
		if (U.notEmpty(appJar)) {
			runIfExists("app", appJar);
		}

		APP_CHANGE_WATCHER.watch();
	}

	static void stopApp(String appId) {
		ProcessHandle proc = DEPLOYED.find(appId);

		if (proc != null) {
			Log.info("Stopping application", "id", proc.id(), "process", proc.params().command());
			proc.destroy();
		}
	}

	static void notifyAppChanged(String root, String appId, String filename) {
		startOrRestartApp(appId);
	}
}
