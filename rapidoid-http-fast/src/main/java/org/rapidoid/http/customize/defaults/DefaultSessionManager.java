package org.rapidoid.http.customize.defaults;

/*
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.event.Events;
import org.rapidoid.event.Fire;
import org.rapidoid.http.Req;
import org.rapidoid.http.customize.SessionManager;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class DefaultSessionManager extends RapidoidThing implements SessionManager {

	public static class SessionHolder {
		volatile byte[] serialized;
		volatile Map<String, Serializable> session;
		final AtomicLong refCounter = new AtomicLong();
	}

	private final Map<String, SessionHolder> sessions = Coll.autoExpandingMap(String.class, SessionHolder.class);

	@Override
	public Map<String, Serializable> loadSession(Req req, String sessionId) throws Exception {
		Fire.event(Events.SESSION_LOAD, "id", sessionId);

		SessionHolder holder = sessions.get(sessionId);

		if (holder.session == null) {
			synchronized (holder) {
				if (holder.session == null) {

					if (holder.serialized != null) {
						Fire.event(Events.SESSION_DESERIALIZE, "id", sessionId);
						holder.session = U.cast(Msc.deserialize(holder.serialized));
					} else {
						holder.session = Coll.concurrentMap();
						Fire.event(Events.SESSION_CONCURRENT_ACCESS, "id", sessionId);
					}
				}
			}
		}

		holder.refCounter.incrementAndGet();

		return holder.session;
	}

	@Override
	public void saveSession(Req req, String sessionId, Map<String, Serializable> session) throws Exception {
		Fire.event(Events.SESSION_SAVE, "id", sessionId);

		SessionHolder holder = sessions.get(sessionId);
		long refN = holder.refCounter.decrementAndGet();

		U.must(refN >= 0, "The session has negative reference counter!");

		if (refN == 0) {
			synchronized (holder) {
				if (holder.refCounter.get() == 0) {
					Fire.event(Events.SESSION_SERIALIZE, "id", sessionId);
					holder.serialized = Msc.serialize(session);
					holder.session = null;
				}
			}
		}
	}

}
