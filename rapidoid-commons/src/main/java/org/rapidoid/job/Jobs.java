package org.rapidoid.job;

import org.rapidoid.RapidoidThing;
import org.rapidoid.activity.RapidoidThreadFactory;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.WithContext;
import org.rapidoid.u.U;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Jobs extends RapidoidThing {

	public static final Config JOBS = Conf.JOBS;

	private static final AtomicLong errorCounter = new AtomicLong();

	static {
		RapidoidInitializer.initialize();
	}

	private static ScheduledExecutorService SCHEDULER;

	private static Executor EXECUTOR;

	private Jobs() {
	}

	public static synchronized ScheduledExecutorService scheduler() {
		if (SCHEDULER == null) {
			int threads = JOBS.sub("scheduler").entry("threads").or(64);
			SCHEDULER = Executors.newScheduledThreadPool(threads, new RapidoidThreadFactory("scheduler"));
		}

		return SCHEDULER;
	}

	public static synchronized Executor executor() {
		if (EXECUTOR == null) {
			int threads = JOBS.sub("executor").entry("threads").or(64);
			EXECUTOR = Executors.newFixedThreadPool(threads, new RapidoidThreadFactory("executor"));
		}

		return EXECUTOR;
	}

	public static ScheduledFuture<?> schedule(Runnable job, long delay, TimeUnit unit) {
		return scheduler().schedule(wrap(job), delay, unit);
	}

	public static <T> ScheduledFuture<?> schedule(Callable<T> job, long delay, TimeUnit unit, Callback<T> callback) {
		return schedule(callbackJob(job, callback), delay, unit);
	}

	public static ScheduledFuture<?> scheduleAtFixedRate(Runnable job, long initialDelay, long period, TimeUnit unit) {
		return scheduler().scheduleAtFixedRate(wrap(job), initialDelay, period, unit);
	}

	public static <T> ScheduledFuture<?> scheduleAtFixedRate(Callable<T> job, long initialDelay, long period,
	                                                         TimeUnit unit, Callback<T> callback) {
		return scheduleAtFixedRate(callbackJob(job, callback), initialDelay, period, unit);
	}

	public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable job, long initialDelay, long delay, TimeUnit unit) {
		return scheduler().scheduleWithFixedDelay(wrap(job), initialDelay, delay, unit);
	}

	public static <T> ScheduledFuture<?> scheduleWithFixedDelay(Callable<T> job, long initialDelay, long delay,
	                                                            TimeUnit unit, Callback<T> callback) {
		return scheduleWithFixedDelay(callbackJob(job, callback), initialDelay, delay, unit);
	}

	public static void execute(Runnable job) {
		ContextPreservingJobWrapper jobWrapper = wrap(job);
		executor().execute(jobWrapper);
	}

	public static void executeAndWait(Runnable job) {
		ContextPreservingJobWrapper jobWrapper = wrap(job);
		executor().execute(jobWrapper);
		while (!jobWrapper.isDone()) {
			U.sleep(10);
		}
	}

	public static <T> void execute(Callable<T> job, Callback<T> callback) {
		execute(callbackJob(job, callback));
	}

	public static ContextPreservingJobWrapper wrap(Runnable job) {
		Ctx ctx = Ctxs.get();

		if (ctx != null) {
			// increment reference counter
			ctx = ctx.span(); // currently the same ctx is returned
		}

		return new ContextPreservingJobWrapper(job, ctx);
	}

	public static <T> void call(Callback<T> callback, T result, Throwable error) {
		Jobs.execute(new CallbackExecutorJob<T>(callback, result, error));
	}

	private static <T> Runnable callbackJob(final Callable<T> job, final Callback<T> callback) {
		return new Runnable() {
			@Override
			public void run() {
				T result;

				try {
					result = job.call();
				} catch (Throwable e) {
					call(callback, null, e);
					return;
				}

				call(callback, result, null);
			}
		};
	}

	public static void executeInContext(WithContext context, Runnable action) {
		executor().execute(new PredefinedContextJobWrapper(context, action));
	}

	public static JobsDSL after(long delay, TimeUnit unit) {
		return new JobsDSL(delay, -1, unit);
	}

	public static JobsDSL every(long period, TimeUnit unit) {
		return new JobsDSL(-1, period, unit);
	}

	public static AtomicLong errorCounter() {
		return errorCounter;
	}

}
