package org.rapidoidx.db.impl;

/*
 * #%L
 * rapidoid-x-db-impl
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
import org.rapidoid.beany.Prop;
import org.rapidoid.beany.PropertyFilter;
import org.rapidoidx.db.DbList;
import org.rapidoidx.db.DbRef;
import org.rapidoidx.db.DbSet;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbHelper {

	@SuppressWarnings("serial")
	public static final PropertyFilter DB_REL_PROPS = new PropertyFilter() {
		@Override
		public boolean eval(Prop prop) throws Exception {
			Class<?> type = prop.getType();
			return DbList.class.isAssignableFrom(type) || DbSet.class.isAssignableFrom(type)
					|| DbRef.class.isAssignableFrom(type);
		}
	};

}
