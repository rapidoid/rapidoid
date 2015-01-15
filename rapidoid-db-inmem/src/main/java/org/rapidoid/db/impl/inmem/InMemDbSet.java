package org.rapidoid.db.impl.inmem;

/*
 * #%L
 * rapidoid-db-inmem
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.List;

import org.rapidoid.db.Database;
import org.rapidoid.db.impl.DefaultDbSet;
import org.rapidoid.inmem.EntityLinks;
import org.rapidoid.inmem.EntityLinksContainer;

import com.fasterxml.jackson.annotation.JsonValue;

public class InMemDbSet<E> extends DefaultDbSet<E> implements EntityLinksContainer {

	private final EntityLinks entityLinks = new DbEntityLinks(db, this, tracker);

	public InMemDbSet(Database db, Object holder, String relation) {
		super(db, holder, relation);
	}

	public InMemDbSet(Database db, Object holder, String relation, List<? extends Number> ids) {
		super(db, holder, relation, ids);
	}

	@Override
	public EntityLinks getEntityLinks() {
		return entityLinks;
	}

	@JsonValue
	@Override
	public Object serialized() {
		return super.serialized();
	}

}
