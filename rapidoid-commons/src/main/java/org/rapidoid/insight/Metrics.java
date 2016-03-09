package org.rapidoid.insight;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.TimeSeries;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Metrics implements Runnable {

	public static final TimeSeries SYSTEM_CPU = new TimeSeries();
	public static final TimeSeries PROCESS_CPU = new TimeSeries();

	public static final TimeSeries MEM_USED = new TimeSeries();
	public static final TimeSeries MEM_TOTAL = new TimeSeries();

	private static volatile OperatingSystemMXBean os;
	private static volatile Method sysCpuM;
	private static volatile Method procCpuM;

	static {
		Log.info("Initializing metrics");

		os = ManagementFactory.getOperatingSystemMXBean();
		sysCpuM = Cls.findMethod(os.getClass(), "getSystemCpuLoad");
		procCpuM = Cls.findMethod(os.getClass(), "getProcessCpuLoad");

		Jobs.every(1, TimeUnit.SECONDS).run(new Metrics());
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
			SYSTEM_CPU.put(U.time(), (Double) Cls.invoke(sysCpuM, os));
		}

		if (procCpuM != null) {
			PROCESS_CPU.put(U.time(), (Double) Cls.invoke(procCpuM, os));
		}
	}

}
