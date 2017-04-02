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
import org.rapidoid.setup.App;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class BenchmarkCenter extends RapidoidThing {

	public static void main(String[] args) {
//		args = new String[]{"benchmark.run=/fortunes", "benchmark.plan=t4:r3:d15:c256,512", "profiles=mysql", "hikari.maximumPoolSize=150", "jdbc.workers=16"};
		App.run(args);

		run();
	}

	public static void run() {
		BenchmarkForker forker = new BenchmarkForker();
		forker.clear();

		if (forker.hasTarget()) {
			forker.benchmark();

		} else {
			// run built-in benchmarks if no target is specified:
			runBuiltInBenchmarks(forker);
		}

		forker.printResults();
	}

	private static void runBuiltInBenchmarks(BenchmarkForker forker) {

		String run = Conf.BENCHMARK.entry("run").or("/plaintext,/json,/fortunes,/fortunes/multi,http-fast");

		for (String demo : run.split(",")) {
			demo = demo.trim();
			if (!demo.isEmpty()) {
				if (demo.equals("http-fast")) {
					forker.benchmark(org.rapidoid.benchmark.lowlevel.Main.class, "/plaintext");
					forker.benchmark(org.rapidoid.benchmark.lowlevel.Main.class, "/json");
				} else {
					forker.benchmark(org.rapidoid.benchmark.highlevel.Main.class, demo);
				}
			}
		}
	}

}
