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

import org.essentials4j.Do;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.http.Route;
import org.rapidoid.http.impl.RouteMeta;
import org.rapidoid.log.Log;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.6.0")
public class OpenAPIDescriptor extends RapidoidThing {

	private final Setup setup;
	private final Config cfg;

	public OpenAPIDescriptor(Setup setup, Config cfg) {
		this.setup = setup;
		this.cfg = cfg;

		if (cfg.isEmpty()) {
			Log.warn("OpenAPI is not configured, will use the defaults!");
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getAPIDocs() {
		Map<String, Object> docs = createRoot();
		Map<String, Object> paths = (Map<String, Object>) docs.computeIfAbsent("paths", x -> U.map());

		Do.group(findPublishableRoutes()).by(Route::path).forEach((path, routes) -> {
			paths.put(path, Do.map(routes).toMap(route -> route.verb().name().toLowerCase(), this::descRoute));
		});

		return docs;
	}

	private List<Route> findPublishableRoutes() {
		// admin zone routes are considered private, don't publish them
		Set<Route> nonAdmin = setup.routes().allNonAdmin();

		// TODO routes can be marked as private/public

		// routes having paths starting with '/_' are considered private, don't publish them
		return Do.findIn(nonAdmin).all(route -> !route.path().startsWith("/_"));
	}

	private Map<String, Object> createRoot() {
		Map<String, Object> spec = Coll.deepCopyOf(cfg.toMap());

		if (spec.isEmpty()) {
			Map<String, Object> info = U.map(
				"version", cfg.entry("version").or("0"),
				"title", cfg.entry("title").or("Untitled")
			);

			spec.put("info", info);

			Map<String, Object> servers = U.map(
				"url", Msc.http() + "://localhost:" + Conf.ON.entry("port").or(8080) + "/"
			);

			spec.put("servers", servers);
		}

		return spec;
	}

	private Map<String, Object> descRoute(Route route) {
		Map<String, Object> desc = U.map();

		RouteMeta meta = route.config().meta();

		if (meta.summary() != null) {
			desc.put("summary", meta.summary());
		}

		if (meta.id() != null) {
			desc.put("operationId", meta.id());
		}

		if (U.notEmpty(meta.tags())) {
			desc.put("tags", meta.tags());
		}

		if (U.notEmpty(meta.schema())) {
			desc.put("parameters", meta.schema());
		}

		desc.put("responses", U.notEmpty(meta.responses()) ? meta.responses() : defaultResponses(route));

		return desc;
	}

	private Map<String, Object> defaultResponses(Route route) {
		Map<String, Object> responses = U.map();

		Map<String, Object> ok = U.map(
			"description", "successful result",
			"content", U.map(
				"application/json", U.map() // FIXME schemaRef(outputSchema)
			)
		);
		responses.put("200", ok);

		Map<String, Object> error = U.map(
			"description", "unexpected error",
			"content", U.map(
				"application/json", schemaRef("Error")
			)
		);

		responses.put("default", error);

		return responses;
	}

	private Map<String, Map<String, String>> schemaRef(String schemaId) {
		return U.map(
			"schema", U.map("$ref", "#/components/schemas/" + schemaId)
		);
	}

}
