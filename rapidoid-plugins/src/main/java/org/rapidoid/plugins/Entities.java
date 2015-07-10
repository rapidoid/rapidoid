package org.rapidoid.plugins;

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/*
 * #%L
 * rapidoid-plugins
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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class Entities {

	public static <E> Class<E> getEntityType(String simpleTypeName) {
		return Plugins.entities().getEntityType(simpleTypeName);
	}

	public static <E> Class<E> getEntityType(Class<E> clazz) {
		return Plugins.entities().getEntityTypeFor(clazz);
	}

	public static <E> E create(Class<E> entityType) {
		return Plugins.entities().create(entityType);
	}

	public static <E> E create(Class<E> entityType, Map<String, ?> properties) {
		return Plugins.entities().create(entityType, properties);
	}

}
