package org.rapidoid.ctx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;

/*
 * #%L
 * rapidoid-ctx
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Ctx {

	private static final AtomicLong ID_COUNTER = new AtomicLong();

	private final long id = ID_COUNTER.incrementAndGet();

	private volatile UserInfo user;

	private volatile Object exchange;

	private volatile Object app;

	private volatile int referenceCounter = 1;

	private volatile ThreadLocal<Object> persisters = new ThreadLocal<Object>();

	private final List<Object> allPersisters = Collections.synchronizedList(new ArrayList<Object>(5));

	Ctx() {}

	public UserInfo user() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	@SuppressWarnings("unchecked")
	public <T> T exchange() {
		return (T) exchange;
	}

	public void setExchange(Object exchange) {
		this.exchange = exchange;
	}

	@SuppressWarnings("unchecked")
	public <T> T app() {
		return (T) app;
	}

	public void setApp(Object app) {
		this.app = app;
	}

	@SuppressWarnings("unchecked")
	public synchronized <P> P persister() {
		Object persister = this.persisters.get();

		if (persister == null) {
			persister = Ctxs.createPersister();
			this.persisters.set(persister);
			allPersisters.add(persister);
		}

		return (P) persister;
	}

	synchronized void span() {
		referenceCounter++;
		Log.debug("Spanning context", "ctx", this);
	}

	synchronized void close() {
		Log.debug("Closing context", "ctx", this);
		referenceCounter--;

		if (referenceCounter == 0) {
			clear();
			this.persisters = new ThreadLocal<Object>();

		} else if (referenceCounter < 0) {
			throw new IllegalStateException("Reference counter < 0 for context: " + this);
		}
	}

	private synchronized void clear() {
		Log.debug("Clearing context", "ctx", this);

		this.referenceCounter = 0;
		this.user = null;
		this.exchange = null;
		this.app = null;

		for (Object persister : allPersisters) {
			Ctxs.closePersister(persister);
		}

		allPersisters.clear();
	}

	@Override
	public synchronized String toString() {
		return "Ctx#" + id + "  [user=" + user + ", exchange=" + exchange + ", app=" + app + ", referenceCounter="
				+ referenceCounter + ", allPersisters=" + allPersisters.size() + "]";
	}

}
