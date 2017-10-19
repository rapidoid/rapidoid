package org.rapidoid.io;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Arrays;

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
@Since("5.0.11")
public class ResKey extends RapidoidThing {

	final String filename;
	final String[] possibleLocations;

	public ResKey(String filename, String[] possibleLocations) {
		this.filename = filename;
		this.possibleLocations = possibleLocations;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ResKey resKey = (ResKey) o;

		if (!filename.equals(resKey.filename)) return false;

		return Arrays.equals(possibleLocations, resKey.possibleLocations);
	}

	@Override
	public int hashCode() {
		int result = filename.hashCode();
		result = 31 * result + Arrays.hashCode(possibleLocations);
		return result;
	}

}
