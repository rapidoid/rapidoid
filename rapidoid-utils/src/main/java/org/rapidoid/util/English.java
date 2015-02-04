package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
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

import java.util.Map;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class English {

	private static final Pattern PLURAL1 = Pattern.compile(".*(s|x|z|ch|sh)$");
	private static final Pattern PLURAL1U = Pattern.compile(".*(S|X|Z|CH|SH)$");
	private static final Pattern PLURAL2 = Pattern.compile(".*[bcdfghjklmnpqrstvwxz]o$");
	private static final Pattern PLURAL2U = Pattern.compile(".*[BCDFGHJKLMNPQRSTVWXZ]O$");
	private static final Pattern PLURAL3 = Pattern.compile(".*[bcdfghjklmnpqrstvwxz]y$");
	private static final Pattern PLURAL3U = Pattern.compile(".*[BCDFGHJKLMNPQRSTVWXZ]Y$");

	private static final Map<String, String> IRREGULAR_PLURAL = IO.loadMap("irregular-plural.txt");

	public static String plural(String s) {
		if (U.isEmpty(s)) {
			return s;
		}

		if (IRREGULAR_PLURAL.containsKey(s.toLowerCase())) {
			boolean capital = Character.isUpperCase(s.charAt(0));
			boolean upper = Character.isUpperCase(s.charAt(s.length() - 1));
			String pl = IRREGULAR_PLURAL.get(s.toLowerCase());

			if (upper) {
				return pl.toUpperCase();
			} else {
				return (capital ? U.capitalized(pl) : pl);
			}

		} else if (PLURAL1.matcher(s).matches()) {
			return s + "es";
		} else if (PLURAL2.matcher(s).matches()) {
			return s + "es";
		} else if (PLURAL3.matcher(s).matches()) {
			return U.mid(s, 0, -1) + "ies";
		} else if (PLURAL1U.matcher(s).matches()) {
			return s + "ES";
		} else if (PLURAL2U.matcher(s).matches()) {
			return s + "ES";
		} else if (PLURAL3U.matcher(s).matches()) {
			return U.mid(s, 0, -1) + "IES";
		} else {
			boolean upper = Character.isUpperCase(s.charAt(s.length() - 1));
			return s + (upper ? "S" : "s");
		}
	}

}
