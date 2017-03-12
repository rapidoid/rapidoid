package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
import org.rapidoid.annotation.Since;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.handler.HttpHandler;

import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface HttpRoutes {

	void on(String verb, String path, HttpHandler handler);

	void on(String verb, String path, ReqHandler handler);

	void on(String verb, String path, ReqRespHandler handler);

	void remove(String verb, String path);

	void addGenericHandler(HttpHandler handler);

	void removeGenericHandler(HttpHandler handler);

	void reset();

	Set<Route> all();

	Set<Route> allAdmin();

	Set<Route> allNonAdmin();

	Customization custom();

	Route find(HttpVerb verb, String path);

	boolean hasRouteOrResource(HttpVerb verb, String uri);

	Runnable onInit();

	void onInit(Runnable onInit);

	boolean isEmpty();

}
