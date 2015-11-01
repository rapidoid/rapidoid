package org.rapidoid.app;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Metadata;
import org.rapidoid.plugins.entities.Entities;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class Scaffolding {

	public static Class<?> getScaffoldingEntity(String type) {
		Class<?> entityType = Entities.getEntityType(type);

		if (entityType == null || !Metadata.isAnnotated(entityType, Scaffold.class)) {
			return null;
		}

		return entityType;
	}

}
