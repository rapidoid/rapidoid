package org.rapidoid.ioc;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.ScanPackages;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Metadata;
import org.rapidoid.cls.Cls;
import org.rapidoid.u.U;
import org.rapidoid.util.MscOpts;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/*
 * #%L
 * rapidoid-inject
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
@Since("5.1.0")
public class ClassMetadata extends RapidoidThing {

	@SuppressWarnings("unchecked")
	public static final List<Class<? extends Annotation>> INJECTION_ANNOTATIONS = U.list(Wired.class, Resource.class, Inject.class);

	public final Class<?> clazz;

	public final Set<Field> injectableFields;

	public final Set<Constructor<?>> injectableConstructors;

	public final Constructor<?> defaultConstructor;

	public final Set<Class<?>> typesToManage;

	public final Set<String> packagesToScan;

	public ClassMetadata(Class<?> clazz) {
		this.clazz = clazz;
		this.injectableFields = Collections.synchronizedSet(getInjectableFields(clazz));
		this.injectableConstructors = Collections.synchronizedSet(getInjectableConstructors(clazz));
		this.defaultConstructor = getDefaultConstructor(clazz);
		this.typesToManage = Collections.synchronizedSet(getTypesToManage(clazz));
		this.packagesToScan = Collections.synchronizedSet(getPackagesToScan(clazz));
	}

	public static Set<Class<?>> getTypesToManage(Class<?> clazz) {
		Set<Class<?>> types = U.set();

		Manage depAnn = Metadata.getAnnotationRecursive(clazz, Manage.class);

		if (depAnn != null) {
			Collections.addAll(types, depAnn.value());
		}

		return types;
	}

	private Set<String> getPackagesToScan(Class<?> clazz) {
		ScanPackages scan = Metadata.getAnnotationRecursive(clazz, ScanPackages.class);

		if (scan != null) {
			String[] pkgs = scan.value();
			U.must(U.notEmpty(pkgs), "@ScanPackages requires a list of packages to scan!");
			return U.set(pkgs);

		} else {
			return U.set();
		}
	}

	public static Set<Field> getInjectableFields(Class<?> clazz) {
		Set<Field> fields = U.set();

		for (Class<? extends Annotation> annotation : INJECTION_ANNOTATIONS) {
			fields.addAll(Cls.getFieldsAnnotated(clazz, annotation));
		}

		if (MscOpts.hasJPA()) {
			Class<Annotation> javaxPersistenceContext = Cls.get("javax.persistence.PersistenceContext");
			List<Field> emFields = Cls.getFieldsAnnotated(clazz, javaxPersistenceContext);

			for (Field emField : emFields) {
				U.must(emField.getType().getName().equals("javax.persistence.EntityManager"), "Expected EntityManager type!");
			}

			fields.addAll(emFields);
		}

		return fields;
	}

	public static Set<Constructor<?>> getInjectableConstructors(Class<?> clazz) {
		Set<Constructor<?>> constructors = U.set();

		for (Constructor<?> constr : clazz.getDeclaredConstructors()) {
			if (Metadata.hasAny(constr.getAnnotations(), ClassMetadata.INJECTION_ANNOTATIONS)) {
				constructors.add(constr);
			}
		}

		return constructors;
	}

	public static Constructor<?> getDefaultConstructor(Class<?> clazz) {
		try {
			return clazz.getDeclaredConstructor();
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

}
