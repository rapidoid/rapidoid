/*-
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

package org.rapidoid.job;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.WithContext;
import org.rapidoid.log.Log;
import org.rapidoid.optional.Opt;
import org.rapidoid.u.U;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Jobs extends RapidoidInitializer {

	private static final JobsService jobs = new JobsService();

	private Jobs() {
	}

	public static synchronized void reset() {
		jobs.reset();
	}

	static void init() {
		jobs.init();
	}

	public static ThreadPoolExecutor executor() {
		return jobs.executor().get();
	}

	public static ScheduledThreadPoolExecutor scheduler() {
		return jobs.scheduler().get();
	}

	public static ScheduledFuture<?> schedule(Runnable job, long delay, TimeUnit unit) {
		return requireActiveScheduler().schedule(wrap(job), delay, unit);
	}

	public static <T> ScheduledFuture<?> schedule(Callable<T> job, long delay, TimeUnit unit, Callback<T> callback) {
		return schedule(callbackJob(job, callback), delay, unit);
	}

	public static ScheduledFuture<?> scheduleAtFixedRate(Runnable job, long initialDelay, long period, TimeUnit unit) {
		return requireActiveScheduler().scheduleAtFixedRate(wrap(job), initialDelay, period, unit);
	}

	public static <T> ScheduledFuture<?> scheduleAtFixedRate(Callable<T> job, long initialDelay, long period,
	                                                         TimeUnit unit, Callback<T> callback) {
		return scheduleAtFixedRate(callbackJob(job, callback), initialDelay, period, unit);
	}

	public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable job, long initialDelay, long delay, TimeUnit unit) {
		return requireActiveScheduler().scheduleWithFixedDelay(wrap(job), initialDelay, delay, unit);
	}

	public static <T> ScheduledFuture<?> scheduleWithFixedDelay(Callable<T> job, long initialDelay, long delay,
	                                                            TimeUnit unit, Callback<T> callback) {
		return scheduleWithFixedDelay(callbackJob(job, callback), initialDelay, delay, unit);
	}

	public static void execute(Runnable job) {
		Opt<ThreadPoolExecutor> executor = jobs.executor();

		if (executor.exists()) {
			ContextPreservingJobWrapper jobWrapper = wrap(job);

			try {
				executor.get().execute(jobWrapper);
			} catch (RejectedExecutionException e) {
				Log.warn("Job execution was rejected!", "job", job);
			}
		}
	}

	public static void executeAndWait(Runnable job) {
		Opt<ThreadPoolExecutor> executor = jobs.executor();

		if (executor.exists()) {
			ContextPreservingJobWrapper jobWrapper = wrap(job);

			try {
				executor.get().execute(jobWrapper);
			} catch (RejectedExecutionException e) {
				Log.warn("Job execution was rejected!", "job", job);
			}

			while (!jobWrapper.isDone()) {
				U.sleep(10);
			}
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
		Jobs.execute(new CallbackExecutorJob<>(callback, result, error));
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
		Opt<ThreadPoolExecutor> executor = jobs.executor();

		if (executor.exists()) {
			try {
				executor.get().execute(new PredefinedContextJobWrapper(context, action));
			} catch (RejectedExecutionException e) {
				Log.warn("The job was rejected by the executor/scheduler!", "context", context.tag());
			}
		}
	}

	public static JobsDelayDSL after(long delay) {
		return new JobsDelayDSL(delay);
	}

	public static JobsDSL after(long delay, TimeUnit unit) {
		return new JobsDSL(delay, -1, unit);
	}

	public static JobsDSL every(long period, TimeUnit unit) {
		return new JobsDSL(-1, period, unit);
	}

	public static AtomicLong errorCounter() {
		return jobs.errorCounter();
	}

	public static synchronized void shutdown() {
		jobs.shutdown();
	}

	public static synchronized void shutdownNow() {
		jobs.shutdownNow();
	}

	private static ScheduledThreadPoolExecutor requireActiveScheduler() {
		return jobs.scheduler().orFail("The scheduler is not active!");
	}

	static void awaitTermination(ThreadPoolExecutor threadPoolExecutor) {
		try {
			threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	private static void executeWithRetriesOnReject(Runnable action) {
		int attempt = 0;

		while (true) {
			attempt++;

			try {
				action.run();

			} catch (RejectedExecutionException e) {

				Log.warn("Job execution was rejected!", "attempt", attempt, "thread", Thread.currentThread().getName());

				// retry later
				try {
					Thread.sleep(1);
				} catch (InterruptedException e2) {
					return; // stop if interrupted
				}
			}
		}
	}

}
