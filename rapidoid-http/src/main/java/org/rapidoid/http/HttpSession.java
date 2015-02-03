package org.rapidoid.http;

import java.io.Serializable;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

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
public interface HttpSession extends Serializable {

	void openSession(String sessionId);

	Map<String, Object> getSession(String sessionId);

	void setAttribute(String sessionId, String attribute, Object value);

	Object getAttribute(String sessionId, String attribute);

	void deleteAttribute(String sessionId, String attribute);

	void closeSession(String sessionId);

	boolean exists(String sessionId);

	void saveSession(String sessionId);

	void loadSession(String sessionId);

	void clearSession(String sessionId);

}
