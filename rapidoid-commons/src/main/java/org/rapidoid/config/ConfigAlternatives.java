package org.rapidoid.config;

/*
 * #%L
 * rapidoid-commons
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
import org.rapidoid.u.U;
import org.rapidoid.value.Value;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ConfigAlternatives extends RapidoidThing implements BasicConfig {

	private final BasicConfig primary;

	private final BasicConfig alternative;

	public ConfigAlternatives(BasicConfig primary, BasicConfig alternative) {
		this.primary = primary;
		this.alternative = alternative;
	}

	@Override
	public Value<Object> entry(String key) {
		return primary.entry(key).orElse(alternative.entry(key));
	}

	@Override
	public boolean has(String key) {
		return primary.has(key) || alternative.has(key);
	}

	@Override
	public BasicConfig sub(String... keys) {
		U.must(keys.length == 1, "Currently supporting only 1 key!");
		String key = keys[0];

		return primary.has(key) ? primary.sub(key) : alternative.sub(key);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = U.map(alternative.toMap());
		map.putAll(primary.toMap());
		return map;
	}

	@Override
	public ConfigAlternatives or(BasicConfig alternative) {
		return new ConfigAlternatives(this, alternative);
	}

}
