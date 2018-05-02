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
import org.rapidoid.commons.Str;
import org.rapidoid.gui.GUI;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class ScaffoldDSL extends RapidoidThing {

	private final Class<?> entityType;

	private volatile String[] roles;

	private volatile String baseUri;

	public ScaffoldDSL(Class<?> entityType) {
		this.entityType = entityType;
	}

	public ScaffoldDSL roles(String... roles) {
		this.roles = roles;
		return this;
	}

	public ScaffoldDSL baseUri(String baseUri) {
		this.baseUri = baseUri;
		return this;
	}

	public void on(Setup setup) {
		String uri = baseUri != null ? baseUri : GUI.typeUri(entityType);

		if (uri.length() > 1) {
			uri = Str.trimr(uri, "/");
		}

		String[] scafRoles = U.or(roles, new String[0]);

		ScaffoldUtil.scaffold(setup, entityType, uri, scafRoles);
	}

}
