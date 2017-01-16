package org.rapidoid.job;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.WithContext;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

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
@Since("5.1.0")
public class PredefinedContextJobWrapper extends RapidoidThing implements Runnable {

	private final Runnable job;
	private final WithContext context;

	public PredefinedContextJobWrapper(WithContext context, Runnable job) {
		this.context = context;
		this.job = job;
	}

	@Override
	public void run() {
		U.must(!Ctxs.hasContext(), "Detected context leak!");

		try {
			Ctxs.open(context);
		} catch (Throwable e) {
			Log.error("Job context initialization failed!", e);
			throw U.rte("Job context initialization failed!", e);
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
