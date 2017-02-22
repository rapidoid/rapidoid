package org.rapidoid.web.config.listener;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.ConfigChanges;
import org.rapidoid.lambda.Operation;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public abstract class GenericConfigListener<T> extends RapidoidThing implements Operation<ConfigChanges> {

	protected final Class<T> type;

	public GenericConfigListener(Class<T> type) {
		this.type = type;
	}

	@Override
	public void execute(ConfigChanges changes) throws Exception {
		for (Map.Entry<String, T> e : changes.getAddedOrChangedAs(type).entrySet()) {

			String key = e.getKey().trim();
			T config = e.getValue();

			applyEntry(key, config);
		}
	}

	protected abstract void applyEntry(String key, final T config);

}
