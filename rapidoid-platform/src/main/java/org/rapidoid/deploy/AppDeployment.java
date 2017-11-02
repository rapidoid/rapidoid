package org.rapidoid.deploy;

/*-
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

import org.apache.commons.io.FileUtils;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.env.Env;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.process.Proc;
import org.rapidoid.process.ProcessHandle;
import org.rapidoid.reverseproxy.Reverse;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Authors("Nikolche Mihajlovski")
@Since("5.4.7")
public class AppDeployment extends RapidoidThing {

	private static final String CLASSPATH = System.getProperty("java.class.path");

	private static final AtomicInteger PORTS = new AtomicInteger(10000);
	private final String name;
	private final String path;
	private final int port;
	private final AppChangeWatcher watcher = new AppChangeWatcher();

	private volatile ProcessHandle process;

	private AppDeployment(String name, String path, int port) {
		this.name = U.notNull(name, "name");
		this.path = U.notNull(path, "path");
		this.port = port;
		this.watcher.register(this);
	}

	public static AppDeployment fromFilename(String filename) {
		String name = inferAppName(filename);
		return create(name);
	}

	public static AppDeployment create(String name) {
		String path = Msc.path(MscOpts.appsPath(), name);
		return create(name, path, PORTS.incrementAndGet());
	}

	public static AppDeployment create(String name, String path, int port) {
		return new AppDeployment(name, path, port);
	}

	private static String inferAppName(String fullFilename) {
		String filename = new File(fullFilename).getName();
		String name = Str.cutToFirst(filename, ".");
		return U.or(name, filename);
	}

	public void stage(String filename, byte[] content) {
		U.must(U.notEmpty(filename), "Empty application filename was provided!");

		String stagedFilename = Msc.path(path(), filename + ".staged");
		IO.save(stagedFilename, content);

		Log.info("Staged application", "application", name, "filename", filename, "size",
			Msc.fileSizeReadable(content.length), "destination", stagedFilename);
	}

	public void deploy() {
		List<String> stagedFiles = findStaged();

		U.must(U.notEmpty(stagedFiles), "Cannot find the staged file!");
		U.must(stagedFiles.size() < 2, "Found more than 1 staged files!");

		String filename = stagedFiles.get(0);
		U.must(filename.endsWith(".staged"));
		String targetFilename = Str.trimr(filename, ".staged");

		String staged = Msc.path(path, filename);
		String target = Msc.path(path, targetFilename);

		Log.info("Deploying staged application...", "application", name, "staged", filename, "destination", target);

		U.must(new File(staged).exists(), "Cannot deploy, the application needs to be staged first, cannot find: %s", staged);

		watcher.active(false);

		try {
			Files.move(Paths.get(staged), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
			startOrRestartApp();

		} catch (IOException e) {
			throw U.rte("Deployment error!", e);

		} finally {
			watcher.active(true);
		}

		Log.info("Deployed application", "application", name, "filename", filename, "destination", target);
	}

	private List<String> findStaged() {
		return IO.find("*.staged").files().in(path).getRelativeNames();
	}

	public boolean exists() {
		return new File(path()).exists();
	}

	public boolean isEmpty() {
		return !exists() || IO.find().in(path()).getRelativeNames().isEmpty();
	}

	public void delete() {
		Log.info("Deleting application", "application", name, "path", path());

		stop();

		try {
			FileUtils.forceDelete(new File(path()));

		} catch (IOException e) {
			throw U.rte(e);
		}
	}

	void watch() {
		watcher.watch();
	}

	private void startOrRestartApp() {
		restart();
	}

	public void start() {
		String jar = jarName();

		String[] appJarCmd = {"java", "-jar", jar};

		String[] defaultAppCmd = {"java", "-cp", CLASSPATH, "org.rapidoid.platform.DefaultApp"};

		boolean hasJar = new File(jar).exists();
		String[] cmd = hasJar ? appJarCmd : defaultAppCmd;
		run(cmd);

		Reverse.proxy("/" + name).to("localhost:" + port).add();
	}

	public String jarName() {
		return Msc.path(path, "app.jar");
	}

	private void run(String[] cmd) {

		Map<String, String> env = U.map(
			"ID", name,
			"ON_PORT", port + "",
			"ROOT", path,
			"MODE", Env.mode().name().toLowerCase()
		);

		process = Proc.group(Apps.processes())
			.id(name)
			.printingOutput(true)
			.env(env)
			.run(cmd);
	}

	public void stop() {
		if (process != null && process.isAlive()) {
			Log.info("Stopping application", "application", name, "process", process.params().command());
			process.terminate();
			Apps.processes().remove(process);
		}
	}

	public void restart() {
		Log.info("Restarting application", "application", name);
		stop();
		start();
	}

	public void create() {
		if (!exists()) {
			Log.info("Creating application", "application", name);

			try {
				FileUtils.forceMkdir(new File(path));

			} catch (IOException e) {
				throw U.rte(e);
			}
		}
	}

	void onAppChanged(String filename) {
		restart();
	}

	public String name() {
		return name;
	}

	public String path() {
		return path;
	}

	public boolean isAlive() {
		return process != null && process.isAlive();
	}

	public int port() {
		return port;
	}

	public boolean isStaged() {
		return !findStaged().isEmpty();
	}

	public ProcessHandle process() {
		return process;
	}
}
