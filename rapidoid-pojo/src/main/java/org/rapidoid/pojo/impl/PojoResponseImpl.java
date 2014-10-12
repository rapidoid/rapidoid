package org.rapidoid.pojo.impl;

import org.rapidoid.pojo.PojoResponse;

/*
 * #%L
 * rapidoid-pojo
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

public class PojoResponseImpl implements PojoResponse {

	private final Object result;

	private final boolean hasError;

	public PojoResponseImpl(Object result, boolean hasError) {
		this.result = result;
		this.hasError = hasError;
	}

	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public boolean hasError() {
		return hasError;
	}

}
