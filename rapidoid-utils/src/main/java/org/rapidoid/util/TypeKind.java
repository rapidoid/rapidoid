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

/**
 * !!! IMPORTANT !!!
 * 
 * DO NOT CHANGE THE ORDER!
 * 
 * ONLY APPEND NEW ELEMENTS!
 */

public enum TypeKind implements Constants {

	NULL(F, F, T), BOOLEAN(T, F, T), BYTE(T, T, T), SHORT(T, T, T), CHAR(T, T, T), INT(T, T, T), LONG(T, T, T), FLOAT(
			T, T, T), DOUBLE(T, T, T), STRING(F, F, T), BOOLEAN_OBJ(F, F, T), BYTE_OBJ(F, T, T), SHORT_OBJ(F, T, T), CHAR_OBJ(
			F, T, T), INT_OBJ(F, T, T), LONG_OBJ(F, T, T), FLOAT_OBJ(F, T, T), DOUBLE_OBJ(F, T, T), OBJECT(F, F, F), DATE(
			F, F, T);

	private final boolean primitive;

	private final boolean number;

	private final boolean simple;

	private TypeKind(boolean primitive, boolean number, boolean simple) {
		this.primitive = primitive;
		this.number = number;
		this.simple = simple;
	}

	public boolean isPrimitive() {
		return primitive;
	}

	public boolean isNumber() {
		return number;
	}

	public boolean isSimple() {
		return simple;
	}

}
