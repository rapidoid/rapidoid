package org.rapidoid.cls;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;
import org.rapidoid.u.U;
import org.rapidoid.util.TUUID;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public abstract class AbstractTypeSpecificVisitor<T, R> extends RapidoidThing implements TypeSpecificVisitor<T, R> {

	protected R dispatch(T context, TypeKind kind, Object value) {
		switch (kind) {

			case NULL:
				return processNull(context);

			case UNKNOWN:
				return processUnknown(context, (Object) value);

			case OBJECT_ARR:
				return processArray(context, (Object[]) value);

			case BOOLEAN:
				return process(context, ((Boolean) value).booleanValue());

			case BYTE:
				return process(context, ((Byte) value).byteValue());

			case SHORT:
				return process(context, ((Short) value).shortValue());

			case CHAR:
				return process(context, ((Character) value).charValue());

			case INT:
				return process(context, ((Integer) value).intValue());

			case LONG:
				return process(context, ((Long) value).longValue());

			case FLOAT:
				return process(context, ((Float) value).floatValue());

			case DOUBLE:
				return process(context, ((Double) value).doubleValue());

			case STRING:
				return process(context, (String) value);

			case BOOLEAN_OBJ:
				return process(context, (Boolean) value);

			case BYTE_OBJ:
				return process(context, (Byte) value);

			case SHORT_OBJ:
				return process(context, (Short) value);

			case CHAR_OBJ:
				return process(context, (Character) value);

			case INT_OBJ:
				return process(context, (Integer) value);

			case LONG_OBJ:
				return process(context, (Long) value);

			case FLOAT_OBJ:
				return process(context, (Float) value);

			case DOUBLE_OBJ:
				return process(context, (Double) value);

			case DATE:
				return process(context, (Date) value);

			case UUID:
				return process(context, (java.util.UUID) value);

			case BOOLEAN_ARR:
				return process(context, (boolean[]) value);

			case BYTE_ARR:
				return process(context, (byte[]) value);

			case SHORT_ARR:
				return process(context, (short[]) value);

			case CHAR_ARR:
				return process(context, (char[]) value);

			case INT_ARR:
				return process(context, (int[]) value);

			case LONG_ARR:
				return process(context, (long[]) value);

			case FLOAT_ARR:
				return process(context, (float[]) value);

			case DOUBLE_ARR:
				return process(context, (double[]) value);

			case LIST:
				return process(context, (List<?>) value);

			case SET:
				return process(context, (Set<?>) value);

			case MAP:
				return process(context, (Map<?, ?>) value);

			case NONE:
				return processNone(context);

			case DELETED:
				return processDeleted(context);

			case TUUID:
				return process(context, (TUUID) value);

			default:
				throw Err.notExpected();
		}
	}

	@Override
	public R dispatch(T context, Object value) {
		TypeKind kind = Cls.kindOf(value);
		return dispatch(context, kind, value);
	}

	@Override
	public R processUnknown(T context, Object value) {
		throw U.rte("Unsupported type: " + Cls.of(value).getName());
	}

	@Override
	public R process(T context, Boolean value) {
		return process(context, value.booleanValue());
	}

	@Override
	public R process(T context, Byte value) {
		return process(context, value.byteValue());
	}

	@Override
	public R process(T context, Short value) {
		return process(context, value.shortValue());
	}

	@Override
	public R process(T context, Character value) {
		return process(context, value.charValue());
	}

	@Override
	public R process(T context, Integer value) {
		return process(context, value.intValue());
	}

	@Override
	public R process(T context, Long value) {
		return process(context, value.longValue());
	}

	@Override
	public R process(T context, Float value) {
		return process(context, value.floatValue());
	}

	@Override
	public R process(T context, Double value) {
		return process(context, value.doubleValue());
	}

}
