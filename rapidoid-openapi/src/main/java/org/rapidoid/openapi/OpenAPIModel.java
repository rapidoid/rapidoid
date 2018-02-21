/*-
 * #%L
 * rapidoid-openapi
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

package org.rapidoid.openapi;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.6.0")
public class OpenAPIModel extends RapidoidThing {

	public static Map<String, Map<String, String>> schemaRef(String schemaId) {
		return U.map(
			"schema", U.map("$ref", "#/components/schemas/" + schemaId)
		);
	}

	public static Map<String, String> primitiveSchema(String type) {
		return U.map("type", type);
	}

	public static Map<String, Object> arraySchema(String componentType) {
		return U.map(
			"type", "array",
			"items", primitiveSchema(componentType)
		);
	}

	public static Map<String, Object> defaultErrorSchema() {
		return U.map(
			"type", "object",
			"properties", U.map(
				"error", primitiveSchema("string"),
				"code", primitiveSchema("integer"),
				"status", primitiveSchema("string")
			)
		);
	}

}
