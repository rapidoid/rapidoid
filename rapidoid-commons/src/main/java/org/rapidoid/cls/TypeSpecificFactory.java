package org.rapidoid.cls;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.*;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface TypeSpecificFactory<T> {

	Object create(T context);

	Object unknown(T context);

	Object nullValue(T context);

	Object[] objectArr(T context);

	Object objectValue(T context);

	boolean booleanValue(T context);

	byte byteValue(T context);

	short shortValue(T context);

	char charValue(T context);

	int intValue(T context);

	long longValue(T context);

	float floatValue(T context);

	double doubleValue(T context);

	String string(T context);

	Boolean booleanObj(T context);

	Byte byteObj(T context);

	Short shortObj(T context);

	Character charObj(T context);

	Integer intObj(T context);

	Long longObj(T context);

	Float floatObj(T context);

	Double doubleObj(T context);

	Date date(T context);

	UUID uuid(T context);

	boolean[] booleanArr(T context);

	byte[] byteArr(T context);

	short[] shortArr(T context);

	char[] charArr(T context);

	int[] intArr(T context);

	long[] longArr(T context);

	float[] floatArr(T context);

	double[] doubleArr(T context);

	List<?> list(T context);

	Set<?> set(T context);

	Map<?, ?> map(T context);

}
