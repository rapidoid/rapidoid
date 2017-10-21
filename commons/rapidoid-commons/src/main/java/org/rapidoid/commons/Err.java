package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

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
public class Err extends RapidoidThing {

	public static final String MSG_INTENTIONAL = "Intentional error!";

	public static RuntimeException notExpected() {
		return U.rte("This operation is not expected to be called!");
	}

	public static RuntimeException notReady() {
		return U.rte("Not yet implemented!");
	}

	public static RuntimeException notSupported() {
		return U.rte("This operation is not supported by this implementation!");
	}

	public static void rteIf(boolean failureCondition, String msg) {
		if (failureCondition) {
			throw U.rte(msg);
		}
	}

	public static IllegalArgumentException illegalArg(String message, Object... args) {
		return new IllegalArgumentException(U.frmt(message, args));
	}

	public static void secure(boolean condition, String msg) {
		if (!condition) {
			throw new SecurityException(U.str(msg));
		}
	}

	public static void secure(boolean condition, String msg, Object arg) {
		if (!condition) {
			throw new SecurityException(U.frmt(msg, arg));
		}
	}

	public static void secure(boolean condition, String msg, Object arg1, Object arg2) {
		if (!condition) {
			throw new SecurityException(U.frmt(msg, arg1, arg2));
		}
	}

	public static void argMust(boolean expectedCondition, String message, Object... args) {
		if (!expectedCondition) {
			throw illegalArg(message, args);
		}
	}

	public static void bounds(int value, int min, int max) {
		U.must(value >= min && value <= max, "%s is not in the range [%s, %s]!", value, min, max);
	}

	public static RuntimeException intentional() {
		return new RuntimeException(MSG_INTENTIONAL);
	}

}
