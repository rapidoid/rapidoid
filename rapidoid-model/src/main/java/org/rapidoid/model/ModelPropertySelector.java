package org.rapidoid.model;

/*
 * #%L
 * rapidoid-model
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

import org.rapidoid.prop.Prop;
import org.rapidoid.prop.PropertySelectorByName;

public abstract class ModelPropertySelector extends PropertySelectorByName {

	public ModelPropertySelector(String... propertyNames) {
		super(propertyNames);
	}

	@Override
	public int compare(Prop p1, Prop p2) {
		if (p1.getName().equals("id")) {
			return -1;
		} else if (p2.getName().equals("id")) {
			return 1;
		}
		return 0;
	}

}
