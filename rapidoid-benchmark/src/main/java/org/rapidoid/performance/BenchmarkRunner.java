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
import org.rapidoid.io.IO;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class BenchmarkRunner extends RapidoidThing {

	public static void main(String[] args) {
		String mainClass = args[0];
		String uri = args[1];
		String filename = args[2];
		String plan = args[3];

		Msc.invokeMain(Cls.get(mainClass), new String[0]);

		String desc = mainClass + " " + uri;
		benchmark(desc, uri, filename, plan);

		System.exit(0);
	}

	public static void benchmark(String desc, String url, String filename, String plan) {

		String[] parts = plan.split("x");
		int rounds = U.num(parts[0]);
		int duration = U.num(parts[1]);

		BenchmarkResults results = Perf.wrk()
			.url(url)
			.duration(duration)
			.warmUp(duration)
			.rounds(rounds)
			.pause(duration)
			.showDetails(false)
			.run();

		String info = U.frmt("%s => %s", desc, results.bestThroughput());
		U.print(info);

		IO.append(filename, (info + "\n").getBytes());
	}

}
