package org.rapidoid.insight;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.lang.management.*;
import java.util.List;
import java.util.concurrent.Callable;

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
@Since("5.3.0")
public class JMXMetrics extends RapidoidThing {

	public static void bootstrap() {

		OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

		if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
			final com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) osBean;

			Metrics.measure("Total memory (MB)", new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					return os.getCommittedVirtualMemorySize() / 1024.0 / 1024.0;
				}
			});
		}

		List<GarbageCollectorMXBean> gcs = ManagementFactory.getGarbageCollectorMXBeans();

		for (final GarbageCollectorMXBean gc : gcs) {
			Metrics.measure(gc.getName() + " - GC count", new Callable<Long>() {
				@Override
				public Long call() throws Exception {
					return gc.getCollectionCount();
				}
			});

			Metrics.measure(gc.getName() + " - GC time", new Callable<Long>() {
				@Override
				public Long call() throws Exception {
					return gc.getCollectionTime();
				}
			});
		}

		final MemoryMXBean mem = ManagementFactory.getMemoryMXBean();

		Metrics.measure("Heap memory used (MB)", new Callable<Double>() {
			@Override
			public Double call() throws Exception {
				return mem.getHeapMemoryUsage().getUsed() / 1024.0 / 1024.0;
			}
		});

		Metrics.measure("Non-heap memory used (MB)", new Callable<Double>() {
			@Override
			public Double call() throws Exception {
				return mem.getNonHeapMemoryUsage().getUsed() / 1024.0 / 1024.0;
			}
		});

		Metrics.measure("Heap memory committed (MB)", new Callable<Double>() {
			@Override
			public Double call() throws Exception {
				return mem.getHeapMemoryUsage().getCommitted() / 1024.0 / 1024.0;
			}
		});

		Metrics.measure("Non-heap memory committed (MB)", new Callable<Double>() {
			@Override
			public Double call() throws Exception {
				return mem.getNonHeapMemoryUsage().getCommitted() / 1024.0 / 1024.0;
			}
		});

		Metrics.measure("Heap memory max (MB)", new Callable<Double>() {
			@Override
			public Double call() throws Exception {
				return mem.getHeapMemoryUsage().getMax() / 1024.0 / 1024.0;
			}
		});

		Metrics.measure("Non-heap memory max (MB)", new Callable<Double>() {
			@Override
			public Double call() throws Exception {
				return mem.getNonHeapMemoryUsage().getMax() / 1024.0 / 1024.0;
			}
		});

		memDetails();
	}

	public static void memDetails() {
		List<MemoryPoolMXBean> mems = ManagementFactory.getMemoryPoolMXBeans();

		for (final MemoryPoolMXBean mx : mems) {
			Metrics.measure(mx.getName() + " (MB)", new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					return mx.getUsage().getUsed() / 1024.0 / 1024.0;
				}
			});
		}
	}

}
