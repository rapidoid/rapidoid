package org.rapidoid.activity;

/*
 * #%L
 * rapidoid-activity
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

public abstract class AbstractActivity<T> implements Activity<T> {

	protected final String name;

	public AbstractActivity(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T start() {
		// FIXME implement
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T halt() {
		// FIXME implement
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T shutdown() {
		// FIXME implement
		return (T) this;
	}

	@Override
	public boolean isActive() {
		throw new RuntimeException("Not implemented!"); // FIXME
	}

}
