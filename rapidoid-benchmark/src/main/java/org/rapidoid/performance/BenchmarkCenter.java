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

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class BenchmarkCenter extends RapidoidThing {

	public static void main(String[] args) {
		BenchmarkForker forker = new BenchmarkForker(args);
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

		forker.benchmark(org.rapidoid.benchmark.lowlevel.Main.class, "/plaintext");
		forker.benchmark(org.rapidoid.benchmark.lowlevel.Main.class, "/json");

		forker.benchmark(org.rapidoid.benchmark.highlevel.Main.class, "/plaintext");
		forker.benchmark(org.rapidoid.benchmark.highlevel.Main.class, "/json");
	}

}
