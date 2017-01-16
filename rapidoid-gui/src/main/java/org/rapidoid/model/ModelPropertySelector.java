package org.rapidoid.model;

/*
 * #%L
 * rapidoid-gui
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Prop;
import org.rapidoid.beany.PropertySelectorByName;
import org.rapidoid.commons.Arr;

@SuppressWarnings("serial")
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class ModelPropertySelector extends PropertySelectorByName {

	private static final String[] ORDER = {"id", "version", "createdBy", "createdOn", "lastUpdatedBy",
		"lastUpdatedOn", "title", "name", "firstName", "lastName", "description"};

	public ModelPropertySelector(String... propertyNames) {
		super(propertyNames);
	}

	@Override
	public int compare(Prop p1, Prop p2) {
		int pos1 = Arr.indexOf(ORDER, p1.getName());
		if (pos1 < 0) {
			pos1 = Integer.MAX_VALUE;
		}

		int pos2 = Arr.indexOf(ORDER, p2.getName());
		if (pos2 < 0) {
			pos2 = Integer.MAX_VALUE;
		}

		return pos1 - pos2;
	}

}
