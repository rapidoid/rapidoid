package org.rapidoid.ioc;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Manage;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.Wired;
import org.rapidoid.beany.Metadata;
import org.rapidoid.cls.Cls;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/*
 * #%L
 * rapidoid-inject
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
public class ClassMetadata extends RapidoidThing {

	final Class<?> clazz;

	final List<Field> injectableFields;

	final List<Class<?>> dependencyTypes;

	public ClassMetadata(Class<?> clazz) {
		this.clazz = clazz;
		this.injectableFields = Collections.synchronizedList(getInjectableFields(clazz));
		this.dependencyTypes = Collections.synchronizedList(getDependencyTypes(clazz));
	}

	private List<Class<?>> getDependencyTypes(Class<?> clazz) {
		List<Class<?>> dependencies = U.list();

		Manage depAnn = Metadata.getAnnotationRecursive(clazz, Manage.class);

		if (depAnn != null) {
			Collections.addAll(dependencies, depAnn.value());
		}

		return dependencies;
	}

	public static List<Field> getInjectableFields(Class<?> clazz) {
		List<Field> fields = U.list();

		fields.addAll(Cls.getFieldsAnnotated(clazz, Wired.class));
		fields.addAll(Cls.getFieldsAnnotated(clazz, Resource.class));
		fields.addAll(Cls.getFieldsAnnotated(clazz, Inject.class));

		if (Msc.hasJPA()) {
			Class<Annotation> javaxPersistenceContext = Cls.get("javax.persistence.PersistenceContext");
			List<Field> emFields = Cls.getFieldsAnnotated(clazz, javaxPersistenceContext);

			for (Field emField : emFields) {
				U.must(emField.getType().getName().equals("javax.persistence.EntityManager"), "Expected EntityManager type!");
			}

			fields.addAll(emFields);
		}

		return fields;
	}

}
