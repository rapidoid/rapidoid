package org.rapidoid.setup;

/*
 * #%L
 * rapidoid-http-server
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.jpa.JPA;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.Once;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class AppBootstrap extends RapidoidThing {

	private static final Once jpa = new Once();
	private static final Once adminCenter = new Once();
	private static final Once auth = new Once();

	public AppBootstrap jpa() {
		if (!jpa.go()) return this;

		if (Msc.hasJPA()) {
			JPA.bootstrap(App.path());
		}

		return this;
	}

	public AppBootstrap adminCenter() {
		if (!adminCenter.go()) return this;

		getGoodies().adminCenter(Admin.setup());

		return this;
	}

	public AppBootstrap auth() {
		if (!auth.go()) return this;

		getGoodies().auth(On.setup());

		return this;
	}

	private IGoodies getGoodies() {
		Class<?> goodiesClass = Cls.getClassIfExists("org.rapidoid.goodies.RapidoidGoodies");

		U.must(goodiesClass != null, "Cannot find the Goodies, is the rapidoid-web module missing?");

		return (IGoodies) Cls.newInstance(goodiesClass);
	}

	public void full() {
		jpa();
		adminCenter();
		auth();
	}

	static void reset() {
		jpa.reset();
		auth.reset();
		adminCenter.reset();
	}

}
