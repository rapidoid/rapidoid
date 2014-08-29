package org.rapidoid.util;

/*
 * #%L
 * rapidoid-u
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

public class UTest extends TestCommons {

	@Test
	public void testPropertiesOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testKindOfClassOfQ() {
		fail("Not yet implemented");
	}

	@Test
	public void testKindOfObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testTraceString() {
		fail("Not yet implemented");
	}

	@Test
	public void testTraceStringStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testTraceStringStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testTraceStringStringObjectStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testDebugString() {
		fail("Not yet implemented");
	}

	@Test
	public void testDebugStringStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testDebugStringStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testDebugStringStringObjectStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testInfoString() {
		fail("Not yet implemented");
	}

	@Test
	public void testInfoStringStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testInfoStringStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testInfoStringStringObjectStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testWarnString() {
		fail("Not yet implemented");
	}

	@Test
	public void testWarnStringStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testWarnStringStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testWarnStringStringObjectStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testWarnStringThrowable() {
		fail("Not yet implemented");
	}

	@Test
	public void testErrorString() {
		fail("Not yet implemented");
	}

	@Test
	public void testErrorStringStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testErrorStringStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testErrorStringStringObjectStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testErrorStringThrowable() {
		fail("Not yet implemented");
	}

	@Test
	public void testSevereString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSevereStringStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testSevereStringStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testSevereStringStringObjectStringObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testSevereStringThrowable() {
		fail("Not yet implemented");
	}

	@Test
	public void testSleep() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitInterruption() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitForObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetFieldValueObjectStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetFieldValueFieldObjectObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFieldValueObjectString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFieldValueFieldObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFields() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFieldsAnnotated() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMethod() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindMethod() {
		fail("Not yet implemented");
	}

	@Test
	public void testInvokeStatic() {
		fail("Not yet implemented");
	}

	@Test
	public void testInvoke() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetImplementedInterfaces() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetConstructor() {
		fail("Not yet implemented");
	}

	@Test
	public void testAnnotatedMethod() {
		fail("Not yet implemented");
	}

	@Test
	public void testTextCollectionOfObject() {
		eq(U.text(new ArrayList<Integer>()), "[]");

		List<String> lst = new ArrayList<String>();

		lst.add("java");
		lst.add("c");
		lst.add("c++");

		eq(U.text(lst), "[java, c, c++]");
	}

	@Test
	public void testTextObject() {
		eq(U.text((Object) null), "null");

		eq(U.text(123), "123");
		eq(U.text(1.23), "1.23");

		eq(U.text(true), "true");
		eq(U.text(false), "false");

		eq(U.text(""), "");
		eq(U.text("abc"), "abc");

		eq(U.text(new byte[] { -50, 0, 9 }), "[-50, 0, 9]");
		eq(U.text(new short[] { -500, 0, 9 }), "[-500, 0, 9]");
		eq(U.text(new int[] { 300000000, 70, 100 }), "[300000000, 70, 100]");
		eq(U.text(new long[] { 3000000000000000000L, 1, -8900000000000000000L }),
				"[3000000000000000000, 1, -8900000000000000000]");

		eq(U.text(new float[] { -30.40000000f, -1.587f, 89.3f }), "[-30.4, -1.587, 89.3]");
		eq(U.text(new double[] { -9987.1, -1.5, 8.3 }), "[-9987.1, -1.5, 8.3]");

		eq(U.text(new boolean[] { true }), "[true]");

		eq(U.text(new char[] { 'k', 'o', 'h' }), "[k, o, h]");
		eq(U.text(new char[] { '-', '.', '+' }), "[-, ., +]");
	}

	@Test
	public void testTextObjectArray() {
		eq(U.text(new Object[] {}), "[]");
		eq(U.text(new Object[] { 1, new boolean[] { true, false }, 3 }), "[1, [true, false], 3]");
		eq(U.text(new Object[] { new double[] { -9987.1 }, new char[] { 'a', '.' }, new int[] { 300, 70, 100 } }),
				"[[-9987.1], [a, .], [300, 70, 100]]");

		eq(U.text(new int[][] { { 1, 2 }, { 3, 4, 5 } }), "[[1, 2], [3, 4, 5]]");

		eq(U.text(new String[][][] { { { "a" }, { "r" } }, { { "m" } } }), "[[[a], [r]], [[m]]]");
	}

	@Test
	public void testTextIteratorOfQ() {
		fail("Not yet implemented");
	}

	@Test
	public void testTextln() {
		fail("Not yet implemented");
	}

	@Test
	public void testReplaceText() {
		fail("Not yet implemented");
	}

	@Test
	public void testJoinObjectArrayString() {
		fail("Not yet implemented");
	}

	@Test
	public void testJoinIterableOfQString() {
		fail("Not yet implemented");
	}

	@Test
	public void testRenderObjectArrayStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testRenderIterableOfQStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testResource() {
		fail("Not yet implemented");
	}

	@Test
	public void testTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testXor() {
		eq(U.xor(true, true), false);
		eq(U.xor(true, false), true);
		eq(U.xor(false, true), true);
		eq(U.xor(false, false), true);
	}

	@Test
	public void testEq() {
		isTrue(U.eq("2", "2"));
		isFalse(U.eq("2", "3"));
		isTrue(U.eq("2", "2"));
		isFalse(U.eq("a", "b"));
		isFalse(U.eq('a', 'b'));

		isFalse(U.eq(null, 'b'));
		isFalse(U.eq('a', null));
		isTrue(U.eq(null, null));
	}

	@Test
	public void testFailIf() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoad() {
		fail("Not yet implemented");
	}

	@Test
	public void testSave() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testExpandTArrayInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testExpandTArrayT() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubarray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSet() {
		Set<Integer> set = U.set(1, 3, 5, 8);

		eq((set.size()), 4);

		isTrue(set.contains(1));
		isTrue(set.contains(3));
		isTrue(set.contains(5));
		isTrue(set.contains(8));
	}

	@Test
	public void testList() {
		List<String> list = U.list("m", "k", "l");

		eq((list.size()), 3);

		eq((list.get(0)), "m");
		eq((list.get(1)), "k");
		eq((list.get(2)), "l");
	}

	@Test
	public void testMap() {
		Map<String, Integer> map = U.map();

		isTrue((map.isEmpty()));
	}

	@Test
	public void testMapKV() {
		fail("Not yet implemented");
	}

	@Test
	public void testMapKVKV() {
		fail("Not yet implemented");
	}

	@Test
	public void testMapKVKVKV() {
		fail("Not yet implemented");
	}

	@Test
	public void testMapKVKVKVKV() {
		fail("Not yet implemented");
	}

	@Test
	public void testAutoExpandingMapF1OfVK() {
		fail("Not yet implemented");
	}

	@Test
	public void testAutoExpandingMapClassOfV() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitForAtomicBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitForAtomicIntegerInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testSerializeObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeserializeByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSerializeObjectByteBuffer() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeserializeByteBuffer() {
		fail("Not yet implemented");
	}

	@Test
	public void testEncode() {
		fail("Not yet implemented");
	}

	@Test
	public void testDecode() {
		fail("Not yet implemented");
	}

	@Test
	public void testExpandByteBufferInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testExpandByteBuffer() {
		fail("Not yet implemented");
	}

	@Test
	public void testBuf2str() {
		fail("Not yet implemented");
	}

	@Test
	public void testBuf() {
		fail("Not yet implemented");
	}

	@Test
	public void testCopyNtimes() {
		fail("Not yet implemented");
	}

	@Test
	public void testRteStringThrowable() {
		fail("Not yet implemented");
	}

	@Test
	public void testRteThrowable() {
		fail("Not yet implemented");
	}

	@Test
	public void testRteStringObjectArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testRteStringThrowableObjectArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testEnsureBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testEnsureBooleanString() {
		fail("Not yet implemented");
	}

	@Test
	public void testEnsureBooleanStringLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testEnsureBooleanStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testEnsureBooleanStringObjectObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotNullObjectArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotNullObjectString() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotReady() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotSupported() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotExpected() {
		fail("Not yet implemented");
	}

	@Test
	public void testStackTraceOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testBenchmark() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCpuMemStats() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateProxy() {
		fail("Not yet implemented");
	}

	@Test
	public void testImplementInterfacesObjectInvocationHandlerClassOfQArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testImplementInterfacesObjectInvocationHandler() {
		fail("Not yet implemented");
	}

	@Test
	public void testTracer() {
		fail("Not yet implemented");
	}

	@Test
	public void testShow() {
		fail("Not yet implemented");
	}

	@Test
	public void testRteClassOfT() {
		fail("Not yet implemented");
	}

	@Test
	public void testRteClassOfTString() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewInstanceClassOfT() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewInstanceClassOfTObjectArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testInitNewInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testInit() {
		fail("Not yet implemented");
	}

	@Test
	public void testOr() {
		fail("Not yet implemented");
	}

	@Test
	public void testSchedule() {
		fail("Not yet implemented");
	}

	@Test
	public void testStartMeasure() {
		fail("Not yet implemented");
	}

	@Test
	public void testEndMeasure() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrint() {
		fail("Not yet implemented");
	}

	@Test
	public void testSingleton() {
		fail("Not yet implemented");
	}

	@Test
	public void testArgs() {
		fail("Not yet implemented");
	}

	@Test
	public void testHasOption() {
		fail("Not yet implemented");
	}

	@Test
	public void testOptionStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testOptionStringLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testOptionStringDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsEmpty() {
		eq(U.isEmpty(""), true);
		eq(U.isEmpty("a"), false);
		eq(U.isEmpty(null), true);
	}

	@Test
	public void testConnect() {
		fail("Not yet implemented");
	}

	@Test
	public void testListenIntF2OfVoidBufferedReaderDataOutputStream() {
		fail("Not yet implemented");
	}

	@Test
	public void testListenStringIntF2OfVoidBufferedReaderDataOutputStream() {
		fail("Not yet implemented");
	}

	@Test
	public void testMicroHttpServer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetId() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIds() {
		fail("Not yet implemented");
	}

	@Test
	public void testReplace() {
		fail("Not yet implemented");
	}

	@Test
	public void testManage() {
		fail("Not yet implemented");
	}

	@Test
	public void testInject() {

		fail("Not yet implemented");
	}

	@Test
	public void testInitialize() {
		fail("Not yet implemented");
	}

	@Test
	public void testInstantiate() {
		fail("Not yet implemented");
	}

	@Test
	public void testAutowire() {
		fail("Not yet implemented");
	}

	@Test
	public void testWireT() {
		fail("Not yet implemented");
	}

	@Test
	public void testWireTObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testWireClassOfT() {
		fail("Not yet implemented");
	}

	@Test
	public void testWireClassOfTObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testConvert() {
		fail("Not yet implemented");
	}

}
