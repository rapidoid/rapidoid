package org.rapidoid.plugin.app;

/*
 * #%L
 * Rapidoid App Plugin
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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.config.Config;
import org.rapidoid.config.ConfigImpl;
import org.rapidoid.http.HTTP;
import org.rapidoid.io.IO;
import org.rapidoid.io.Upload;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
@Mojo(name = "deploy", aggregator = true, defaultPhase = LifecyclePhase.DEPLOY)
public class AppDeployMojo extends AbstractRapidoidMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject project;

	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	protected MavenSession session;

	@Parameter(defaultValue = "${token}")
	protected String token = "";

	@Parameter(defaultValue = "${servers}")
	protected String servers = "";

	public void execute() throws MojoExecutionException {
		Msc.setMavenBuild(true);
		initConfig();

		String uberJar = buildUberJar(project, session);

		deploy(uberJar);
	}

	protected void deploy(String uberJar) throws MojoExecutionException {

		validateConfig();

		List<String> targetServers = getServers();

		getLog().info(U.frmt("Deploying the uber-jar to %s servers...", targetServers.size()));
		getLog().info("");

		Upload jar = new Upload("app.jar", IO.loadBytes(uberJar));

		boolean ok = true;
		for (String server : targetServers) {
			ok &= doStage(jar, server + "/rapidoid/stage");
		}

		failIf(!ok, "The staging failed on at least 1 server. Aborting the deployment!");

		for (String server : targetServers) {
			doDeploy(server + "/rapidoid/deploy");
		}
	}

	private void validateConfig() throws MojoExecutionException {
		if (U.isEmpty(servers)) {
			servers = "localhost";
			Log.warn("No 'servers' were configured, using 'localhost' as default");
		}

		if (U.isEmpty(token)) {
			token = "";
			Log.warn("No 'token' was configured, using empty token as default");
		}
	}

	private List<String> getServers() {
		String[] srvrs = servers.split("\\s*,\\s*");

		for (int i = 0; i < srvrs.length; i++) {
			String server = Str.trimr(srvrs[i], "/");
			if (!server.startsWith("http")) server = "http://" + server;
			srvrs[i] = server;
		}

		return U.list(srvrs);
	}

	private void initConfig() {
		Config config = new ConfigImpl("deploy");
		config.setPath(project.getBasedir().toString());

		Map<String, Object> cfg = config.toMap();

		if (U.isEmpty(token)) token = U.safe(U.str(cfg.get("token")));

		if (U.isEmpty(servers)) {
			Object srvrs = cfg.get("servers");

			if (srvrs instanceof String) {
				servers = (String) srvrs;

			} else if (srvrs instanceof List) {
				List list = (List) srvrs;
				servers = U.join(", ", list);
			}
		}
	}

	private boolean doStage(Upload jar, String url) {
		getLog().info(" - uploading / staging the uber-jar to: " + url);
		return request(HTTP.post(url).data("_token", token).file("file", U.list(jar)));
	}

	private boolean doDeploy(String url) {
		getLog().info(" - deploying the staged application on: " + url);
		return request(HTTP.post(url).data("_token", token));
	}

}
