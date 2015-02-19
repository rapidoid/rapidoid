package org.rapidoid.db;

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
 * rapidoid-db
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

/**
 * Base class for persisted domain model entities.
 */
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class Entity extends EntityCommons implements IEntityCommons, CommonRoles, Serializable {

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

	public Entity() {
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
					prop.setRaw(target, DB.list(target, rel.value()));
				} else if (DbSet.class.equals(type)) {
					prop.setRaw(target, DB.set(target, rel.value()));
				} else if (DbRef.class.equals(type)) {
					prop.setRaw(target, DB.ref(target, rel.value()));
				}
			}
		}
	}

}
