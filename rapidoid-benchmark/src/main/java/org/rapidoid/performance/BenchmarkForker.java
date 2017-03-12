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
import org.rapidoid.io.IO;
import org.rapidoid.process.Proc;
import org.rapidoid.process.ProcessHandle;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;

import java.io.File;
import java.io.IOException;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class BenchmarkForker extends RapidoidThing {

	private final String resultsFile;
	private final String plan;
	private final String target;

	public BenchmarkForker(String[] args) {
		try {
			this.resultsFile = File.createTempFile("benchmark", ".txt").getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't create temporary file!", e);
		}

		this.plan = args.length > 0 ? args[0] : "2x3";
		this.target = args.length > 1 ? args[1] : null;
	}

	public void clear() {
		IO.save(resultsFile, "BENCHMARK plan: " + plan + "\n");
	}

	public ProcessHandle benchmark(Class<?> mainClass, String uri) {

		String classpath = U.join(":", ClasspathUtil.getClasspath());
		String runner = BenchmarkRunner.class.getCanonicalName();
		String main = mainClass.getCanonicalName();

		ProcessHandle proc = Proc.printingOutput(true)
			.linePrefix("[BENCHMARK] ")
			.run("java", "-Dfile.encoding=UTF-8", "-classpath", classpath, runner, main, uri, resultsFile, plan)
			.waitFor();

		return proc;
	}

	public void printResults() {
		U.print("");
		U.print(IO.load(resultsFile));
	}

	public boolean hasTarget() {
		return target != null;
	}

	public void benchmark() {
		BenchmarkRunner.benchmark(target, target, resultsFile, plan);
	}
}
