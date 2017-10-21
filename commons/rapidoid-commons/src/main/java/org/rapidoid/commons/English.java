package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

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
public class English extends RapidoidThing {

	private static final Pattern PLURAL1 = Pattern.compile(".*(s|x|z|ch|sh)$");
	private static final Pattern PLURAL1U = Pattern.compile(".*(S|X|Z|CH|SH)$");
	private static final Pattern PLURAL2 = Pattern.compile(".*[bcdfghjklmnpqrstvwxz]o$");
	private static final Pattern PLURAL2U = Pattern.compile(".*[BCDFGHJKLMNPQRSTVWXZ]O$");
	private static final Pattern PLURAL3 = Pattern.compile(".*[bcdfghjklmnpqrstvwxz]y$");
	private static final Pattern PLURAL3U = Pattern.compile(".*[BCDFGHJKLMNPQRSTVWXZ]Y$");

	private static final Map<String, String> IRREGULAR_PLURAL = loadIrregularPlural();

	private static Map<String, String> loadIrregularPlural() {
		Properties props = new Properties();

		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("irregular-plural.txt"));
		} catch (IOException e) {
			Log.error("Couldn't load irregular plural!", e);
		}

		return U.cast(props);
	}

	public static String plural(String noun) {
		if (U.isEmpty(noun)) {
			return noun;
		}

		if (IRREGULAR_PLURAL.containsKey(noun.toLowerCase())) {
			boolean capital = Character.isUpperCase(noun.charAt(0));
			boolean upper = Character.isUpperCase(noun.charAt(noun.length() - 1));
			String pl = IRREGULAR_PLURAL.get(noun.toLowerCase());

			if (upper) {
				return pl.toUpperCase();
			} else {
				return (capital ? Str.capitalized(pl) : pl);
			}

		} else if (PLURAL1.matcher(noun).matches()) {
			return noun + "es";
		} else if (PLURAL2.matcher(noun).matches()) {
			return noun + "es";
		} else if (PLURAL3.matcher(noun).matches()) {
			return Str.sub(noun, 0, -1) + "ies";
		} else if (PLURAL1U.matcher(noun).matches()) {
			return noun + "ES";
		} else if (PLURAL2U.matcher(noun).matches()) {
			return noun + "ES";
		} else if (PLURAL3U.matcher(noun).matches()) {
			return Str.sub(noun, 0, -1) + "IES";
		} else {
			boolean upper = Character.isUpperCase(noun.charAt(noun.length() - 1));
			return noun + (upper ? "S" : "s");
		}
	}

	public static String singular(String noun) {
		if (U.isEmpty(noun)) {
			return noun;
		}

		if (noun.toLowerCase().endsWith("s")) {
			String singular = Str.sub(noun, 0, -1);
			if (plural(singular).equals(noun)) {
				return singular;
			}
		}

		if (noun.toLowerCase().endsWith("es")) {
			String singular = Str.sub(noun, 0, -2);
			if (plural(singular).equals(noun)) {
				return singular;
			}
		}

		if (noun.toLowerCase().endsWith("ies")) {
			String singular = Str.sub(noun, 0, -1);
			if (!singular.isEmpty()) {
				boolean upper = Character.isUpperCase(singular.charAt(singular.length() - 1));
				singular += upper ? 'Y' : 'y';
				if (plural(singular).equals(noun)) {
					return singular;
				}
			}
		}

		// FIXME handle irregular plural
		return noun;
	}

}
