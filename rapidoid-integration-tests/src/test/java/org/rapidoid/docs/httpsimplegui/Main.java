/*-
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.docs.httpsimplegui;

import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

public class Main {

	public static void main(String[] args) {
		App.gui()
			.brand("Cool app!")
			.title("the head title")
			.search(true)
			.addMenuItem("/", 10, "Home")
			.addMenuItem("/portfolio", 20, "Portfolio")
			.addSubMenuItem("/about", 30, "About", 1, "About Us")
			.addSubMenuItem("/", 30, "About", 2, "About You");

		/* On [GET /hi] or [POST /hi] return a "Hello World" web page */

		On.page("/hi").mvc("Hello <b>world</b>!");
	}

}
