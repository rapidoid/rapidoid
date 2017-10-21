package org.rapidoid.group;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.lambda.Mapper;

import java.util.Map;
import java.util.UUID;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public abstract class AutoManageable<T extends AutoManageable> extends AbstractManageable {

	private static final Map<Class<? extends Manageable>, GroupOf<? extends Manageable>> GROUPS = Coll.autoExpandingMap(new Mapper<Class<? extends Manageable>, GroupOf<? extends Manageable>>() {
		@Override
		public GroupOf<?> map(Class<? extends Manageable> cls) throws Exception {
			return new GroupOf<>(cls);
		}
	});

	private final String id;

	public AutoManageable() {
		this(UUID.randomUUID().toString());
	}

	public AutoManageable(String id) {
		this.id = id;

		group().add(me());
	}

	@SuppressWarnings("unchecked")
	private T me() {
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public GroupOf<T> group() {
		return (GroupOf<T>) GROUPS.get(getClass());
	}

	@Override
	public String id() {
		return id;
	}

	public static void reset() {
		GROUPS.clear();
	}
}
