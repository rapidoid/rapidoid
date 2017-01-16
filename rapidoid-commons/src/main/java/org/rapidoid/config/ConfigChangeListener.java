package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Operation;

import java.util.List;

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
@Since("5.3.0")
class ConfigChangeListener extends RapidoidThing {

	final List<String> keys;
	final Operation<ConfigChanges> operation;

	ConfigChangeListener(List<String> keys, Operation<ConfigChanges> operation) {
		this.keys = keys;
		this.operation = operation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConfigChangeListener that = (ConfigChangeListener) o;

		if (!keys.equals(that.keys)) return false;
		return operation.equals(that.operation);
	}

	@Override
	public int hashCode() {
		int result = keys.hashCode();
		result = 31 * result + operation.hashCode();
		return result;
	}
}
