package org.rapidoid.ctx;

import java.io.Serializable;

/*
 * #%L
 * rapidoid-ctx
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

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 7062732348562440194L;

	public volatile String username;

	public volatile String passwordHash;

	public volatile String email;

	public volatile String name;

	public volatile String oauthId;

	public volatile String oauthProvider;

	public UserInfo() {}

	public UserInfo(String username) {
		this.username = username;
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
