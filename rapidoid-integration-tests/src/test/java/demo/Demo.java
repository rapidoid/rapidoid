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

package demo;

import org.rapidoid.goodies.Boot;
import org.rapidoid.http.Self;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

public class Demo {

	public static void main(String[] args) {
		App.bootstrap(args, "users.admin.password=a", "secret=X");

		Boot.all();

		On.get("/length/{x}").json((String x) -> x.length());

		Self.get("/length/abc").print();
		Self.get("/hey").print();
	}

}
