package org.rapidoid.ctx;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;

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
public class UserInfo implements Serializable {

	private static final String USERNAME = "_USER.USERNAME";
	private static final String EMAIL = "_USER.EMAIL";
	private static final String NAME = "_USER.NAME";

	private static final long serialVersionUID = 7062732348562440194L;

	private static final UserInfo ANONYMOUS = new UserInfo("anonymous", null, "Anonymous", null, null,
			U.set("ANONYMOUS"));

	public final String username;

	public final String email;

	public final String name;

	public final String oauthId;

	public final String oauthProvider;

	public volatile Set<String> roles;

	public volatile Map<String, Boolean> is;

	public UserInfo(String username) {
		this(username, username, username);
	}

	public UserInfo(String username, String email, String name) {
		this(username, email, name, null, null);
	}

	public UserInfo(String username, String email, String name, String oauthId, String oauthProvider) {
		this(username, email, name, oauthId, oauthProvider, Roles.getRolesFor(username));
	}

	public UserInfo(String username, String email, String name, String oauthId, String oauthProvider, Set<String> roles) {
		this.username = username;
		this.email = email;
		this.name = name;
		this.oauthId = oauthId;
		this.oauthProvider = oauthProvider;
		this.roles = roles;
		this.is = rolesMap(roles);
	}

	private static Map<String, Boolean> rolesMap(Set<String> roles) {
		Map<String, Boolean> rolesMap = U.map();

		for (String role : U.safe(roles)) {
			rolesMap.put(role, true);
		}

		return rolesMap;
	}

	public static UserInfo from(Map<String, ?> scope) {
		String username = (String) scope.get(USERNAME);
		String email = (String) scope.get(EMAIL);
		String name = (String) scope.get(NAME);

		return username != null ? new UserInfo(username, email, name) : ANONYMOUS;
	}

	public void saveTo(Map<String, Serializable> scope) {
		scope.put(USERNAME, this.username);
		scope.put(EMAIL, this.email);
		scope.put(NAME, this.name);
	}

	@Override
	public String toString() {
		return "UserInfo [username=" + username + ", email=" + email + ", name=" + name + ", oauthId=" + oauthId
				+ ", oauthProvider=" + oauthProvider + "]";
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
