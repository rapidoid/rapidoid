package org.rapidoid.config;

/*
 * #%L
 * rapidoid-commons
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.value.Value;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ConfigAlternatives extends RapidoidThing {

	private final Config primary;

	private final Config alternative;

	public ConfigAlternatives(Config primary, Config alternative) {
		this.primary = primary;
		this.alternative = alternative;
	}

	public Value<Object> entry(String key) {
		return primary.entry(key).orElse(alternative.entry(key));
	}

	public boolean has(String key) {
		return primary.has(key) || alternative.has(key);
	}

	public Config sub(String key) {
		return primary.has(key) ? primary.sub(key) : alternative.sub(key);
	}
}
