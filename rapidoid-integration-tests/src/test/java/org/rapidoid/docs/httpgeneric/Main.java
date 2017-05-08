package org.rapidoid.docs.httpgeneric;

import org.rapidoid.setup.On;
import org.rapidoid.u.U;

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

public class Main {

	public static void main(String[] args) {
		/* Generic handlers match any request (in the declaration order) */

		On.req(req -> req.data().isEmpty() ? "Simple: " + req.uri() : null);

		/* The next handler is executed if the previous returns [NOT FOUND] */

		On.req(req -> U.list(req.verb(), req.uri(), req.data()));
	}

}
