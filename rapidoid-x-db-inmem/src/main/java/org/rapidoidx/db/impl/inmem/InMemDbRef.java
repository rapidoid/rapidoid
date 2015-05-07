package org.rapidoidx.db.impl.inmem;

/*
 * #%L
 * rapidoid-x-db-inmem
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoidx.db.Database;
import org.rapidoidx.db.impl.DefaultDbRef;
import org.rapidoidx.inmem.EntityLinks;
import org.rapidoidx.inmem.EntityLinksContainer;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class InMemDbRef<E> extends DefaultDbRef<E> implements EntityLinksContainer {

	private static final long serialVersionUID = 6459087258568217810L;

	private final EntityLinks entityLinks = new DbEntityLinks(db, this, tracker);

	public InMemDbRef(Database db, Object holder, String relation) {
		super(db, holder, relation);
	}

	public InMemDbRef(Database db, Object holder, String relation, long id) {
		super(db, holder, relation, id);
	}

	@Override
	public EntityLinks getEntityLinks() {
		return entityLinks;
	}

}
