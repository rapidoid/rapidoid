package org.rapidoid.activity;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.util.concurrent.CancellationException;

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
public abstract class AbstractLoopThread extends RapidoidThread {

	private volatile long sleepMs = 5;

	public AbstractLoopThread() {
	}

	public AbstractLoopThread(String name) {
		super(name);
	}

	public AbstractLoopThread(long sleepMs) {
		this.sleepMs = sleepMs;
	}

	@Override
	public final void run() {
		while (!Thread.interrupted()) {
			try {

				loop();

				if (sleepMs > 0) U.sleep(sleepMs);

			} catch (ThreadDeath e) {
				Log.error("Received ThreadDeath error, terminating!", e);
				return;

			} catch (CancellationException e) {
				Log.info("The thread was interrupted.");
				return;

			} catch (Throwable e) {
				Log.error("Exception occurred inside the thread loop!", e);
			}
		}
	}

	protected abstract void loop() throws Exception;

}
