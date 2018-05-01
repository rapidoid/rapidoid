/*-
 * #%L
 * rapidoid-rest
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

package org.rapidoid.setup;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.security.Role;
import org.rapidoid.util.LazyInit;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Admin extends RapidoidThing {

	private static final LazyInit<Setup> setup = new LazyInit<>(Admin::createAdminSetup);

	private static Setup createAdminSetup() {
		return Setups.create("admin");
	}

	public static synchronized Setup setup() {
		return setup.get();
	}

	public static synchronized OnRoute route(String verb, String path) {
		return setup().on(verb, path).roles(Role.ADMINISTRATOR);
	}

	public static synchronized OnRoute any(String path) {
		return setup().any(path).roles(Role.ADMINISTRATOR);
	}

	public static synchronized OnRoute get(String path) {
		return setup().get(path).roles(Role.ADMINISTRATOR);
	}

	public static synchronized OnRoute post(String path) {
		return setup().post(path).roles(Role.ADMINISTRATOR);
	}

	public static synchronized OnRoute put(String path) {
		return setup().put(path).roles(Role.ADMINISTRATOR);
	}

	public static synchronized OnRoute delete(String path) {
		return setup().delete(path).roles(Role.ADMINISTRATOR);
	}

	public static synchronized OnRoute patch(String path) {
		return setup().patch(path).roles(Role.ADMINISTRATOR);
	}

	public static synchronized OnRoute options(String path) {
		return setup().options(path).roles(Role.ADMINISTRATOR);
	}

	public static synchronized OnRoute head(String path) {
		return setup().head(path).roles(Role.ADMINISTRATOR);
	}

	public static synchronized OnRoute trace(String path) {
		return setup().trace(path).roles(Role.ADMINISTRATOR);
	}

	public static synchronized OnRoute page(String path) {
		return setup().page(path).roles(Role.ADMINISTRATOR);
	}

	public static synchronized ServerSetup port(int port) {
		return new ServerSetup(Conf.ADMIN).port(port);
	}

	public static synchronized ServerSetup address(String address) {
		return new ServerSetup(Conf.ADMIN).address(address);
	}

}
