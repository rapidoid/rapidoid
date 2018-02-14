/*-
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.job;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.RapidoidInitializer;
import org.rapidoid.optional.Opt;
import org.rapidoid.util.LazyInit;
import org.rapidoid.util.Once;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Authors("Nikolche Mihajlovski")
@Since("5.5.1")
public class JobsService extends RapidoidInitializer {

	private final Once init = new Once();

	private final AtomicLong errorCounter = new AtomicLong();

	private final LazyInit<JobScheduler> scheduler = new LazyInit<>(JobScheduler.class);

	private final LazyInit<JobExecutor> executor = new LazyInit<>(JobExecutor.class);

	private final AtomicBoolean active = new AtomicBoolean(true);

	public synchronized void reset() {
		errorCounter.set(0);
		executor.resetAndClose();
		scheduler.resetAndClose();
		active.set(true);
	}

	void init() {
		active.set(true); // activate

		if (init.go()) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					shutdownNow();
				}
			});
		}
	}

	public boolean isActive() {
		return active.get();
	}

	public Opt<ThreadPoolExecutor> executor() {
		return Opt.maybe(isActive() ? executor.get().executor() : null);
	}

	public Opt<ScheduledThreadPoolExecutor> scheduler() {
		return Opt.maybe(isActive() ? scheduler.get().scheduler() : null);
	}

	public AtomicLong errorCounter() {
		return errorCounter;
	}

	public synchronized void shutdown() {
		active.set(false);

		if (executor.isInitialized()) {
			Opt<ThreadPoolExecutor> exe = executor();
			if (exe.exists()) {
				exe.get().shutdown();
				Jobs.awaitTermination(exe.get());
			}
		}

		if (scheduler.isInitialized()) {
			Opt<ScheduledThreadPoolExecutor> sch = scheduler();
			if (sch.exists()) {
				sch.get().shutdown();
				Jobs.awaitTermination(sch.get());
			}
		}
	}

	public synchronized void shutdownNow() {
		active.set(false);

		if (executor.isInitialized()) {
			Opt<ThreadPoolExecutor> exe = executor();
			if (exe.exists()) {
				exe.get().shutdownNow();
				Jobs.awaitTermination(exe.get());
			}
		}

		if (scheduler.isInitialized()) {
			Opt<ScheduledThreadPoolExecutor> sch = scheduler();
			if (sch.exists()) {
				sch.get().shutdownNow();
				Jobs.awaitTermination(sch.get());
			}
		}
	}

}
