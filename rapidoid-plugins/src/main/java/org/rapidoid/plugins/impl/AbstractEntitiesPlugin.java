package org.rapidoid.plugins.impl;

import java.util.Map;

import org.rapidoid.plugins.spec.EntitiesPlugin;

/*
 * #%L
 * rapidoid-plugins
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

/**
 * @author Nikolche Mihajlovski
 * @since 3.0.0
 */
public class AbstractEntitiesPlugin implements EntitiesPlugin {

	@Override
	public <E> Class<E> getEntityType(String simpleTypeName) {
		throw new AbstractMethodError("Not implemented!");
	}

	@Override
	public <E> Class<E> getEntityTypeFor(Class<E> clazz) {
		throw new AbstractMethodError("Not implemented!");
	}

	@Override
	public <E> E create(Class<E> entityType) {
		throw new AbstractMethodError("Not implemented!");
	}

	@Override
	public <E> E create(Class<E> entityType, Map<String, ?> properties) {
		throw new AbstractMethodError("Not implemented!");
	}

}
