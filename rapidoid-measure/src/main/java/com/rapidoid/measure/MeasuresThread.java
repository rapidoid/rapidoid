package com.rapidoid.measure;

/*
 * #%L
 * rapidoid-measure
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

import org.rapidoid.util.U;

public class MeasuresThread extends Thread {

	private final Measures statistics;

	private String lastInfo = "";

	public MeasuresThread(Measures statistics) {
		this.statistics = statistics;
	}

	@Override
	public void run() {
		try {
			while (true) {
				String info = statistics.info();
				if (!lastInfo.equals(info)) {
					printStats(info);
					lastInfo = info;
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			U.print("Stats EXCEPTION!");
			e.printStackTrace();
		}
	}

	private void printStats(String measured) {
		Runtime rt = Runtime.getRuntime();
		long totalMem = rt.totalMemory();
		long maxMem = rt.maxMemory();
		long freeMem = rt.freeMemory();
		long usedMem = totalMem - freeMem;
		int megs = 1024 * 1024;

		String gcinfo = "";
		List<GarbageCollectorMXBean> gcs = ManagementFactory.getGarbageCollectorMXBeans();
		for (GarbageCollectorMXBean gc : gcs) {
			gcinfo += " | " + gc.getName() + " x" + gc.getCollectionCount() + ":" + gc.getCollectionTime() + "ms";
		}

		String msg = "%s | total: %s MB, used: %s, free: %s MB, max: %s MB | %s";
		String info = String.format(msg, measured, totalMem / megs, usedMem / megs, freeMem / megs, maxMem / megs,
				gcinfo);
		U.print(info);
	}
}
