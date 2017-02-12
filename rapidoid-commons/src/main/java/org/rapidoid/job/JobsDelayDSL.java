package org.rapidoid.job;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
public class JobsDelayDSL extends RapidoidThing {

	private final long delay;

	public JobsDelayDSL(long delay) {
		this.delay = delay;
	}

	public ScheduledFuture<Void> nanoseconds(Runnable action) {
		return Jobs.after(delay, TimeUnit.NANOSECONDS).run(action);
	}

	public ScheduledFuture<Void> microseconds(Runnable action) {
		return Jobs.after(delay, TimeUnit.MICROSECONDS).run(action);
	}

	public ScheduledFuture<Void> milliseconds(Runnable action) {
		return Jobs.after(delay, TimeUnit.MILLISECONDS).run(action);
	}

	public ScheduledFuture<Void> seconds(Runnable action) {
		return Jobs.after(delay, TimeUnit.SECONDS).run(action);
	}

	public ScheduledFuture<Void> minutes(Runnable action) {
		return Jobs.after(delay, TimeUnit.MINUTES).run(action);
	}

	public ScheduledFuture<Void> hours(Runnable action) {
		return Jobs.after(delay, TimeUnit.HOURS).run(action);
	}

	public ScheduledFuture<Void> days(Runnable action) {
		return Jobs.after(delay, TimeUnit.DAYS).run(action);
	}

}
