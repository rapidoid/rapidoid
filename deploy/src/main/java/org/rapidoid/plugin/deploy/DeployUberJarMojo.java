package org.rapidoid.plugin.deploy;

/*
 * #%L
 * Rapidoid Deploy Plugin
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
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
@Mojo(name = "uber-jar", aggregator = true, defaultPhase = LifecyclePhase.DEPLOY)
public class DeployUberJarMojo extends AbstractRapidoidMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject project;

	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	protected MavenSession session;

	@Parameter(defaultValue = "${token}", required = false)
	protected String token = "";

	@Parameter(defaultValue = "${servers}", required = false)
	protected String servers = "";

	public void execute() throws MojoExecutionException {
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
			ok &= doStage(jar, server + "/_stage");
		}

		failIf(!ok, "The staging failed on at least 1 server. Aborting the deployment!");

		for (String server : targetServers) {
			doDeploy(server + "/_deploy");
		}
	}

	private void validateConfig() throws MojoExecutionException {
		if (U.isEmpty(servers)) throw new MojoExecutionException("The 'servers' must be configured!");
		if (U.isEmpty(token)) throw new MojoExecutionException("The 'token' must be configured!");
	}

	private List<String> getServers() {
		String[] srvrs = servers.split("\\s*\\,\\s*");

		for (int i = 0; i < srvrs.length; i++) {
			String srvr = Str.trimr(srvrs[i], "/");
			if (!srvr.startsWith("http")) srvr = "http://" + srvr;
			srvrs[i] = srvr;
		}

		return U.list(srvrs);
	}

	private void initConfig() {
		Config config = new ConfigImpl("deploy");
		config.setPath(project.getBasedir().toString());

		Map<String, Object> cfg = config.toMap();

		if (U.isEmpty(token)) token = U.safe(U.str(cfg.get("token")));
		if (U.isEmpty(servers)) servers = U.safe(U.str(cfg.get("servers")));
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
