package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.env.Env;

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
@Since("5.1.0")
public class AnsiColor extends RapidoidThing {

	public static String bold(String text) {
		return code("1m", text);
	}

	public static String black(String text) {
		return code("0;30m", text);
	}

	public static String darkGray(String text) {
		return code("1;30m", text);
	}

	public static String red(String text) {
		return code("0;31m", text);
	}

	public static String lightRed(String text) {
		return code("1;31m", text);
	}

	public static String green(String text) {
		return code("0;32m", text);
	}

	public static String lightGreen(String text) {
		return code("1;32m", text);
	}

	public static String brownOrange(String text) {
		return code("0;33m", text);
	}

	public static String yellow(String text) {
		return code("1;33m", text);
	}

	public static String blue(String text) {
		return code("0;34m", text);
	}

	public static String lightBlue(String text) {
		return code("1;34m", text);
	}

	public static String purple(String text) {
		return code("0;35m", text);
	}

	public static String lightPurple(String text) {
		return code("1;35m", text);
	}

	public static String cyan(String text) {
		return code("0;36m", text);
	}

	public static String lightCyan(String text) {
		return code("1;36m", text);
	}

	public static String lightGray(String text) {
		return code("0;37m", text);
	}

	public static String white(String text) {
		return code("1;37m", text);
	}

	private static String code(String code, String text) {
		return Env.dev() ? "\33[" + code + text + "\33[0m" : text;
	}

}
