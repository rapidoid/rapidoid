package org.rapidoid.http;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

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

public class InMemoryHttpSession implements HttpSession {

	private static final long serialVersionUID = -3390334080583841460L;

	private final ConcurrentMap<String, ConcurrentMap<String, Object>> sessions = U.concurrentMap();

	@Override
	public void openSession(String sessionId) {
		Object prev = sessions.putIfAbsent(sessionId, U.<String, Object> concurrentMap());
		U.must(prev == null, "There is already an existing session with ID=%s", sessionId);
	}

	@Override
	public Map<String, Object> getSession(String sessionId) {
		return Collections.unmodifiableMap(session(sessionId));
	}

	@Override
	public void setAttribute(String sessionId, String attribute, Object value) {
		session(sessionId).put(attribute, value);
	}

	@Override
	public Object getAttribute(String sessionId, String attribute) {
		return session(sessionId).get(attribute);
	}

	@Override
	public void deleteAttribute(String sessionId, String attribute) {
		session(sessionId).remove(attribute);
	}

	@Override
	public void closeSession(String sessionId) {
		sessions.remove(sessionId);
	}

	@Override
	public boolean exists(String sessionId) {
		return sessions.containsKey(sessionId);
	}

	private ConcurrentMap<String, Object> session(String sessionId) {
		ConcurrentMap<String, Object> session = sessions.get(sessionId);
		U.must(session != null, "Cannot find session with ID=%s", sessionId);
		return session;
	}

	@Override
	public void saveSession(String sessionId) {
	}

	@Override
	public void loadSession(String sessionId) {
	}

}
