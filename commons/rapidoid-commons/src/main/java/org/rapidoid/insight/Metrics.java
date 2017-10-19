package org.rapidoid.insight;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.collection.Coll;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.job.Jobs;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.log.Log;
import org.rapidoid.timeseries.TimeSeries;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.Once;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/*
 * #%L
 * rapidoid-commons
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Metrics extends RapidoidInitializer implements Runnable {

	public static final Map<String, TimeSeries> METRICS = Coll.synchronizedMap();

	public static final TimeSeries SYSTEM_CPU = new TimeSeries().title("System CPU");
	public static final TimeSeries PROCESS_CPU = new TimeSeries().title("Process CPU");

	public static final TimeSeries MEM_USED = new TimeSeries().title("Used JVM memory (MB)");
	public static final TimeSeries MEM_TOTAL = new TimeSeries().title("Total JVM memory (MB)");

	public static final TimeSeries NUM_THREADS = new TimeSeries().title("Number of JVM threads");
	public static final TimeSeries NUM_FILE_DESC = new TimeSeries().title("Open files and connections");

	private static volatile OperatingSystemMXBean os;
	private static volatile Method sysCpuM;
	private static volatile Method procCpuM;
	private static volatile Method openFileDescriptorCount;

	private static volatile ThreadMXBean threads;

	private static final Once once = new Once();

	public static void bootstrap() {
		if (!once.go()) return;

		Log.info("Bootstraping metrics");

		os = ManagementFactory.getOperatingSystemMXBean();
		sysCpuM = Cls.findMethod(os.getClass(), "getSystemCpuLoad");
		procCpuM = Cls.findMethod(os.getClass(), "getProcessCpuLoad");
		openFileDescriptorCount = Cls.findMethod(os.getClass(), "getOpenFileDescriptorCount");

		threads = ManagementFactory.getThreadMXBean();

		register("cpu/system", SYSTEM_CPU);
		register("cpu/process", PROCESS_CPU);

		register("mem/used", MEM_USED);
		register("mem/total", MEM_TOTAL);

		register("threads", NUM_THREADS);
		register("descriptors", NUM_FILE_DESC);

		// FIXME there are too many metrics
//		JMXMetrics.bootstrap();

		Metrics updateMetrics = new Metrics();
		updateMetrics.run();
		Jobs.scheduleAtFixedRate(updateMetrics, 1, 1, TimeUnit.SECONDS);
	}

	public static TimeSeries get(String uri) {
		return METRICS.get(uri);
	}

	public static TimeSeries register(String uri, TimeSeries metric) {
		return METRICS.put(uri, metric);
	}

	@Override
	public void run() {
		Runtime rt = Runtime.getRuntime();

		long totalMem = rt.totalMemory();
		long maxMem = rt.maxMemory();
		long freeMem = rt.freeMemory();
		long usedMem = totalMem - freeMem;

		double megs = 1024.0 * 1024;

		MEM_TOTAL.put(U.time(), totalMem / megs);
		MEM_USED.put(U.time(), usedMem / megs);

		if (sysCpuM != null) {
			SYSTEM_CPU.put(U.time(), ((Number) Cls.invoke(sysCpuM, os)).doubleValue());
		}

		if (procCpuM != null) {
			PROCESS_CPU.put(U.time(), ((Number) Cls.invoke(procCpuM, os)).doubleValue());
		}

		if (openFileDescriptorCount != null) {
			NUM_FILE_DESC.put(U.time(), ((Number) Cls.invoke(openFileDescriptorCount, os)).doubleValue());
		}

		NUM_THREADS.put(U.time(), threads.getThreadCount());
	}

	public static Map<String, TimeSeries> all() {
		return METRICS;
	}

	public static TimeSeries measure(String title, final Number var) {
		return measure(title, var, 1, TimeUnit.SECONDS);
	}

	public static TimeSeries measure(String title, final Number var, long period, TimeUnit timeUnit) {
		return measure(title, new Callable<Number>() {
			@Override
			public Number call() throws Exception {
				return var;
			}
		}, period, timeUnit);
	}

	public static TimeSeries measure(String title, Callable<? extends Number> var) {
		return measure(title, var, 1, TimeUnit.SECONDS);
	}

	public static TimeSeries measure(String title, final Callable<? extends Number> var, long period, TimeUnit timeUnit) {

		final TimeSeries ts = new TimeSeries();
		ts.title(title);

		Jobs.every(period, timeUnit).run(new Runnable() {
			@Override
			public void run() {
				Number value = Lmbd.call(var);
				if (value != null) ts.put(U.time(), value.doubleValue());
			}
		});

		register("/" + Msc.textToId(title), ts);

		return ts;
	}

}
