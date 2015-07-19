package org.rapidoidx.net.impl;

/*
 * #%L
 * rapidoid-x-net
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.activity.LifecycleActivity;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public abstract class AbstractLoop<T> extends LifecycleActivity<T> implements Runnable {

	protected volatile Thread ownerThread;

	protected volatile LoopStatus status = LoopStatus.INIT;

	public AbstractLoop(String name) {
		super(name);
	}

	@Override
	public void run() {
		this.ownerThread = Thread.currentThread();

		Log.info("Starting event loop", "name", name);

		setStatus(LoopStatus.BEFORE_LOOP);

		try {
			beforeLoop();
		} catch (Throwable e) {
			Log.error("Error occured before loop is started", "name", name, "error", e);
			setStatus(LoopStatus.FAILED);
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
				Log.error("Event loop exception in " + name, e);
			}
		}

		setStatus(LoopStatus.AFTER_LOOP);

		afterLoop();

		setStatus(LoopStatus.STOPPED);

		Log.info("Stopped event loop", "name", name);
	}

	private void setStatus(LoopStatus status) {
		this.status = status;
	}

	protected synchronized void stopLoop() {
		Log.info("Stopping event loop", "name", name);

		while (status == LoopStatus.INIT || status == LoopStatus.BEFORE_LOOP) {
			try {
				Thread.sleep(100);
				Log.info("Waiting for event loop to initialize...", "name", name);
			} catch (InterruptedException e) {
				// ignore it, stopping anyway
			}
		}

		if (status == LoopStatus.LOOP) {
			status = LoopStatus.STOPPED;
		}

		Log.info("Stopped event loop", "name", name);
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

	@SuppressWarnings("unchecked")
	@Override
	public T start() {
		super.start();

		waitToStart();

		return (T) this;
	}

	public void waitToStart() {
		// wait for the event loop to activate
		while (status == LoopStatus.INIT || status == LoopStatus.BEFORE_LOOP) {
			U.sleep(50);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T shutdown() {
		super.shutdown();

		waitToStop();

		return (T) this;
	}

	public void waitToStop() {
		// wait for the event loop to stop
		while (status != LoopStatus.STOPPED && status != LoopStatus.FAILED) {
			U.sleep(50);
		}
	}

}
