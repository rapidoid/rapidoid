package org.rapidoid.plugins.entities;

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.P;
import org.rapidoid.annotation.Since;
import org.rapidoid.plugins.Plugin;

/*
 * #%L
 * rapidoid-commons
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
public interface EntitiesPlugin extends Plugin {

	<E> Class<E> getEntityType(@P("simpleTypeName") String simpleTypeName);

	<E> Class<E> getEntityTypeFor(@P("clazz") Class<E> clazz);

	<E> E create(@P("entityType") Class<E> entityType);

	<E> E create(@P("entityType") Class<E> entityType, @P("properties") Map<String, ?> properties);

}
