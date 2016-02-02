package org.rapidoid.pojo;

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
import org.rapidoid.pojo.impl.DispatchReqKind;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class DispatchResult {

	private final Object result;

	private final DispatchReqKind kind;

	private final Map<String, Object> config;

	public DispatchResult(Object result, DispatchReqKind kind, Map<String, Object> config) {
		this.result = result;
		this.kind = kind;
		this.config = config;
	}

	public Object getResult() {
		return result;
	}

	public DispatchReqKind getKind() {
		return kind;
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	@Override
	public String toString() {
		return "DispatchResult [result=" + result + ", kind=" + kind + "]";
	}

}
