package org.rapidoid.html;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.u.U;

import java.util.Date;

/*
 * #%L
 * rapidoid-html
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
public class BasicUtils extends RapidoidThing {

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
