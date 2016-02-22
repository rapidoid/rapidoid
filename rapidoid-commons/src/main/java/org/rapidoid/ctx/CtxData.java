package org.rapidoid.ctx;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("2.5.0")
public class CtxData {

	private String username = null;

	private Set<String> roles = null;

	private Object persister = null;

	private Object exchange = null;

	private Map<String, Object> extras = null;

	public synchronized CtxData username(String username) {
		this.username = username;
		return this;
	}

	public synchronized String username() {
		return this.username;
	}

	public synchronized CtxData roles(Set<String> roles) {
		this.roles = roles;
		return this;
	}

	public synchronized Set<String> roles() {
		return this.roles;
	}

	public synchronized CtxData persister(Object persister) {
		this.persister = persister;
		return this;
	}

	public synchronized Object persister() {
		return this.persister;
	}

	public synchronized CtxData exchange(Object exchange) {
		this.exchange = exchange;
		return this;
	}

	public synchronized Object exchange() {
		return this.exchange;
	}

	public synchronized CtxData extras(Map<String, Object> extras) {
		this.extras = extras;
		return this;
	}

	public synchronized Map<String, Object> extras() {
		return extras;
	}

	public synchronized <T> T call(Callable<T> action) {
		return Ctx.executeInCtx(this, action);
	}

}
