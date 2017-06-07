package org.rapidoid.goodies;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;
import org.rapidoid.setup.IGoodies;
import org.rapidoid.setup.Setup;

/*
 * #%L
 * rapidoid-web
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RapidoidGoodies extends RapidoidThing implements IGoodies {

	@Override
	public void overview(Setup setup) {
		Goodies.overview(setup);
	}

	@Override
	public void application(Setup setup) {
		Goodies.application(setup);
	}

	@Override
	public void lifecycle(Setup setup) {
		Goodies.lifecycle(setup);
	}

	@Override
	public void processes(Setup setup) {
		Goodies.processes(setup);
	}

	@Override
	public void dbAdmin(Setup setup) {
		Goodies.dbAdmin(setup);
	}

	@Override
	public void manageables(Setup setup) {
		Goodies.manageables(setup);
	}

	@Override
	public void jmx(Setup setup) {
		Goodies.jmx(setup);
	}

	@Override
	public void metrics(Setup setup) {
		Goodies.metrics(setup);
	}

	@Override
	public void deploy(Setup setup) {
		throw Err.notSupported();
	}

	@Override
	public void ping(Setup setup) {
		Goodies.ping(setup);
	}

	@Override
	public void auth(Setup setup) {
		Goodies.auth(setup);
	}

	@Override
	public void oauth(Setup setup) {
		Goodies.oauth(setup);
	}

	@Override
	public void adminCenter(Setup setup) {
		Goodies.adminCenter(setup);
	}

	@Override
	public void entities(Setup setup) {
		Goodies.entities(setup);
	}

	@Override
	public void welcome(Setup setup) {
		Goodies.welcome(setup);
	}

	@Override
	public void status(Setup setup) {
		Goodies.status(setup);
	}

	@Override
	public void discovery(Setup setup) {
		Goodies.discovery(setup);
	}

	@Override
	public void echo(Setup setup) {
		Goodies.echo(setup);
	}
}
