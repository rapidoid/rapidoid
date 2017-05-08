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
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.setup.App;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Arrays;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class BenchmarkRunner extends RapidoidThing {

	public static void main(String[] args) {
		Msc.setPlatform(true);
		App.run(args);

		String mainClass = Conf.BENCHMARK.entry("main").str().getOrNull();
		String uri = Conf.BENCHMARK.entry("target").str().getOrNull();
		boolean passive = Conf.BENCHMARK.entry("passive").or(false);

		String desc = mainClass + " " + uri + (passive ? " [PASSIVE]" : "");

		// start the app that will be benchmarked
		Msc.invokeMain(Cls.get(mainClass), args);

		if (!passive) {

			String plan = Conf.BENCHMARK.entry("plan").str().getOrNull();
			String filename = Conf.BENCHMARK.entry("file").str().getOrNull();
			String url = Conf.BENCHMARK.entry("target").str().getOrNull();

			benchmark(desc, plan, filename, url);

			System.exit(0);
		}
	}

	public static void benchmark(String desc, String plan, String filename, String url) {

		WrkSetup wrk = Perf.wrk()
			.url(url);

		for (String part : plan.split(":")) {
			part = part.toLowerCase();

			char flag = part.charAt(0);
			String val = part.substring(1);

			switch (flag) {
				case 't':
					wrk.threads(U.num(val));
					break;

				case 'r':
					wrk.rounds(U.num(val));
					break;

				case 'd':
					int duration = U.num(val);
					wrk.duration(duration);
					if (wrk.warmUp() < 0) wrk.warmUp(duration);
					wrk.pause(duration);
					break;

				case 'c':
					wrk.connections(Arrays.stream(val.split(",")).mapToInt(U::num).toArray());
					break;

				case 'p':
					wrk.pipeline(U.num(val));
					break;

				case 's':
					wrk.showDetails(false).showWarmUpDetails(false);
					break;

				case 'w':
					wrk.warmUp(U.num(val));
					break;

				default:
					throw U.rte("Unknown benchmark option: " + flag);
			}
		}

		Log.info("Running benchmark", "setup", wrk);

		BenchmarkResults results = wrk.run();

		String info = U.frmt("%s => %s", desc, results.bestThroughput());
		U.print(info);

		IO.append(filename, (info + "\n").getBytes());
	}

}
