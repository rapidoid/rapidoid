/*-
 * #%L
 * rapidoid-web
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

package org.rapidoid.goodies;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.setup.App;
import org.rapidoid.setup.Setup;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
@SuppressWarnings("WeakerAccess")
public class Boot extends RapidoidThing {

	public static Booter on(Setup setup) {
		return new Booter(setup);
	}

	private static Booter main() {
		return on(App.setup());
	}

	public static Booter adminCenter() {
		return main().adminCenter();
	}

	public static Booter auth() {
		return main().auth();
	}

	public static Booter lifecycle() {
		return main().lifecycle();
	}

	public static Booter overview() {
		return main().overview();
	}

	public static Booter application() {
		return main().application();
	}

	public static Booter metrics() {
		return main().metrics();
	}

	public static Booter jmx() {
		return main().jmx();
	}

	public static Booter entities() {
		return main().entities();
	}

	public static Booter oauth() {
		return main().oauth();
	}

	public static Booter openapi() {
		return main().openapi();
	}

	public static Booter jpa(String... packages) {
		return main().jpa(packages);
	}

	public static Booter all() {
		return main().all();
	}

}
