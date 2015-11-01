package org.rapidoid.app.builtin;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import org.rapidoid.http.HttpExchange;
import org.rapidoid.u.U;
import org.rapidoid.var.Var;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class BuiltInCmdHandler {

	public void on_set(Var<Object> var, Object value) {
		var.set(value);
	}

	public void on_inc(Var<Integer> var, Integer value) {
		var.set(var.get() + value);
	}

	public void on_dec(Var<Integer> var, Integer value) {
		var.set(var.get() - value);
	}

	public void on_sort(Var<String> var, String value) {
		String before = var.get();
		if (!U.isEmpty(before) && !U.isEmpty(value)) {
			if (!value.startsWith("-") && before.equals(value)) {
				var.set("-" + value);
				return;
			}
		}

		var.set(value);
	}

	public void onCancel(HttpExchange x) {
		x.goBack(1);
	}

	public void onBack(HttpExchange x) {
		x.goBack(1);
	}

}
