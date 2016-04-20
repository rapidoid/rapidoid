package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.commons.Coll;
import org.rapidoid.commons.Str;
import org.rapidoid.u.U;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-render
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
public class RenderCtxImpl extends RapidoidThing implements RenderCtx {

	private final OutputStream out;
	private final String ext;
	private final List<Object> model;

	public RenderCtxImpl(OutputStream out, String filename, Object... model) {
		this.out = out;
		this.model = U.list(model);

		String fileExt = Str.cutFromFirst(filename, ".");
		this.ext = fileExt != null ? "." + fileExt : "";
	}

	@Override
	public void print(String s) {
		try {
			out.write(s.getBytes());
		} catch (IOException e) {
			throw U.rte(e);
		}
	}

	@Override
	public Object[] iter(String name) {
		Object val = get(name);

		if (val instanceof Collection<?>) {
			return ((Collection<?>) val).toArray();

		} else if (val instanceof Object[]) {
			return (Object[]) val;

		} else {
			return val != null && !Boolean.FALSE.equals(val) ? U.array(val) : U.array();
		}
	}

	@Override
	public void val(String name, boolean escape) {
		valOr(name, "N/A", escape);
	}

	@Override
	public void valOr(String name, String or, boolean escape) {
		Object val = name.equals(".") ? self() : get(name);
		val = U.or(val, or);
		print(str(escape, val));
	}

	private String str(boolean escape, Object val) {
		String str = U.str(val);
		if (escape) str = Str.htmlEscape(str);
		return str;
	}

	private Object self() {
		return !model.isEmpty() ? model.get(model.size() - 1) : null;
	}

	@Override
	public void push(int index, Object v) {
		if (v != null && !Boolean.TRUE.equals(v)) model.add(v);
	}

	@Override
	public void pop(int index, Object v) {
		if (v != null && !Boolean.TRUE.equals(v)) {
			Object del = model.remove(model.size() - 1);
			U.must(del == v);
		}
	}

	@Override
	public void call(String name) {
		Templates.fromFile(name + ext).renderTo(out, model.toArray());
	}

	@Override
	public boolean cond(String name) {
		Object val = get(name);

		return val != null
				&& !Boolean.FALSE.equals(val)
				&& (!Coll.isCollection(val) || !U.isEmpty((Collection<?>) val))
				&& (!Coll.isMap(val) || !U.isEmpty((Map<?, ?>) val));
	}

	private Object get(String name) {
		return propOf(name, model.toArray());
	}

	private static Object propOf(String name, Object[] scope) {
		int p = name.indexOf(".");

		if (p > 0) {
			Object first = propOf(name.substring(0, p), scope);
			return propOf(name.substring(p + 1), new Object[]{first});
		}

		for (int i = scope.length - 1; i >= 0; i--) {
			Object x = scope[i];
			if (x != null) {
				if (x instanceof Map<?, ?>) {
					Map<?, ?> map = (Map<?, ?>) x;

					if (map.containsKey(name)) {
						return map.get(name);
					}

				} else if (x instanceof Getter) {
					Getter getter = (Getter) x;

					return getter.get(name);

				} else {
					Prop prop = Beany.property(x, name, false);

					if (prop != null) {
						return prop.get(x);
					}
				}
			}
		}

		return null;
	}

}
