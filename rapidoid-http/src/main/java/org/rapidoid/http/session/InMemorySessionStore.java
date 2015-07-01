package org.rapidoid.http.session;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
public class InMemorySessionStore implements SessionStore {

	private final ConcurrentMap<String, Map<String, Serializable>> sessions = U.concurrentMap();

	public boolean exists(String sessionId) {
		return sessions.containsKey(sessionId);
	}

	@Override
	public Map<String, Serializable> get(String sessionId) {
		Map<String, Serializable> session = sessions.get(sessionId);

		if (session == null) {
			session = U.synchronizedMap();
		}

		return session;
	}

	@Override
	public void set(String sessionId, Map<String, Serializable> session) {
		if (!U.isEmpty(session)) {
			sessions.put(sessionId, session);
		} else {
			sessions.remove(sessionId);
		}
	}

}
