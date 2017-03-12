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

import javassist.Modifier;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.*;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpReq;
import org.rapidoid.http.HttpResp;
import org.rapidoid.io.IO;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.GlobalCfg;
import org.rapidoid.scan.Scan;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.*;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public abstract class AbstractRapidoidMojo extends AbstractMojo {

	protected static final String ABORT = "Aborting the build!";

	protected void failIf(boolean failureCondition, String msg, Object... args) throws MojoExecutionException {
		if (failureCondition) {
			throw new MojoExecutionException(U.frmt(msg, args));
		}
	}

	protected boolean request(HttpReq req) {
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

	protected String createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) throws MojoExecutionException {
		String assemblyFile;

		try {
			assemblyFile = Files.createTempFile(prefix, suffix, attrs).toAbsolutePath().toString();

		} catch (IOException e) {
			throw new MojoExecutionException("Couldn't create temporary file! " + ABORT, e);
		}

		return assemblyFile;
	}

	protected String createTempDir(String prefix, FileAttribute<?>... attrs) throws MojoExecutionException {
		String assemblyFile;

		try {
			assemblyFile = Files.createTempDirectory(prefix, attrs).toAbsolutePath().toString();

		} catch (IOException e) {
			throw new MojoExecutionException("Couldn't create temporary directory! " + ABORT, e);
		}

		return assemblyFile;
	}

	protected void invoke(MavenSession session, List<String> goals, boolean updateSnapshots, Map<String, String> properties) throws MojoExecutionException {
		if (MscOpts.hasMavenEmbedder() && GlobalCfg.get("maven.home") == null) {
			invokeEmbedded(session, goals, updateSnapshots, properties);
		} else {
			invokeInstalled(session, goals, updateSnapshots, properties);
		}
	}

	protected void invokeEmbedded(MavenSession session, List<String> goals, boolean updateSnapshots, Map<String, String> properties) throws MojoExecutionException {
		EmbeddedMavenCli cli = new EmbeddedMavenCli(session);
		cli.execute(goals, session.getRequest().getBaseDirectory(), updateSnapshots, properties);
	}

	protected void invokeInstalled(MavenSession session, List<String> goals, boolean updateSnapshots, Map<String, String> properties) throws MojoExecutionException {

		InvocationRequest request = new DefaultInvocationRequest();

		request.setPomFile(session.getRequest().getPom());
		request.setGoals(goals);
		request.setAlsoMake(true);
		request.setUpdateSnapshots(updateSnapshots);

		Properties props = new Properties();
		props.putAll(properties);
		request.setProperties(props);

		Invoker invoker = new DefaultInvoker();

		boolean success;

		try {
			InvocationResult result = invoker.execute(request);
			success = result.getExitCode() == 0 && result.getExecutionException() == null;

		} catch (MavenInvocationException e) {
			throw new MojoExecutionException("Invocation error! " + ABORT, e);
		}

		failIf(!success, "An error occurred. " + ABORT);
	}

	protected String buildUberJar(MavenProject project, MavenSession session) throws MojoExecutionException {

		List<String> goals = U.list("package", "org.apache.maven.plugins:maven-assembly-plugin:2.6:single");

		String assemblyFile = createTempFile("app-assembly-", ".xml");

		IO.save(assemblyFile, IO.load("uber-jar.xml"));

		Map<String, String> properties = U.map();
		properties.put("skipTests", "true");
		properties.put("descriptor", assemblyFile);
		properties.put("assembly.appendAssemblyId", "true");
		properties.put("assembly.attach", "false");

		invoke(session, goals, false, properties);

		boolean deleted = new File(assemblyFile).delete();
		if (!deleted) getLog().warn("Couldn't delete the temporary assembly descriptor file!");

		List<String> appJars = IO.find("*-uber-jar.jar").in(project.getBuild().getDirectory()).getNames();

		failIf(appJars.size() != 1, "Cannot find the deployment JAR (found %s candidates)! " + ABORT, appJars.size());

		String uberJar = U.first(appJars);

		try {
			Path uberJarPath = Paths.get(uberJar);
			Path appJar = uberJarPath.getParent().resolve("app.jar");
			Files.move(uberJarPath, appJar, StandardCopyOption.REPLACE_EXISTING);
			uberJar = appJar.toFile().getAbsolutePath();
		} catch (IOException e) {
			throw new MojoExecutionException("Couldn't rename the file! " + ABORT, e);
		}

		String mainClass = findMainClass(project);
		getLog().info("");
		getLog().info("The main class is: " + mainClass);

		try {
			addJarManifest(uberJar, project, mainClass);
		} catch (IOException e) {
			throw new MojoExecutionException("Couldn't add the JAR manifest! " + ABORT, e);
		}

		String size = Msc.fileSizeReadable(uberJar);

		getLog().info("");
		getLog().info("Successfully packaged the application with dependencies:");
		getLog().info(U.frmt("%s (size: %s).", uberJar, size));
		getLog().info("");

		return uberJar;
	}

	private void addJarManifest(String uberJar, MavenProject project, String mainClass) throws IOException {
		Path path = Paths.get(uberJar);
		URI uri = URI.create("jar:" + path.toUri());

		String user = System.getProperty("user.name");

		String manifestContent = IO.load("manifest-template.mf")
			.replace("$user", user)
			.replace("$java", Msc.javaVersion())
			.replace("$name", project.getName())
			.replace("$version", project.getVersion())
			.replace("$groupId", project.getGroupId())
			.replace("$organization", project.getOrganization() != null ? U.or(project.getOrganization().getName(), "?") : "?")
			.replace("$url", U.or(project.getUrl(), "?"))
			.replace("$main", U.safe(mainClass));

		try (FileSystem fs = FileSystems.newFileSystem(uri, U.<String, Object>map())) {
			Path manifest = fs.getPath("META-INF/MANIFEST.MF");
			try (Writer writer = Files.newBufferedWriter(manifest, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
				writer.write(manifestContent);
			}
		}
	}

	protected String findMainClass(MavenProject project) {
		List<String> mainClasses = U.list();

		try {
			for (String path : project.getCompileClasspathElements()) {

				if (new File(path).isDirectory()) {
					getLog().info("Scanning classpath directory: " + path);
					scanForMainClass(path, mainClasses);

				} else if (!path.endsWith(".jar")) {
					getLog().warn("Ignoring classpath entry: " + path);
				}
			}

		} catch (Exception e) {
			throw U.rte(e);
		}

		switch (mainClasses.size()) {
			case 0:
				getLog().warn("Couldn't find the main class!");
				return null;

			case 1:
				return U.first(mainClasses);

			default:
				getLog().warn("Found multiple main classes, trying to pick the right one: " + mainClasses);
				return pickMainClass(mainClasses, project);
		}
	}

	private String pickMainClass(List<String> mainClasses, MavenProject project) {

		// the.group.id.Main
		String byGroupId = project.getGroupId() + ".Main";
		if (mainClasses.contains(byGroupId)) return byGroupId;

		List<String> namedMain = U.list();
		List<String> withGroupIdPkg = U.list();

		for (String name : mainClasses) {
			if (name.equals("Main")) return "Main";

			if (name.endsWith(".Main")) {
				namedMain.add(name);
			}

			if (name.startsWith(project.getGroupId() + ".")) {
				withGroupIdPkg.add(name);
			}
		}

		// the.group.id.foo.bar.Main
		getLog().info("Candidates by group ID: " + withGroupIdPkg);
		if (withGroupIdPkg.size() == 1) return U.single(withGroupIdPkg);

		// foo.bar.Main
		getLog().info("Candidates named Main: " + namedMain);
		if (namedMain.size() == 1) return U.single(namedMain);

		namedMain.retainAll(withGroupIdPkg);
		getLog().info("Candidates by group ID - named Main: " + namedMain);
		if (namedMain.size() == 1) return U.single(namedMain);

		// the.group.id.foo.bar.Main (the shortest name)
		Collections.sort(withGroupIdPkg, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.length() - s2.length();
			}
		});
		getLog().info("Candidates by group ID - picking one with the shortest name: " + withGroupIdPkg);

		return U.first(withGroupIdPkg);
	}

	private static void scanForMainClass(String path, Collection<String> mainClasses) {
		mainClasses.addAll(Scan.classpath(path).bytecodeFilter(new Predicate<InputStream>() {
			@Override
			public boolean eval(InputStream input) throws Exception {
				return hasMainMethod(new ClassFile(new DataInputStream(input)));
			}
		}).getAll());
	}

	private static boolean hasMainMethod(ClassFile cls) {
		int flags = cls.getAccessFlags();

		if (Modifier.isInterface(flags)
			|| Modifier.isAnnotation(flags)
			|| Modifier.isEnum(flags)) return false;

		for (Object m : cls.getMethods()) {
			if (m instanceof MethodInfo) {
				if (isMainMethod((MethodInfo) m)) return true;
			}
		}

		return false;
	}

	private static boolean isMainMethod(MethodInfo method) {
		int flags = method.getAccessFlags();

		return method.getName().equals("main")
			&& Modifier.isPublic(flags)
			&& Modifier.isStatic(flags)
			&& U.eq(method.getDescriptor(), "([Ljava/lang/String;)V"); // TODO find more elegant solution
	}

}
