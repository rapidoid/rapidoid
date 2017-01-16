package org.rapidoid.activity;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;

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
@Since("2.0.0")
public abstract class AbstractThreadActivity<T> extends LifecycleActivity<T> implements Runnable {

	protected final Thread thread;

	public AbstractThreadActivity(String name) {
		super(name);

		this.thread = new RapidoidThread(this, name);
	}

	@Override
	public T start() {
		checkActive(false);
		thread.start();
		return super.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	public T halt() {
		checkActive(true);
		thread.stop();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// do nothing
		}
		return super.halt();
	}

	@Override
	public T shutdown() {
		checkActive(true);
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// do nothing
		}
		return super.shutdown();
	}

	@Override
	public final void run() {
		Log.info("Starting activity thread", "name", name);

		try {
			while (!Thread.interrupted()) {
				try {
					loop();
				} catch (CancellationException e) {
					Log.info("Interrupted activity thread", "name", name);
					return;
				} catch (Exception e) {
					onError(e);
				}
			}

		} catch (ThreadDeath e) {
			Log.info("Halted activity thread", "name", name);
			return;
		}

		Log.info("Finished activity thread", "name", name);
	}

	protected void onError(Exception e) {
		Log.error("Activity error!", "activity", name, "error", e);
	}

	protected abstract void loop();

}
