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
import org.rapidoid.commons.Str;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.process.Proc;
import org.rapidoid.process.ProcessHandle;
import org.rapidoid.u.U;

import java.util.Arrays;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class WrkSetup extends RapidoidThing {

	private volatile String url = "/";

	private volatile int[] connections = {128};

	private volatile int duration = 5;

	private volatile int timeout = 5;

	private volatile int rounds = 1;

	private volatile int warmUp = -1;

	private volatile int pause = 1;

	private volatile int pipeline = 1;

	private volatile int threads;

	private volatile boolean showWarmUpDetails = true;

	private volatile boolean showDetails = true;

	public String url() {
		return url;
	}

	public WrkSetup url(String url) {
		this.url = url;
		return this;
	}

	public int[] connections() {
		return connections;
	}

	public WrkSetup connections(int[] connections) {
		this.connections = connections;
		return this;
	}

	public int duration() {
		return duration;
	}

	public WrkSetup duration(int duration) {
		this.duration = duration;
		return this;
	}

	public int pipeline() {
		return pipeline;
	}

	public WrkSetup pipeline(int pipeline) {
		this.pipeline = pipeline;
		return this;
	}

	public int timeout() {
		return timeout;
	}

	public WrkSetup timeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public int rounds() {
		return rounds;
	}

	public WrkSetup rounds(int rounds) {
		this.rounds = rounds;
		return this;
	}

	public int threads() {
		return threads;
	}

	public WrkSetup threads(int threads) {
		this.threads = threads;
		return this;
	}

	public int warmUp() {
		return warmUp;
	}

	public WrkSetup warmUp(int warmUp) {
		this.warmUp = warmUp;
		return this;
	}

	public boolean showWarmUpDetails() {
		return showWarmUpDetails;
	}

	public WrkSetup showWarmUpDetails(boolean showWarmUpDetails) {
		this.showWarmUpDetails = showWarmUpDetails;
		return this;
	}

	public boolean showDetails() {
		return showDetails;
	}

	public WrkSetup showDetails(boolean showDetails) {
		this.showDetails = showDetails;
		return this;
	}

	public int pause() {
		return pause;
	}

	public WrkSetup pause(int pause) {
		this.pause = pause;
		return this;
	}

	public BenchmarkResults run() {
		BenchmarkResults results = new BenchmarkResults();

		if (url.startsWith("/")) {
			url = "http://localhost:8080" + url;
		}

		if (warmUp > 0) {
			Log.info("Warming up...", "duration", warmUp);

			ProcessHandle warm = runWrk(connections[0]);
			if (showWarmUpDetails) warm.log(LogLevel.INFO);
		}

		for (int round = 1; round <= rounds; round++) {
			for (int conn : connections) {

				U.sleep(pause * 1000); // pause a bit

				Log.info("Running benchmark...", "round", round, "connections", conn, "duration", duration);

				ProcessHandle proc = runWrk(conn);

				if (showDetails) proc.log(LogLevel.INFO);

				processResults(results, proc.out(), proc.err());

				Log.info("!Benchmark result", "round", round, "connections", conn, "errors", results.errors, "!throughput", U.last(results.throughputs));
				Log.info("");
			}
		}

		Log.info("Aggregated benchmark results", "errors", results.errors, "throughputs", results.throughputs, "best", results.bestThroughput());

		return results;
	}

	private ProcessHandle runWrk(int conn) {

		List<String> args = U.list("wrk",
			"-t", (threads > 0 ? threads : Runtime.getRuntime().availableProcessors()) + "",
			"-c", conn + "",
			"-d", duration + "",
			"--timeout", timeout + "");

		if (pipeline > 1) {
			args.add("-s");
			args.add("/opt/pipeline.lua");
		}

		args.add(url);

		if (pipeline > 1) {
			args.add("--");
			args.add(pipeline + "");
		}

		return Proc.run(U.arrayOf(args)).waitFor();
	}

	private void processResults(BenchmarkResults results, List<String> out, List<String> err) {
		results.rounds++;
		if (!err.isEmpty()) {
			results.errors++;
			return;
		}

		for (String line : out) {
			if (line.startsWith("Requests/sec: ")) {
				String rps = Str.triml(line, "Requests/sec: ");

				double throughput = Double.parseDouble(rps);
				results.throughputs.add(throughput);
				return;
			}
		}

		Log.error("Couldn't parse the benchmark output!");
		results.errors++;
	}

	@Override
	public String toString() {
		return "WrkSetup{" +
			"url='" + url + '\'' +
			", connections=" + Arrays.toString(connections) +
			", duration=" + duration +
			", timeout=" + timeout +
			", rounds=" + rounds +
			", warmUp=" + warmUp +
			", pause=" + pause +
			", pipeline=" + pipeline +
			", threads=" + threads +
			", showWarmUpDetails=" + showWarmUpDetails +
			", showDetails=" + showDetails +
			'}';
	}
}
