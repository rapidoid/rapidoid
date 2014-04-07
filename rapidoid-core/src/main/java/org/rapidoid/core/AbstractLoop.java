package org.rapidoid.core;

/*
 * #%L
 * rapidoid-core
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.util.U;

public abstract class AbstractLoop implements Runnable {

	protected final String name;

	protected Thread ownerThread;

	protected volatile LoopStatus status = LoopStatus.INIT;

	public AbstractLoop(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		this.ownerThread = Thread.currentThread();

		U.info("Starting event loop", "name", name);

		setStatus(LoopStatus.BEFORE_LOOP);

		try {
			beforeLoop();
		} catch (Throwable e) {
			U.severe("Error occured before loop is started", "name", name, "error", e);
			return;
		}

		setStatus(LoopStatus.LOOP);

		while (status == LoopStatus.LOOP) {
			if (Thread.currentThread().isInterrupted()) {
				break;
			}

			try {
				insideLoop();
			} catch (Throwable e) {
				U.severe("Event loop exception in " + name, e);
			}
		}

		setStatus(LoopStatus.AFTER_LOOP);

		afterLoop();

		setStatus(LoopStatus.STOPPED);

		U.info("Stopped event loop", "name", name);
	}

	private void setStatus(LoopStatus status) {
		this.status = status;
	}

	protected synchronized void stopLoop() {
		U.info("Stopping event loop", "name", name);

		while (status == LoopStatus.INIT || status == LoopStatus.BEFORE_LOOP) {
			try {
				Thread.sleep(100);
				U.info("Waiting for event loop to initialize...", "name", name);
			} catch (InterruptedException e) {
				// ignore it, stopping anyway
			}
		}

		U.info("Stopped event loop", "name", name);

		if (status == LoopStatus.LOOP) {
			status = LoopStatus.STOPPED;
		}
	}

	protected void beforeLoop() {

	}

	protected abstract void insideLoop();

	protected void afterLoop() {

	}

	protected void assertStatus(LoopStatus expected) {
		if (status != expected) {
			throw new IllegalStateException("Expected status=" + expected + " for event loop: " + name);
		}
	}

	protected boolean onSameThread() {
		return ownerThread == Thread.currentThread();
	}

	protected void checkOnSameThread() {
		if (!onSameThread()) {
			throw U.rte("Not on the owner thread, expected %s, but found: %s", ownerThread, Thread.currentThread());
		}
	}

}
