package org.rapidoid.docs.httpcustomauth;

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

import org.rapidoid.http.Self;
import org.rapidoid.setup.App;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;
import org.rapidoid.util.Tokens;

public class Main {

	public static void main(String[] args) {
		App.bootstrap(args);

		My.rolesProvider((req, username) -> username.equals("bob") ? U.set("manager") : U.set());

		On.get("/hey").roles("manager").json(() -> U.map("msg", "ok"));

		// generate a token
		String token = Tokens.serialize(U.map("_user", "bob"));

		// demo request, prints {"msg":"ok"}
		Self.get("/hey?_token=" + token).print();
	}

}
