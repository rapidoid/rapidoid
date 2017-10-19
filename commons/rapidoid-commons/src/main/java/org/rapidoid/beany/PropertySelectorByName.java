package org.rapidoid.beany;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.Arrays;
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
@Since("2.0.0")
public abstract class PropertySelectorByName extends RapidoidThing implements PropertySelector {

	private static final long serialVersionUID = 7826078564960583655L;

	private final String[] propertyNames;

	private final Set<String> exclude;

	private final Set<String> include;

	public PropertySelectorByName(String... propertyNames) {
		this.propertyNames = propertyNames;
		this.exclude = excluding(propertyNames);
		this.include = including(propertyNames);
	}

	@Override
	public Set<String> include() {
		return include;
	}

	@Override
	public Set<String> exclude() {
		return exclude;
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
		return Arrays.equals(propertyNames, other.propertyNames);
	}

	static Set<String> excluding(Object[] properties) {
		Set<String> excluding = U.set();

		for (Object prop : properties) {
			if (prop instanceof String) {
				String strProp = (String) prop;
				if (strProp.startsWith("-")) {
					excluding.add(strProp.substring(1));
				}
			}
		}

		return excluding;
	}

	static Set<String> including(Object[] properties) {
		Set<String> including = U.set();

		for (Object prop : properties) {
			if (prop instanceof String) {
				String strProp = (String) prop;
				if (!strProp.startsWith("-")) {
					including.add(strProp);
				}
			}
		}

		return including;
	}

}
