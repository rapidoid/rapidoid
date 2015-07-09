package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.log.Log;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class ContextPreservingJob implements Runnable {

	private final Runnable job;

	private final Ctx ctx;

	public ContextPreservingJob(Runnable job, Ctx ctx) {
		this.job = job;
		this.ctx = ctx;
	}

	@Override
	public void run() {
		if (ctx != null) {
			Ctxs.attach(ctx);
		} else {
			Ctxs.open();
		}

		try {
			job.run();
		} catch (Throwable e) {
			Log.error("Job execution failed!", e);
			throw U.rte("Job execution failed!", e);
		} finally {
			Ctxs.close();
		}
	}

}
