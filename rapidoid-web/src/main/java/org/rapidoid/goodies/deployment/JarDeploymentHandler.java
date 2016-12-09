package org.rapidoid.goodies.deployment;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.NiceResponse;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.log.Log;
import org.rapidoid.process.Proc;
import org.rapidoid.process.ProcessHandle;
import org.rapidoid.process.Processes;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;

import java.io.File;

/*
 * #%L
 * rapidoid-web
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class JarDeploymentHandler extends GUI implements ReqHandler {

	private static final String SUCCESS = "Successfully deployed the application";

	private static final String NOT_POSSIBLE = "Cannot deploy, the application jar path was not configured!";

	private static final String STAGE_FIRST = "Cannot deploy, the application needs to be staged first!";

	static final Processes DEPLOYED_PROCESSES = new Processes("deployed");

	public JarDeploymentHandler() {
		init();
	}

	@Override
	public Object execute(Req req) throws Exception {

		String appJar = ClasspathUtil.appJar();

		if (U.isEmpty(appJar)) return NiceResponse.err(req, NOT_POSSIBLE);

		if (!new File(appJar + ".staged").exists()) return NiceResponse.err(req, STAGE_FIRST);

		deploy();

		return NiceResponse.ok(req, SUCCESS);
	}

	public void init() {
		String appJar = ClasspathUtil.appJar();

		if (new File(appJar).exists()) {
			Log.info("Deploying pre-existing application JAR", "filename", appJar);

			deployJar(appJar);
		}
	}

	public void deploy() {
		Log.info("Deploying the staged JAR...");

		String appJar = ClasspathUtil.appJar();
		String stagedAppJar = appJar + ".staged";

		File jar = new File(appJar);

		if (jar.exists()) U.must(jar.delete(), "Couldn't delete the application JAR!");

		U.must(new File(stagedAppJar).renameTo(jar), "Couldn't rename the staged JAR into application JAR!");

		for (ProcessHandle handle : DEPLOYED_PROCESSES.items()) {
			Log.info("Terminating the previously deployed application", "process", handle.params().command());
			handle.destroy();
		}

		deployJar(appJar);

		Log.info("Deployed JAR", "filename", appJar);
	}

	private void deployJar(String appJar) {
		Proc.group(DEPLOYED_PROCESSES).run("java", "-jar", appJar);
	}

}
