package org.rapidoid.lambda;

import org.rapidoid.RapidoidThing;
import org.rapidoid.u.U;

import java.util.Map;
import java.util.concurrent.Callable;

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

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class Lmbd extends RapidoidThing {

	public static <FROM, TO> Mapper<FROM, TO> mapper(final Map<FROM, TO> map) {
		return new Mapper<FROM, TO>() {
			@Override
			public TO map(FROM key) throws Exception {
				return map.get(key);
			}
		};
	}

	public static <T> boolean eval(Predicate<T> predicate, T target) {
		try {
			return predicate.eval(target);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Cannot evaluate predicate %s on target: %s", predicate, target),
				e);
		}
	}

	public static <FROM, TO> TO eval(Mapper<FROM, TO> mapper, FROM src) {
		try {
			return mapper.map(src);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Cannot evaluate mapper %s on target: %s", mapper, src), e);
		}
	}

	public static <FROM> Object eval(Calc<FROM> calc, FROM src) {
		try {
			return calc.calc(src);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Cannot evaluate calculation %s on target: %s", calc, src), e);
		}
	}

	public static <T> T call(Callable<T> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw U.rte("Error occurred during the call!", e);
		}
	}

	public static <T> Callable<T> callable(final Runnable action) {
		return new Callable<T>() {
			@Override
			public T call() throws Exception {
				action.run();
				return null;
			}
		};
	}

	public static <T> void call(Operation<T> operation, T arg) {
		try {
			operation.execute(arg);
		} catch (Exception e) {
			throw U.rte("Error occurred during the call!", e);
		}
	}

	public static void execute(Executable executable) {
		try {
			executable.execute();
		} catch (Exception e) {
			throw U.rte("Error occurred during the execution!", e);
		}
	}

}
