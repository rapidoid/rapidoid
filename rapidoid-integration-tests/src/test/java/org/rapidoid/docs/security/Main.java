package org.rapidoid.docs.security;

/*
 * #%L
 * rapidoid-integration-tests
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

import org.rapidoid.setup.App;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

public class Main {

	public static void main(String[] args) {
		App.bootstrap(args).auth();

		On.get("/").html((req, resp) -> "this is public!");

		On.get("/manage").roles("manager").html((req, resp) -> "this is private!");

		/* Dummy login: successful if the username is the same as the password */

		My.loginProvider((req, username, password) -> username.equals(password));

		/* Gives the 'manager' role to every logged-in user */

		My.rolesProvider((req, username) -> U.set("manager"));
	}

}
