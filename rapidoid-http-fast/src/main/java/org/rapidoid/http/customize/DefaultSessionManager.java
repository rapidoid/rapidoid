package org.rapidoid.http.customize;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
import org.rapidoid.commons.Coll;
import org.rapidoid.http.Req;

import java.io.Serializable;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class DefaultSessionManager extends RapidoidThing implements SessionManager {

	private final Map<String, Map<String, Serializable>> sessions = Coll.mapOfMaps();

	@Override
	public Map<String, Serializable> loadSession(Req req, String sessionId) throws Exception {
		return sessions.get(sessionId);
	}

	@Override
	public void saveSession(Req req, String sessionId, Map<String, Serializable> session) throws Exception {
		// do nothing, no persistence by default
	}

}
