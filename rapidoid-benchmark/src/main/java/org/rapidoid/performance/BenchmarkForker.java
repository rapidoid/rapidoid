package org.rapidoid.performance;

/*
 * #%L
 * rapidoid-benchmark
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
import org.rapidoid.config.Conf;
import org.rapidoid.env.Env;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.process.Proc;
import org.rapidoid.process.ProcessHandle;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class BenchmarkForker extends RapidoidThing {

	private final String resultsFile;
	private final String plan;
	private final String target;

	public BenchmarkForker() {
		try {
			this.resultsFile = File.createTempFile("benchmark", ".txt").getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't create temporary file!", e);
		}

		this.plan = Conf.BENCHMARK.entry("plan").str().getOrNull();
		this.target = Conf.BENCHMARK.entry("target").str().getOrNull();

		Log.info("!Starting benchmark", "!configuration", Conf.BENCHMARK.toMap());
	}

	public void clear() {
		IO.save(resultsFile, "BENCHMARK plan: " + plan + "\n");
	}

	public ProcessHandle benchmark(Class<?> mainClass, String uri) {

		String classpath = U.join(":", ClasspathUtil.getClasspath());
		String runner = BenchmarkRunner.class.getCanonicalName();
		String main = mainClass.getCanonicalName();

		List<String> cmdWithArgs = U.list("java", "-Xms1g", "-Xmx1g", "-Dfile.encoding=UTF-8", "-classpath", classpath, runner);

		cmdWithArgs.add("benchmark.target=" + uri);
		cmdWithArgs.add("benchmark.main=" + main);
		cmdWithArgs.add("benchmark.file=" + resultsFile);

		cmdWithArgs.addAll(Env.args());

		ProcessHandle proc = Proc.printingOutput(true)
			.linePrefix("[BENCHMARK] ")
			.run(U.arrayOf(cmdWithArgs))
			.waitFor();

		return proc;
	}

	void printResults() {
		U.print("");
		U.print(IO.load(resultsFile));
	}

	boolean hasTarget() {
		return target != null;
	}

	public void benchmark() {
		BenchmarkRunner.benchmark(target, plan, resultsFile, target);
	}

}
