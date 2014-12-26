package org.rapidoid.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

public class Metadata {

	public static Map<Class<?>, Annotation> classAnnotations(Class<?> clazz) {
		Map<Class<?>, Annotation> annotations = U.map();

		for (Annotation ann : clazz.getAnnotations()) {
			annotations.put(ann.annotationType(), ann);
		}

		return annotations;
	}

	public static Map<Class<?>, Annotation> fieldAnnotations(Class<?> clazz, String fieldName) {

		Field field = Cls.getField(clazz, fieldName);
		Map<Class<?>, Annotation> annotations = U.map();

		for (Annotation ann : field.getDeclaringClass().getAnnotations()) {
			annotations.put(ann.annotationType(), ann);
		}

		for (Annotation ann : field.getAnnotations()) {
			annotations.put(ann.annotationType(), ann);
		}

		return annotations;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T fieldAnnotation(Class<?> clazz, String fieldName, Class<T> annotationClass) {
		return (T) fieldAnnotations(clazz, fieldName).get(annotationClass);
	}

}
