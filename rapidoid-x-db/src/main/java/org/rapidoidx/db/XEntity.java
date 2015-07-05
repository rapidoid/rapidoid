package org.rapidoidx.db;

import java.io.Serializable;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Rel;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.beany.PropertyFilter;
import org.rapidoid.util.CommonRoles;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-x-db
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

/**
 * Base class for persisted domain model entities.
 */
@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public abstract class XEntity extends AbstractRichEntity implements CommonRoles, Serializable {

	private static final long serialVersionUID = 8414835674684110203L;

	@SuppressWarnings("serial")
	private static final PropertyFilter ANNOTATED_DB_REL_PROPS = new PropertyFilter() {
		@Override
		public boolean eval(Prop prop) throws Exception {
			Class<?> type = prop.getType();

			if (prop.getAnnotation(Rel.class) == null) {
				return false;
			}

			return DbList.class.isAssignableFrom(type) || DbSet.class.isAssignableFrom(type)
					|| DbRef.class.isAssignableFrom(type);
		}
	};

	public XEntity() {
		initRelations(this);
	}

	@Override
	public String toString() {
		return Beany.beanToStr(this, false);
	}

	public static void initRelations(Object target) {
		for (Prop prop : Beany.propertiesOf(target.getClass()).select(ANNOTATED_DB_REL_PROPS)) {

			Rel rel = prop.getAnnotation(Rel.class);
			U.must(!U.isEmpty(rel.value()), "Relation name must be specified!");

			Object value = prop.getRaw(target);

			if (value == null && !prop.isReadOnly()) {
				Class<?> type = prop.getType();
				if (DbList.class.equals(type)) {
					prop.setRaw(target, XDB.list(target, rel.value()));
				} else if (DbSet.class.equals(type)) {
					prop.setRaw(target, XDB.set(target, rel.value()));
				} else if (DbRef.class.equals(type)) {
					prop.setRaw(target, XDB.ref(target, rel.value()));
				}
			}
		}
	}

}
