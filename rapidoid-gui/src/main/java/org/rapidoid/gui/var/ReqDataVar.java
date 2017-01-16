package org.rapidoid.gui.var;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Err;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.u.U;

import java.util.Collection;
import java.util.List;

/*
 * #%L
 * rapidoid-gui
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
public class ReqDataVar<T> extends WidgetVar<T> {

	private static final long serialVersionUID = 2761159925375675659L;

	private final String localKey;

	private final Class<T> type;

	private final T defaultValue;

	public ReqDataVar(String localKey, Class<T> type, T defaultValue) {
		super(localKey);
		this.localKey = localKey;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get() {
		Object value = U.or(ReqInfo.get().data().get(localKey), null);

		if (type.equals(List.class)) {

			if (value == null) {
				return defaultValue;

			} else if (value instanceof String) {
				String s = (String) value;

				if (U.notEmpty(s)) {
					return (T) U.list(s);
				} else {
					return defaultValue;
				}

			} else if (value instanceof Collection) {
				Collection coll = (Collection) value;
				return (T) U.list(coll);

			} else {
				throw Err.notExpected();
			}

		} else {
			value = U.or(value, defaultValue);
			return Cls.convert(value, type);
		}
	}

	@Override
	public void doSet(T value) {
	}

}
