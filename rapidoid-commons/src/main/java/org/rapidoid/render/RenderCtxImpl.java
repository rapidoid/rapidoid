package org.rapidoid.render;

/*
 * #%L
 * rapidoid-commons
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RenderCtxImpl implements RenderCtx {

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
			return val != null && val != Boolean.FALSE ? U.array(val) : U.array();
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
		if (v != Boolean.TRUE) model.add(v);
	}

	@Override
	public void pop(int index, Object v) {
		if (v != Boolean.TRUE) {
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
				&& val != Boolean.FALSE
				&& (!Coll.isCollection(val) || !U.isEmpty((Collection<?>) val))
				&& (!Coll.isMap(val) || !U.isEmpty((Map<?, ?>) val));
	}

	private Object get(String name) {
		int p = name.indexOf(".");

		if (p > 0) {
			Object first = get(name.substring(0, p));
			return propOf(name.substring(p + 1), first);
		}

		for (int i = model.size() - 1; i >= 0; i--) {
			Object val = propOf(name, model.get(i));
			if (val != null) return val;
		}

		return null;
	}

	private Object propOf(String name, Object model) {
		if (model instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) model;
			Object val = map.get(name);
			if (val != null) {
				return val;
			}

		} else {
			Prop prop = Beany.property(model, name, false);

			if (prop != null) {
				return prop.get(model);
			}
		}
		return null;
	}

}
