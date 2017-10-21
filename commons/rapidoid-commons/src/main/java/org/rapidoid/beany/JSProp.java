package org.rapidoid.beany;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.JS;
import org.rapidoid.u.U;

import javax.script.ScriptException;

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
@Since("5.3.0")
public class JSProp extends CustomReadOnlyProp implements Prop {

	private final String expr;

	public JSProp(String expr) {
		this.expr = expr;
	}

	public static boolean is(String propName) {
		return propName.startsWith("$.") || propName.equals("$");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getRaw(Object target) {
		try {
			return (T) JS.eval(expr, U.map("$", target));
		} catch (ScriptException e) {
			throw U.rte(e);
		}
	}

	@Override
	public String getName() {
		return expr;
	}

}
