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
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Route;
import org.rapidoid.http.impl.RouteMeta;
import org.rapidoid.log.Log;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates structured API docs, based on the OpenAPI specification:
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md
 */
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
		Map<String, Object> components = (Map<String, Object>) docs.computeIfAbsent("components", x -> U.map());
		Map<String, Object> schemas = (Map<String, Object>) components.computeIfAbsent("schemas", x -> U.map());

		schemas.computeIfAbsent("Error", x -> OpenAPIModel.defaultErrorSchema());

		Do.group(findPublishableRoutes()).by(Route::path).forEach((path, routes) -> {
			paths.put(path, Do.map(routes).toMap(
				route -> route.verb().name().toLowerCase(),
				route -> descRoute(route, components)
			));
		});

		return docs;
	}

	private List<Route> findPublishableRoutes() {
		// admin zone routes are considered private, don't publish them
		Set<Route> nonAdmin = setup.routes().allNonAdmin();

		return Do.findIn(nonAdmin).all(this::isPublishable);
	}

	private boolean isPublishable(Route route) {
		RouteMeta meta = route.config().meta();

		return route.isAPI() // non-API routes (e.g. pages) won't be published
			&& meta.publish() // routes can be marked as private/public
			&& !route.path().startsWith("/_"); // routes having paths starting with '/_' are considered private
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

	private Map<String, Object> descRoute(Route route, Map<String, Object> components) {
		Map<String, Object> desc = U.map();

		RouteMeta meta = route.config().meta();

		if (meta.summary() != null) {
			desc.put("summary", meta.summary());
		}

		if (meta.description() != null) {
			desc.put("description", meta.description());
		}

		if (meta.id() != null) {
			desc.put("operationId", meta.id());
		}

		if (U.notEmpty(meta.tags())) {
			desc.put("tags", meta.tags());
		}

		if (U.notEmpty(meta.inputSchema())) {
			desc.put("parameters", meta.inputSchema().toOpenAPISchema());
		}

		desc.put("responses", U.notEmpty(meta.responses()) ? meta.responses() : defaultResponses(route));

		return desc;
	}

	private Map<String, Object> defaultResponses(Route route) {
		RouteMeta meta = route.config().meta();
		Map<String, Object> responses = U.map();

		Map<String, Object> schema = U.notEmpty(meta.outputSchema()) ? meta.outputSchema().toOpenAPISchema() : U.map();

		MediaType mediaType = U.or(route.config().contentType(), MediaType.JSON);
		String contentType = new String(mediaType.getBytes());

		Map<String, Object> ok = U.map(
			"description", "success",
			"content", U.map(
				contentType, schema
			)
		);
		responses.put("200", ok);

		Map<String, Object> error = U.map(
			"description", "unexpected error",
			"content", U.map(
				contentType, OpenAPIModel.schemaRef("Error")
			)
		);

		responses.put("default", error);

		return responses;
	}

}
