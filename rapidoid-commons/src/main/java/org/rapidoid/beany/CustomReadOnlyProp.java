package org.rapidoid.beany;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.u.U;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

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

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public abstract class CustomReadOnlyProp extends RapidoidThing implements Prop {

	@Override
	public <T> T get(Object target) {
		return getRaw(target);
	}

	@Override
	public void setRaw(Object target, Object value) {
		cantChange();
	}

	@Override
	public void set(Object target, Object value) {
		cantChange();
	}

	@Override
	public void reset(Object target) {
		cantChange();
	}

	private void cantChange() {
		throw U.rte("This is a read-only property!");
	}

	@Override
	public String getName() {
		return "Actions";
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public Class<?> getType() {
		return Object.class;
	}

	@Override
	public TypeKind getTypeKind() {
		return TypeKind.UNKNOWN;
	}

	@Override
	public ParameterizedType getGenericType() {
		return null;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		return null;
	}

	@Override
	public int getTypeArgsCount() {
		return 0;
	}

	@Override
	public Class<?> getTypeArg(int index) {
		throw U.rte("No type args available!");
	}

	@SuppressWarnings("unchecked")
	private static Map<Object, Object> map(Object target) {
		return (Map<Object, Object>) target;
	}

	@Override
	public Class<?> getRawType() {
		return getType();
	}

	@Override
	public TypeKind getRawTypeKind() {
		return getTypeKind();
	}

	@Override
	public ParameterizedType getRawGenericType() {
		return null;
	}

	@Override
	public int getRawTypeArgsCount() {
		return 0;
	}

	@Override
	public Class<?> getRawTypeArg(int index) {
		throw U.rte("No type args available!");
	}

	@Override
	public Class<?> getDeclaringType() {
		return null;
	}

	@Override
	public Object getFast(Object target) {
		return getRaw(target);
	}

}
