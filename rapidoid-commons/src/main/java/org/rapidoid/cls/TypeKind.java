package org.rapidoid.cls;

import org.rapidoid.u.U;
import org.rapidoid.util.Deleted;
import org.rapidoid.util.None;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

interface TypeConstants {

	boolean PRIM = true;
	boolean OBJ = false;

	boolean NUM = true;
	boolean NAN = false;

	boolean CONCRETE = true;
	boolean UNCLEAR = false;

	boolean ARR = true;
	boolean NOT_ARR = false;
}

/**
 * !!! IMPORTANT !!!
 * <p>
 * DO NOT CHANGE THE ORDER!
 * <p>
 * ONLY APPEND NEW ELEMENTS!
 *
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public enum TypeKind implements TypeConstants {

	NULL(null, OBJ, NAN, CONCRETE, NOT_ARR),

	BOOLEAN(boolean.class, PRIM, NAN, CONCRETE, NOT_ARR),
	BYTE(byte.class, PRIM, NUM, CONCRETE, NOT_ARR),
	SHORT(short.class, PRIM, NUM, CONCRETE, NOT_ARR),
	CHAR(char.class, PRIM, NUM, CONCRETE, NOT_ARR),
	INT(int.class, PRIM, NUM, CONCRETE, NOT_ARR),
	LONG(long.class, PRIM, NUM, CONCRETE, NOT_ARR),
	FLOAT(float.class, PRIM, NUM, CONCRETE, NOT_ARR),
	DOUBLE(double.class, PRIM, NUM, CONCRETE, NOT_ARR),

	STRING(String.class, OBJ, NAN, CONCRETE, NOT_ARR),

	BOOLEAN_OBJ(Boolean.class, OBJ, NAN, CONCRETE, NOT_ARR),
	BYTE_OBJ(Byte.class, OBJ, NUM, CONCRETE, NOT_ARR),
	SHORT_OBJ(Short.class, OBJ, NUM, CONCRETE, NOT_ARR),
	CHAR_OBJ(Character.class, OBJ, NUM, CONCRETE, NOT_ARR),
	INT_OBJ(Integer.class, OBJ, NUM, CONCRETE, NOT_ARR),
	LONG_OBJ(Long.class, OBJ, NUM, CONCRETE, NOT_ARR),
	FLOAT_OBJ(Float.class, OBJ, NUM, CONCRETE, NOT_ARR),
	DOUBLE_OBJ(Double.class, OBJ, NUM, CONCRETE, NOT_ARR),

	UNKNOWN(Object.class, OBJ, NAN, UNCLEAR, NOT_ARR),
	DATE(Date.class, OBJ, NAN, CONCRETE, NOT_ARR),
	UUID(java.util.UUID.class, OBJ, NAN, CONCRETE, NOT_ARR),

	BOOLEAN_ARR(boolean[].class, OBJ, NAN, CONCRETE, ARR),
	BYTE_ARR(byte[].class, OBJ, NUM, CONCRETE, ARR),
	SHORT_ARR(short[].class, OBJ, NUM, CONCRETE, ARR),
	CHAR_ARR(char[].class, OBJ, NUM, CONCRETE, ARR),
	INT_ARR(int[].class, OBJ, NUM, CONCRETE, ARR),
	LONG_ARR(long[].class, OBJ, NUM, CONCRETE, ARR),
	FLOAT_ARR(float[].class, OBJ, NUM, CONCRETE, ARR),
	DOUBLE_ARR(double[].class, OBJ, NUM, CONCRETE, ARR),

	OBJECT_ARR(Object[].class, OBJ, NAN, UNCLEAR, ARR),
	LIST(List.class, OBJ, NAN, UNCLEAR, NOT_ARR),
	SET(Set.class, OBJ, NAN, UNCLEAR, NOT_ARR),
	MAP(Map.class, OBJ, NAN, UNCLEAR, NOT_ARR),

	NONE(None.class, OBJ, NAN, CONCRETE, NOT_ARR),
	DELETED(Deleted.class, OBJ, NAN, CONCRETE, NOT_ARR),

	TUUID(org.rapidoid.util.TUUID.class, OBJ, NAN, CONCRETE, NOT_ARR);

	private static final Map<Class<?>, TypeKind> KINDS = initKinds();

	private static Map<Class<?>, TypeKind> initKinds() {
		Map<Class<?>, TypeKind> kinds = U.map();

		for (TypeKind kind : TypeKind.values()) {
			if (kind != NULL) {
				kinds.put(kind.getType(), kind);
			}
		}

		return kinds;
	}

	/**
	 * @return Any kind, except NULL
	 */
	public static TypeKind ofType(Class<?> type) {

		if (List.class.isAssignableFrom(type)) {
			return LIST;

		} else if (Set.class.isAssignableFrom(type)) {
			return SET;

		} else if (Map.class.isAssignableFrom(type)) {
			return MAP;

		} else {
			TypeKind kind = KINDS.get(type);

			if (kind == null) {
				kind = type.isArray() ? TypeKind.OBJECT_ARR : TypeKind.UNKNOWN;
			}

			return kind;
		}
	}

	/**
	 * @return Any kind, including NULL
	 */
	public static TypeKind of(Object value) {
		return value != null ? ofType(value.getClass()) : TypeKind.NULL;
	}

	private final Class<?> type;
	private final boolean primitive;
	private final boolean number;
	private final boolean concrete;
	private final boolean array;

	TypeKind(Class<?> type, boolean primitive, boolean number, boolean concrete, boolean array) {
		this.type = type;
		this.primitive = primitive;
		this.number = number;
		this.concrete = concrete;
		this.array = array;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean isPrimitive() {
		return primitive;
	}

	public boolean isNumber() {
		return number;
	}

	public boolean isConcrete() {
		return concrete;
	}

	public boolean isArray() {
		return array;
	}

}
