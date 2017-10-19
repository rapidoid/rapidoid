package org.rapidoid.cls;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;
import org.rapidoid.u.U;
import org.rapidoid.util.Deleted;
import org.rapidoid.util.None;

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
@Since("5.1.0")
public abstract class AbstractTypeSpecificFactory<T> extends RapidoidThing implements TypeSpecificFactory<T> {

	protected Object create(T context, TypeKind kind) {
		switch (kind) {
			case NULL:
				return nullValue(context);

			case BOOLEAN:
				return booleanValue(context);

			case BYTE:
				return byteValue(context);

			case SHORT:
				return shortValue(context);

			case CHAR:
				return charValue(context);

			case INT:
				return intValue(context);

			case LONG:
				return longValue(context);

			case FLOAT:
				return floatValue(context);

			case DOUBLE:
				return doubleValue(context);

			case STRING:
				return string(context);

			case BOOLEAN_OBJ:
				return booleanValue(context);

			case BYTE_OBJ:
				return byteObj(context);

			case SHORT_OBJ:
				return shortObj(context);

			case CHAR_OBJ:
				return charObj(context);

			case INT_OBJ:
				return intObj(context);

			case LONG_OBJ:
				return longObj(context);

			case FLOAT_OBJ:
				return floatObj(context);

			case DOUBLE_OBJ:
				return doubleObj(context);

			case DATE:
				return date(context);

			case UUID:
				return uuid(context);

			case BOOLEAN_ARR:
				return booleanArr(context);

			case BYTE_ARR:
				return byteArr(context);

			case SHORT_ARR:
				return shortArr(context);

			case CHAR_ARR:
				return charArr(context);

			case INT_ARR:
				return intArr(context);

			case LONG_ARR:
				return longArr(context);

			case FLOAT_ARR:
				return floatArr(context);

			case DOUBLE_ARR:
				return doubleArr(context);

			case LIST:
				return list(context);

			case SET:
				return set(context);

			case MAP:
				return map(context);

			case OBJECT_ARR:
				return objectArr(context);

			case UNKNOWN:
				return unknown(context);

			case NONE:
				return noneValue(context);

			case DELETED:
				return deletedValue(context);

			case TUUID:
				return tuuid(context);

			default:
				throw Err.notExpected();
		}
	}

	@Override
	public Object unknown(T context) {
		throw U.rte("Cannot provide instance of unknown type!");
	}

	@Override
	public Object nullValue(T context) {
		return null;
	}

	@Override
	public None noneValue(T context) {
		return None.NONE;
	}

	@Override
	public Deleted deletedValue(T context) {
		return Deleted.DELETED;
	}

	@Override
	public Boolean booleanObj(T context) {
		return booleanValue(context);
	}

	@Override
	public Byte byteObj(T context) {
		return byteValue(context);
	}

	@Override
	public Short shortObj(T context) {
		return shortValue(context);
	}

	@Override
	public Character charObj(T context) {
		return charValue(context);
	}

	@Override
	public Integer intObj(T context) {
		return intValue(context);
	}

	@Override
	public Long longObj(T context) {
		return longValue(context);
	}

	@Override
	public Float floatObj(T context) {
		return floatValue(context);
	}

	@Override
	public Double doubleObj(T context) {
		return doubleValue(context);
	}

}
