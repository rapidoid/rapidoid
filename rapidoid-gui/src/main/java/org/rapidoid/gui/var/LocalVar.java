package org.rapidoid.gui.var;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.u.U;

import java.io.Serializable;

/*
 * #%L
 * rapidoid-gui
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
@Since("4.0.0")
public class LocalVar<T extends Serializable> extends WidgetVar<T> {

	private static final long serialVersionUID = 2761159925375675659L;

	private final String localKey;

	private final T defaultValue;

	public LocalVar(String localKey, T defaultValue) {
		super(localKey);
		this.localKey = localKey;
		this.defaultValue = defaultValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get() {
		return (T) Cls.convert(U.or(ReqInfo.get().posted().get(localKey), defaultValue), Cls.of(defaultValue));
	}

	@Override
	public void doSet(T value) {
	}

}
