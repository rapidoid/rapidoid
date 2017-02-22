package org.rapidoid.web;

/*
 * #%L
 * rapidoid-web
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
import org.rapidoid.http.HttpVerb;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public abstract class GenericRouteConfigListener<T> extends GenericConfigListener<T> {

	public GenericRouteConfigListener(Class<T> type) {
		super(type);
	}

	@Override
	protected void applyEntry(String key, T config) {
		String[] verbUri = key.split("\\s+");

		final HttpVerb verb;
		String uri;

		if (verbUri.length == 1) {
			verb = HttpVerb.GET;
			uri = verbUri[0];

		} else if (verbUri.length == 2) {
			verb = HttpVerb.from(verbUri[0]);
			uri = verbUri[1];

		} else {
			throw U.rte("Invalid route!");
		}

		addHandler(config, verb, uri);
	}

	protected abstract void addHandler(final T config, final HttpVerb verb, final String uri);

}
