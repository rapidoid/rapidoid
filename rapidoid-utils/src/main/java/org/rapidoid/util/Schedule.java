package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class Schedule implements Constants {

	private static ScheduledThreadPoolExecutor EXECUTOR;

	private Schedule() {}

	public static synchronized ScheduledThreadPoolExecutor executor() {
		if (EXECUTOR == null) {
			int threads = Conf.option("threads", 100);
			EXECUTOR = new ScheduledThreadPoolExecutor(threads);
		}

		return EXECUTOR;
	}

	public static synchronized ScheduledFuture<?> job(Runnable job, long delay) {
		return executor().schedule(job, delay, TimeUnit.MILLISECONDS);
	}

}
