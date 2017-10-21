package org.rapidoid.job;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.util.concurrent.CancellationException;
import java.util.concurrent.RejectedExecutionException;

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
@Since("4.1.0")
public class ContextPreservingJobWrapper extends RapidoidThing implements Runnable {

	private final Runnable job;

	private final Ctx ctx;

	private volatile boolean done;

	public ContextPreservingJobWrapper(Runnable job, Ctx ctx) {
		this.job = job;
		this.ctx = ctx;
	}

	@Override
	public void run() {
		try {

			U.must(!Ctxs.hasContext(), "Detected context leak!");

			try {
				if (ctx != null) {
					// U.must(ctx.app() != null, "Application wasn't attached to the context: %s", ctx);
					Ctxs.attach(ctx);
				} else {
					Ctxs.open("job");
					Log.debug("Opening new context");
				}

			} catch (CancellationException e) {
				Log.warn("Job context initialization was canceled!");
				return;

			} catch (Throwable e) {
				Jobs.errorCounter().incrementAndGet();
				Log.error("Job context initialization failed!", e);
				throw U.rte("Job context initialization failed!", e);
			}

			try {
				job.run();

			} catch (CancellationException e) {
				Log.warn("Job execution was canceled!");
				return;

			} catch (RejectedExecutionException e) {
				Log.warn("Job execution was rejected!");
				return;

			} catch (Throwable e) {
				Jobs.errorCounter().incrementAndGet();
				Log.error("Job execution failed!", e);
				throw U.rte("Job execution failed!", e);

			} finally {
				Ctxs.close();
			}

		} finally {
			done = true;
		}
	}

	public boolean isDone() {
		return done;
	}

}
