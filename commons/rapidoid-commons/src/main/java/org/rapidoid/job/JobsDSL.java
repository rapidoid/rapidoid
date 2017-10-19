package org.rapidoid.job;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;

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
@Since("5.1.0")
public class JobsDSL extends RapidoidThing {

	private final long after;
	private final long every;
	private final TimeUnit timeUnit;

	public JobsDSL(long after, long every, TimeUnit timeUnit) {
		this.after = after;
		this.every = every;
		this.timeUnit = timeUnit;
	}

	public ScheduledFuture<Void> run(Runnable action) {
		if (after >= 0) {
			return (ScheduledFuture<Void>) Jobs.schedule(action, after, timeUnit);
		} else if (every >= 0) {
			return (ScheduledFuture<Void>) Jobs.scheduleAtFixedRate(action, 0, every, timeUnit);
		} else {
			throw Err.notExpected();
		}
	}

}
