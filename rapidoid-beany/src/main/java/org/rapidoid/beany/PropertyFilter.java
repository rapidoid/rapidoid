package org.rapidoid.beany;

import org.rapidoid.annotation.Authors;

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

@SuppressWarnings("serial")
@Authors("Nikolche Mihajlovski")
public abstract class PropertyFilter implements PropertySelector {

	@Override
	public String[] requiredProperties() {
		return null;
	}

	@Override
	public int compare(Prop o1, Prop o2) {
		return 0;
	}

}
