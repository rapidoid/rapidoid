package org.rapidoid.http;

/*
 * #%L
 * rapidoid-rest
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
import org.rapidoid.annotation.OfType;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Metadata;
import org.rapidoid.commons.Arr;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.lambda.Dynamic;
import org.rapidoid.u.U;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Future;

@Authors("Nikolche Mihajlovski")
@Since("4.4.0")
public class DynamicRESTClient implements Dynamic {

	private static final String[] VERBS = { "GET", "POST", "PUT", "DELETE" };

	private final Class<?> clientInterface;

	private final String configFile;

	private final Config config;

	public DynamicRESTClient(Class<?> clientInterface) {
		this.clientInterface = clientInterface;
		this.configFile = "endpoints/" + clientInterface.getSimpleName() + ".yaml";
		this.config = Conf.refreshing(configFile);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object call(Method m, Object[] args) {
		U.must(!config.isEmpty(), "Cannot find the endpoint configuration (file '%s') for the interface: %s",
				configFile, clientInterface.getSimpleName());

		Map<String, Object> cfg = config.getOrFail(m.getName(), Map.class);

		String verb = verbOf(cfg);
		String url = (String) cfg.get(verb);

		U.must(!U.isEmpty(verb), "The [verb: url] entry is not configured for the method: %s", m);
		U.must(!U.isEmpty(url), "Cannot find 'url' configuration for the method: %s", m);

		Class<Object> retType = (Class<Object>) m.getReturnType();
		Class<?>[] paramTypes = m.getParameterTypes();
		Class<?> lastParamType = U.last(paramTypes);

		if (lastParamType != null && Callback.class.isAssignableFrom(lastParamType)) {
			// async result with callback

			U.must(retType.equals(void.class) || Future.class.isAssignableFrom(retType)
					|| org.rapidoid.concurrent.Future.class.isAssignableFrom(retType));

			Callback<Object> callback = (Callback<Object>) U.last(args);
			U.notNull(callback, "callback");

			args = Arr.sub(args, 0, -1);
			String realUrl = String.format(url, args);

			OfType ofType = Metadata.get(U.last(m.getParameterAnnotations()), OfType.class);

			Class<Object> resultType = (Class<Object>) (ofType != null ? ofType.value() : Object.class);

			return REST.call(verb, realUrl, resultType, callback);
		} else {
			String realUrl = String.format(url, args);
			return REST.call(verb, realUrl, retType);
		}
	}

	private String verbOf(Map<String, Object> cfg) {
		for (String verb : VERBS) {
			if (cfg.containsKey(verb)) {
				return verb;
			}
			if (cfg.containsKey(verb.toLowerCase())) {
				return verb.toLowerCase();
			}
		}

		return null;
	}

}
