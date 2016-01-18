package org.rapidoid.cls;

/*
 * #%L
 * rapidoid-commons
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

/**
 * !!! IMPORTANT !!!
 * 
 * DO NOT CHANGE THE ORDER!
 * 
 * ONLY APPEND NEW ELEMENTS!
 */

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public enum TypeKind {

	NULL(false, false, true), BOOLEAN(true, false, true), BYTE(true, true, true), SHORT(true, true, true), CHAR(true,
			true, true), INT(true, true, true), LONG(true, true, true), FLOAT(true, true, true), DOUBLE(true, true,
			true), STRING(false, false, true), BOOLEAN_OBJ(false, false, true), BYTE_OBJ(false, true, true), SHORT_OBJ(
			false, true, true), CHAR_OBJ(false, true, true), INT_OBJ(false, true, true), LONG_OBJ(false, true, true), FLOAT_OBJ(
			false, true, true), DOUBLE_OBJ(false, true, true), OBJECT(false, false, false), DATE(false, false, true), UUID(
			false, false, true);

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
