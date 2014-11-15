package org.rapidoid.util;

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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Prop {

	private String name;

	private Field field;

	private Method getter;

	private Method setter;

	private Class<?> type;

	private TypeKind typeKind;

	private Object defaultValue;

	private boolean readOnly = true;

	public void setGetter(Method getter) {
		this.getter = getter;
	}

	public void setSetter(Method setter) {
		this.setter = setter;
	}

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Object obj) {
		try {
			if (field != null) {
				field.setAccessible(true);
				return (T) field.get(obj);
			} else {
				getter.setAccessible(true);
				return (T) getter.invoke(obj);
			}
		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Object obj, T defaultValue) {
		try {
			if (field != null) {
				field.setAccessible(true);
				return (T) field.get(obj);
			} else {
				getter.setAccessible(true);
				return (T) getter.invoke(obj);
			}
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public void set(Object obj, Object value) {
		try {
			if (field != null) {
				field.setAccessible(true);
				field.set(obj, convert(value, getType()));
			} else {
				setter.setAccessible(true);
				setter.invoke(obj, convert(value, getType()));
			}
		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	private Object convert(Object value, Class<?> toType) {
		if (value == null || toType.isAssignableFrom(value.getClass())) {
			return value;
		}

		if (toType.equals(String.class)) {
			return String.valueOf(value);
		}

		if (value instanceof String) {
			return Cls.convert((String) value, toType);
		}

		return value;
	}

	public Class<?> getType() {
		if (type == null) {
			// TODO: improve inference from getter and setter
			type = field != null ? field.getType() : getter.getReturnType();
		}
		return type;
	}

	public TypeKind getTypeKind() {
		if (typeKind == null) {
			typeKind = Cls.kindOf(getType());
		}

		return typeKind;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

}
