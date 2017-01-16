package org.rapidoid.ctx;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.job.Jobs;

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
public class WithContext extends RapidoidThing {

	private volatile String tag;

	private volatile String username;

	private volatile Set<String> roles;

	private volatile Set<String> scope;

	private volatile Object persister;

	private volatile Object exchange;

	private volatile Map<String, Object> extras;

	public String tag() {
		return tag;
	}

	public WithContext tag(String tag) {
		this.tag = tag;
		return this;
	}

	public WithContext username(String username) {
		this.username = username;
		return this;
	}

	public String username() {
		return this.username;
	}

	public WithContext roles(Set<String> roles) {
		this.roles = roles;
		return this;
	}

	public Set<String> roles() {
		return this.roles;
	}

	public WithContext scope(Set<String> scope) {
		this.scope = scope;
		return this;
	}

	public Set<String> scope() {
		return scope;
	}

	public WithContext persister(Object persister) {
		this.persister = persister;
		return this;
	}

	public Object persister() {
		return this.persister;
	}

	public WithContext exchange(Object exchange) {
		this.exchange = exchange;
		return this;
	}

	public Object exchange() {
		return this.exchange;
	}

	public WithContext extras(Map<String, Object> extras) {
		this.extras = extras;
		return this;
	}

	public Map<String, Object> extras() {
		return extras;
	}

	public void run(Runnable action) {
		Jobs.executeInContext(this, action);
	}

}
