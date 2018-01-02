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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Authors("Nikolche Mihajlovski")
@Since("5.5.1")
public class JobExecutor extends RapidoidThing implements Closeable {

	private static final Config CONFIG = Conf.JOBS.sub("executor");

	private final ThreadPoolExecutor executor;

	public JobExecutor() {
		this.executor = newExecutor();
		new ManageableExecutor("executor", executor);
		Jobs.init();
	}

	private static ThreadPoolExecutor newExecutor() {
		int threads = CONFIG.entry("threads").or(64);
		int maxThreads = CONFIG.entry("maxThreads").or(1024);
		int maxQueueSize = CONFIG.entry("maxQueueSize").or(1000000);

		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(maxQueueSize);

		return new ThreadPoolExecutor(threads, maxThreads, 300, TimeUnit.SECONDS, queue, new RapidoidThreadFactory("executor", true));
	}

	@Override
	public void close() {
		executor.shutdown();
		Jobs.awaitTermination(executor);
	}

	public ThreadPoolExecutor executor() {
		return executor;
	}
}
