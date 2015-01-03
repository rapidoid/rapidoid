package org.rapidoid.html;

/*
 * #%L
 * rapidoid-html
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

import java.util.Date;

import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class BasicUtils {

	public static boolean eq(Object a, Object b) {
		return U.eq(a, b);
	}

	public static boolean bool(Object value) {
		return Cls.convert(value, Boolean.class);
	}

	public static String str(Object value) {
		return Cls.convert(value, String.class);
	}

	public static int num(Object value) {
		return Cls.convert(value, Integer.class);
	}

	public static long numL(Object value) {
		return Cls.convert(value, Long.class);
	}

	public static Date date(Object value) {
		return Cls.convert(value, Date.class);
	}

	public static <T> T[] arr(T... arr) {
		return arr;
	}

}
