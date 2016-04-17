package org.rapidoid.gui.var;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.AnyObj;
import org.rapidoid.u.U;
import org.rapidoid.var.Var;

import java.util.Collection;

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
@Since("2.0.0")
public class CollectionContainerVar extends WidgetVar<Boolean> {

	private static final long serialVersionUID = 6990464844550633598L;

	private final Var<Collection<Object>> container;

	private final Object item;

	public CollectionContainerVar(String name, Var<Collection<Object>> container, Object item, boolean initial) {
		super(name, initial);
		this.container = container;
		this.item = item;
		init();
	}

	private void init() {
		if (!initial) {
			if (container.get() == null) {
				container.set(U.list());
			}
			set(getBool());
		}
	}

	@Override
	public Boolean get() {
		return AnyObj.contains(container.get(), item);
	}

	@Override
	public void doSet(Boolean value) {
		Collection<Object> coll = container.get();

		if (value) {
			if (coll != null) {
				if (!coll.contains(item)) {
					coll.add(item);
				}
			} else {
				coll = U.list(item);
			}
		} else {
			if (coll != null) {
				coll.remove(item);
			}
		}

		container.set(coll);
	}

}
