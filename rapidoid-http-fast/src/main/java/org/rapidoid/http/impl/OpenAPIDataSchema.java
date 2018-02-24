/*-
 * #%L
 * rapidoid-http-fast
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

package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.DataSchema;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class OpenAPIDataSchema extends RapidoidThing implements DataSchema {

	private final String id;

	private final Map<String, Object> schema;

	public OpenAPIDataSchema(String id, Map<String, Object> schema) {
		this.id = id;
		this.schema = schema;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public Map<String, Object> toOpenAPISchema() {
		return schema;
	}

}
