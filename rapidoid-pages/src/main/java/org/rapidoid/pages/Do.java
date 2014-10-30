package org.rapidoid.pages;

/*
 * #%L
 * rapidoid-pages
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

public class Do {

	public static <T> Action set(final Var<T> var, final T value) {
		return new Action() {
			@Override
			public void execute() {
				var.set(value);
			}
		};
	}

	public static Action inc(final Var<Integer> var, final int value) {
		return new Action() {
			@Override
			public void execute() {
				var.set(var.get() + value);
			}
		};
	}

	public static Action dec(Var<Integer> var, int value) {
		return inc(var, -1);
	}

}
