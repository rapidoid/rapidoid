package org.rapidoid.reactive.numList;

import org.junit.Test;
import org.rapidoid.reactive.Bool;
import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.NumList;
import org.rapidoid.reactive.num.SimpleNum;
import org.rapidoid.test.TestCommons;

public class NumListTest extends TestCommons {

	@Test
	public void testNumList() {

		Num v0 = new SimpleNum(0);
		Num v1 = new SimpleNum(1);
		Num v2 = new SimpleNum(2);
		Num v3 = new SimpleNum(3);
		Num v4 = new SimpleNum(4);

		// [1, 2, 3]
		NumList list = new NumListImpl(v1, v2, v3);

		Num second = list.get(v1); // second element
		Num third = list.get(v2); // third element

		eq(second.get(), 2);
		eq(third.get(), 3);

		// [4, 1, 2, 3]
		list.insert(0, v4);

		Num index0 = list.get(v0);
		Num index1 = list.get(v1);
		Num index2 = list.get(v2);
		Num index3 = list.get(v3);

		eq(index0.get(), 4);
		eq(index1.get(), 1);
		eq(index2.get(), 2);
		eq(index3.get(), 3);

		eq(second.get(), 1);
		eq(third.get(), 2);

		Num size = list.size();
		eq(size.get(), 4);

		Bool empty = list.isEmpty();
		eq(empty.get(), false);

		Bool containsV1 = list.contains(v1);
		eq(containsV1.get(), true);

		list.add(v0);

		Bool containsV0 = list.contains(v0);

		eq(size.get(), 5);
		eq(containsV0.get(), true);

		list.remove(v0);

		eq(size.get(), 4);
		eq(containsV0.get(), false);

		list.removeAll(v1, v2, v3);

		eq(size.get(), 1);

		list.addAll(v1, v2, v3);

		eq(size.get(), 4);

	}

}
