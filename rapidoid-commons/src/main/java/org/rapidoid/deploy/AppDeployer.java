package org.rapidoid.deploy;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.process.Proc;
import org.rapidoid.process.ProcessHandle;
import org.rapidoid.process.Processes;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/*
 * #%L
 * rapidoid-commons
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

	private static final Processes DEPLOYED_PROCESSES = new Processes("deployed");

	private static void runIfExists(String appJar) {
		if (new File(appJar).exists()) {
			Log.info("Deploying pre-existing application JAR", "filename", appJar);

			runJar(appJar);
		}
	}

	private static void runJar(String appJar) {
		Proc.group(DEPLOYED_PROCESSES).run("java", "-jar", appJar);
	}

	public static void deploy(String appJar) {
		Log.info("Deploying JAR...", "filename", appJar);
		U.must(U.notEmpty(appJar), "Empty application jar name was provided!");

		String stagedAppJar = appJar + ".staged";
		U.must(new File(stagedAppJar).exists(), "Cannot deploy, the application needs to be staged first!");

		try {
			Files.move(Paths.get(stagedAppJar), Paths.get(appJar), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw U.rte("Deployment error!", e);
		}

		for (ProcessHandle handle : DEPLOYED_PROCESSES.items()) {
			Log.info("Terminating the previously deployed application", "process", handle.params().command());
			handle.destroy();
		}

		runJar(appJar);

		Log.info("Deployed JAR", "filename", appJar);
	}

	public static void stageJar(String appJar, byte[] content) {
		String stagedAppJar = appJar + ".staged";

		U.must(U.notEmpty(appJar), "Empty application jar name was provided!");

		IO.save(stagedAppJar, content);

		Log.info("Staged application jar", "size", content, "destination", appJar);
	}

	public static Processes processes() {
		return DEPLOYED_PROCESSES;
	}

	public static void bootstrap() {
		runIfExists(ClasspathUtil.appJar());
	}
}
