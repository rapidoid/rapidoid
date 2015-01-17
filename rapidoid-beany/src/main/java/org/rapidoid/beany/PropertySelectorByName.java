package org.rapidoid.beany;

/*
 * #%L
 * rapidoid-beany
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.Arrays;

public abstract class PropertySelectorByName implements PropertySelector {

	private final String[] propertyNames;

	public PropertySelectorByName(String... propertyNames) {
		this.propertyNames = propertyNames;
	}

	@Override
	public String[] requiredProperties() {
		return propertyNames;
	}

	@Override
	public int compare(Prop o1, Prop o2) {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(propertyNames);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertySelectorByName other = (PropertySelectorByName) obj;
		if (!Arrays.equals(propertyNames, other.propertyNames))
			return false;
		return true;
	}

}
