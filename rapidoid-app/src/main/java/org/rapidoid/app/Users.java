package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.beany.Beany;
import org.rapidoid.db.DB;
import org.rapidoid.security.Secure;
import org.rapidoid.util.U;
import org.rapidoid.util.UserInfo;

@Authors("Nikolche Mihajlovski")
public class Users {

	public static <T> T current(Class<T> userClass) {
		UserInfo u = Secure.user();
		if (u == null) {
			return null;
		}

		T user = U.singleOrNone(DB.find(userClass, "username", u.username));

		if (user == null) {
			user = DB.entity(userClass, Beany.read(u));
			DB.insert(user);
		}

		return user;
	}

}
