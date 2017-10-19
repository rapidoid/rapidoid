package org.rapidoid.ctx;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.log.Log;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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
public class Ctx extends RapidoidThing implements CtxMetadata {

	private static final AtomicLong ID_COUNTER = new AtomicLong();

	private final long id = ID_COUNTER.incrementAndGet();

	private final String tag;

	private volatile int referenceCounter = 1;

	private volatile UserInfo user;

	private volatile Object exchange;

	private volatile boolean closed = false;

	private volatile ThreadLocal<Object> persisters = new ThreadLocal<Object>();

	private final List<Object> persistersToClose = Collections.synchronizedList(new LinkedList<Object>());

	private final Map<Object, Object> extras = Coll.synchronizedMap();

	Ctx(String tag) {
		this.tag = tag;
	}

	public UserInfo user() {
		ensureNotClosed();
		return user;
	}

	public void setUser(UserInfo user) {
		ensureNotClosed();
		this.user = user;
	}

	@SuppressWarnings("unchecked")
	public <T> T exchange() {
		ensureNotClosed();
		return (T) exchange;
	}

	public void setExchange(Object exchange) {
		ensureNotClosed();
		this.exchange = exchange;
	}

	@SuppressWarnings("unchecked")
	public synchronized <P> P persister() {
		ensureNotClosed();

		Object persister = this.persisters.get();

		if (persister == null) {
			persister = Ctxs.createPersister(this);
			this.persisters.set(persister);
			persistersToClose.add(persister);
		}

		return (P) persister;
	}

	public synchronized void setPersister(Object persister) {
		this.persisters.set(persister);
	}

	public synchronized Ctx span() {
		ensureNotClosed();
		referenceCounter++;
		Log.debug("Spanning context", "ctx", this);
		return this;
	}

	synchronized void close() {
		ensureNotClosed();

		Log.debug("Closing context", "ctx", this);

		referenceCounter--;

		if (referenceCounter == 0) {
			clear();

		} else if (referenceCounter < 0) {
			throw new IllegalStateException("Reference counter < 0 for context: " + this);
		}
	}

	private synchronized void clear() {
		ensureNotClosed();

		Log.debug("Clearing context", "ctx", this);

		this.referenceCounter = 0;
		this.user = null;
		this.exchange = null;
		this.persisters = null;

		for (Object persister : persistersToClose) {
			Ctxs.closePersister(this, persister);
		}

		persistersToClose.clear();
		extras.clear();

		closed = true;
	}

	private void ensureNotClosed() {
		if (closed) {
			throw new RuntimeException("The context is closed!");
		}
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return prefixed("Ctx [id=" + id + ", tag=" + tag + ", user=" + user + ", exchange=" + exchange
			+ ", referenceCounter=" + referenceCounter + ", closed=" + closed
			+ ", persistersToClose=" + toString(persistersToClose, maxLen) + ", extras="
			+ toString(extras.entrySet(), maxLen) + "]");
	}

	private String prefixed(String asStr) {
		String isClosed = closed ? " <CLOSED>" : "";
		String prefix = "Ctx#" + id + ":" + tag + isClosed;
		return prefix + " " + asStr;
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	public boolean isClosed() {
		return closed;
	}

	public Map<Object, Object> extras() {
		ensureNotClosed();
		return extras;
	}

	public String tag() {
		return tag;
	}

}
