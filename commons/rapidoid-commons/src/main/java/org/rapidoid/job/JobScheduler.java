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

import org.rapidoid.RapidoidThing;
import org.rapidoid.activity.RapidoidThreadFactory;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;

import java.io.Closeable;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Authors("Nikolche Mihajlovski")
@Since("5.5.1")
public class JobScheduler extends RapidoidThing implements Closeable {

	private static final Config CONFIG = Conf.JOBS.sub("scheduler");

	private final ScheduledThreadPoolExecutor scheduler;

	public JobScheduler() {
		this.scheduler = newScheduler();
		new ManageableExecutor("scheduler", scheduler);
		Jobs.init();
	}

	private static ScheduledThreadPoolExecutor newScheduler() {
		int threads = CONFIG.entry("threads").or(64);

		return new ScheduledThreadPoolExecutor(threads, new RapidoidThreadFactory("scheduler", true));
	}

	@Override
	public void close() {
		scheduler.shutdown();
		Jobs.awaitTermination(scheduler);
	}

	public ScheduledThreadPoolExecutor scheduler() {
		return scheduler;
	}
}
