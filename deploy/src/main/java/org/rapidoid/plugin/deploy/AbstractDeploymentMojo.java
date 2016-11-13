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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.*;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.config.Config;
import org.rapidoid.config.ConfigImpl;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HttpReq;
import org.rapidoid.http.HttpResp;
import org.rapidoid.io.IO;
import org.rapidoid.io.Upload;
import org.rapidoid.u.U;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public abstract class AbstractDeploymentMojo extends AbstractMojo {

	protected static final String ABORT = "Aborting the deployment!";

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject project;

	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	protected MavenSession session;

	@Parameter(defaultValue = "${token}", required = false)
	protected String token;

	@Parameter(defaultValue = "${servers}", required = false)
	protected String servers;

	protected String build(boolean updateSnapshots) throws MojoExecutionException {
		initConfig();

		InvocationRequest request = new DefaultInvocationRequest();

		request.setPomFile(session.getRequest().getPom());
		request.setGoals(U.list("package", "org.apache.maven.plugins:maven-assembly-plugin:2.6:single"));
		request.setAlsoMake(true);
		request.setUpdateSnapshots(updateSnapshots);

		String assemblyFile;
		try {
			assemblyFile = Files.createTempFile("app-assembly-", ".xml").toAbsolutePath().toString();

		} catch (IOException e) {
			throw new MojoExecutionException("Couldn't create temporary assembly descriptor file! " + ABORT, e);
		}

		IO.save(assemblyFile, IO.load("uber-deployment.xml"));

		Properties properties = new Properties();

		properties.setProperty("skipTests", "true");
		properties.setProperty("descriptor", assemblyFile);
		properties.setProperty("assembly.appendAssemblyId", "true");
		properties.setProperty("assembly.attach", "false");

		request.setProperties(properties);

		Invoker invoker = new DefaultInvoker();

		boolean success;
		try {
			InvocationResult result = invoker.execute(request);
			success = result.getExitCode() == 0 && result.getExecutionException() == null;

		} catch (MavenInvocationException e) {
			throw new MojoExecutionException("Invocation error! " + ABORT, e);
		}

		failIf(!success, "The build failed. " + ABORT);

		boolean deleted = new File(assemblyFile).delete();
		if (!deleted) getLog().warn("Couldn't delete the temporary assembly descriptor file!");

		List<String> appJars = IO.find("*-uber-deployment.jar").in(project.getBuild().getDirectory()).getNames();

		failIf(appJars.size() != 1, "Cannot find the deployment JAR (found %s candidates)! " + ABORT, appJars.size());

		String uberJar = U.first(appJars);
		long sizeKB = new File(uberJar).length() / 1024;

		getLog().info("");
		getLog().info(U.frmt("Successfully created the deployment uber-jar (size: %s KB).", sizeKB));
		getLog().info("");

		return uberJar;
	}

	protected void deploy(String uberJar) throws MojoExecutionException {

		List<String> targetServers = U.list(servers.split("\\s*\\,\\s*"));

		// FIXME validate servers and token

		getLog().info(U.frmt("Deploying the uber-jar to %s servers...", targetServers.size()));
		getLog().info("");

		Upload jar = new Upload("app.jar", IO.loadBytes(uberJar));

		boolean ok = true;
		for (String server : targetServers) {
			ok &= doStage(jar, Str.trimr(server, "/") + "/_stage");
		}

		failIf(!ok, "The staging failed on at least 1 server. Aborting the deployment!");

		for (String server : targetServers) {
			doDeploy(Str.trimr(server, "/") + "/_deploy");
		}
	}

	private void initConfig() {
		Config config = new ConfigImpl("deploy");
		config.setPath(project.getBasedir().toString());

		Map<String, Object> cfg = config.toMap();

		if (U.isEmpty(token)) token = U.str(cfg.get("token"));
		if (U.isEmpty(servers)) servers = U.str(cfg.get("servers"));
	}

	private boolean doStage(Upload jar, String url) {
		getLog().info(" - uploading / staging the uber-jar to: " + url);
		return request(HTTP.post(url).data("_token", token).file("file", U.list(jar)));
	}

	private boolean doDeploy(String url) {
		getLog().info(" - deploying the staged application on: " + url);
		return request(HTTP.post(url).data("_token", token));
	}

	private boolean request(HttpReq req) {
		HttpResp resp = req.execute();

		switch (resp.code()) {
			case 200:
				return true;

			case 404:
				getLog().error(U.frmt("Couldn't find: %s", req.url()));
				return false;

			default:
				String msg = "Unexpected response received from: %s! Response code: %s, full response:\n\n%s\n";
				getLog().error(U.frmt(msg, req.url(), resp.code(), resp.body()));
				return false;
		}
	}

	protected void failIf(boolean failureCondition, String msg, Object... args) throws MojoExecutionException {
		if (failureCondition) {
			throw new MojoExecutionException(U.frmt(msg, args));
		}
	}

}
