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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.handler.DataSchemas;
import org.rapidoid.http.impl.RouteMeta;
import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;

@Authors({"Daniel Braga", "Nikolche Mihajlovski"})
@Since("5.6.0")
public class OpenAPIDemo {

	public static void main(String[] args) {
		Setup setup = On.setup();

		RouteMeta meta = new RouteMeta();
		meta.id("test1").summary("Test 1")
			.tags(U.set("tag1", "tag2"))
			.inputSchema(DataSchemas.openAPI("in", OpenAPIModel.arraySchema("string")))
			.outputSchema(DataSchemas.openAPI("out", OpenAPIModel.arraySchema("string")));

		On.get("/test1/").meta(meta).plain("hello");

		On.get("/test2/foo").plain("hello");
		On.get("/test2/output").json("hello");
		On.post("/test2/output").xml("hello");
		On.delete("/test2/output").serve("hello");

		OpenAPI.bootstrap(setup);
	}

}
