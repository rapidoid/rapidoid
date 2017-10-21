package org.rapidoid.ctx;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
public class UserInfo extends RapidoidThing implements Serializable {

	private static final long serialVersionUID = 7062732348562440194L;

	public static final UserInfo ANONYMOUS = new UserInfo(null, U.<String>set(), null);

	public final String username;

	public final Set<String> roles;

	public final Map<String, Boolean> is;

	public final Set<String> scope;

	public volatile String email;

	public volatile String name;

	public volatile String oauthId;

	public volatile String oauthProvider;

	public UserInfo(String username, Set<String> roles, Set<String> scope) {
		this.username = username;
		this.roles = Collections.unmodifiableSet(U.safe(roles));
		this.is = rolesMap(roles);
		this.scope = Collections.unmodifiableSet(U.safe(scope));
	}

	private static Map<String, Boolean> rolesMap(Set<String> roles) {
		Map<String, Boolean> rolesMap = U.map();

		for (String role : U.safe(roles)) {
			rolesMap.put(role, true);
		}

		return Collections.unmodifiableMap(rolesMap);
	}

	@Override
	public String toString() {
		return "UserInfo{" +
			"username='" + username + '\'' +
			", roles=" + roles +
			", is=" + is +
			", email='" + email + '\'' +
			", name='" + name + '\'' +
			", oauthId='" + oauthId + '\'' +
			", oauthProvider='" + oauthProvider + '\'' +
			'}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserInfo other = (UserInfo) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
