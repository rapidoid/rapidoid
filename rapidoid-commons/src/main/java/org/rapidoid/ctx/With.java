package org.rapidoid.ctx;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Map;
import java.util.Set;

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

@Authors("Nikolche Mihajlovski")
@Since("2.5.0")
public class With extends RapidoidThing {

	public static WithContext tag(String tag) {
		return new WithContext().tag(tag);
	}

	public static WithContext username(String username) {
		return new WithContext().username(username);
	}

	public static WithContext roles(Set<String> roles) {
		return new WithContext().roles(roles);
	}

	public static WithContext scope(Set<String> scope) {
		return new WithContext().scope(scope);
	}

	public static WithContext persister(Object persister) {
		return new WithContext().persister(persister);
	}

	public static WithContext exchange(Object exchange) {
		return new WithContext().exchange(exchange);
	}

	public static WithContext extras(Map<String, Object> extras) {
		return new WithContext().extras(extras);
	}

}
