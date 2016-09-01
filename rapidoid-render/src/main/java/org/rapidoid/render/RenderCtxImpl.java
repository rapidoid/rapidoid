package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Str;
import org.rapidoid.render.retriever.GenericValueRetriever;
import org.rapidoid.render.retriever.ValueRetriever;
import org.rapidoid.u.U;
import org.rapidoid.util.StreamUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
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

	private final List<Object> model = U.list();

	private volatile OutputStream out;
	private volatile String ext;
	private volatile TemplateFactory factory;

	@Override
	public void printAscii(String s) throws IOException {
		StreamUtils.writeAscii(out, s);
	}

	@Override
	public void printUTF8(String s) throws IOException {
		StreamUtils.writeUTF8(out, s);
	}

	@Override
	public void printValue(Object value, boolean escape) throws IOException {
		if (!escape) {
			printUTF8(U.str(value));
			return;
		}

		if (value instanceof String) {
			String s = (String) value;
			StreamUtils.writeUTF8HtmlEscaped(out, s);
			return;
		}

		if (value instanceof Number) {

			if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) {
				long n = ((Number) value).longValue();
				StreamUtils.putNumAsText(out, n);
				return;
			}

			printAscii(value.toString());
			return;
		}

		if (value == null) {
			printAscii("null");
			return;
		}

		StreamUtils.writeUTF8HtmlEscaped(out, U.str(value));
	}

	@Override
	public List iter(ValueRetriever retriever) {
		Object val = retriever.retrieve(model);

		if (val instanceof List<?>) {
			return ((List) val);

		} else if (val instanceof Object[]) {
			return U.list((Object[]) val);

		} else if (val instanceof Iterable<?>) {
			return U.list((Iterable<?>) val);

		} else {
			return val != null && !Boolean.FALSE.equals(val) ? U.list(val) : Collections.emptyList();
		}
	}

	@Override
	public void val(ValueRetriever retriever, boolean escape) throws IOException {
		valOr(retriever, "N/A", escape);
	}

	@Override
	public void valOr(ValueRetriever retriever, String or, boolean escape) throws IOException {
		Object val = retriever.retrieve(model);
		val = U.or(val, or);
		printValue(val, escape);
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
		RapidoidTemplate template = (RapidoidTemplate) factory.load(name + ext);
		template.renderInContext(this);
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
		return GenericValueRetriever.propOf(name, model);
	}

	public RenderCtxImpl out(OutputStream out) {
		this.out = out;
		return this;
	}

	public RenderCtxImpl multiModel(List<Object> model) {
		Coll.assign(this.model, model);
		return this;
	}

	public RenderCtxImpl model(Object model) {
		this.model.clear();
		this.model.add(model);
		return this;
	}

	public RenderCtxImpl factory(TemplateFactory factory) {
		this.factory = factory;
		return this;
	}

	private String calcFileExt(String filename) {
		if (U.notEmpty(filename)) {
			String fileExt = Str.cutFromFirst(filename, ".");
			return fileExt != null ? "." + fileExt : "";
		} else {
			return "";
		}
	}

	public RenderCtxImpl filename(String filename) {
		this.ext = calcFileExt(filename);
		return this;
	}

	public void reset() {
		this.model.clear();
	}

}
