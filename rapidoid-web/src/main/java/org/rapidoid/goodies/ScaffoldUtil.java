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
import org.rapidoid.setup.Setup;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
class ScaffoldUtil extends RapidoidThing {

	static void scaffold(Setup setup, Class<?> entityType, String uri, String[] roles, boolean internal) {
		// RESTful services
		setup.get(uri)
			.internal(internal)
			.roles(roles)
			.json(X.index(entityType));

		setup.get(uri + "/{id}")
			.internal(internal)
			.roles(roles)
			.json(X.read(entityType));

		setup.post(uri)
			.internal(internal)
			.transaction()
			.roles(roles)
			.json(X.insert(entityType));

		setup.put(uri + "/{id}")
			.internal(internal)
			.transaction()
			.roles(roles)
			.json(X.update(entityType));

		setup.delete(uri + "/{id}")
			.internal(internal)
			.transaction()
			.roles(roles)
			.json(X.delete(entityType));

		// GUI
		setup.page(uri + "/manage")
			.internal(internal)
			.roles(roles)
			.mvc(X.manage(entityType, uri));

		setup.page(uri + "/add")
			.internal(internal)
			.transaction()
			.roles(roles)
			.mvc(X.add(entityType, uri));

		setup.page(uri + "/{id}/view")
			.internal(internal)
			.transaction()
			.roles(roles)
			.mvc(X.view(entityType, uri));

		setup.page(uri + "/{id}/edit")
			.internal(internal)
			.transaction()
			.roles(roles)
			.mvc(X.edit(entityType, uri));
	}

}
