package org.rapidoid.cls;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.*;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface TypeSpecificVisitor<T, R> {

	R dispatch(T context, Object value);

	R processNull(T context);

	R processUnknown(T context, Object value);

	R processArray(T context, Object[] arr);

	R process(T context, boolean value);

	R process(T context, byte value);

	R process(T context, short value);

	R process(T context, char value);

	R process(T context, int value);

	R process(T context, long value);

	R process(T context, float value);

	R process(T context, double value);

	R process(T context, String value);

	R process(T context, Boolean value);

	R process(T context, Byte value);

	R process(T context, Short value);

	R process(T context, Character value);

	R process(T context, Integer value);

	R process(T context, Long value);

	R process(T context, Float value);

	R process(T context, Double value);

	R process(T context, Date value);

	R process(T context, UUID value);

	R process(T context, boolean[] arr);

	R process(T context, byte[] arr);

	R process(T context, short[] arr);

	R process(T context, char[] arr);

	R process(T context, int[] arr);

	R process(T context, long[] arr);

	R process(T context, float[] arr);

	R process(T context, double[] arr);

	R process(T context, List<?> list);

	R process(T context, Set<?> set);

	R process(T context, Map<?, ?> map);

}
