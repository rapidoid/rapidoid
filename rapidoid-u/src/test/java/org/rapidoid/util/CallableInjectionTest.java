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

import java.util.concurrent.Callable;

import javax.annotation.Resource;

import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

class MyCallable implements Callable<String> {

	@Resource
	Foo foo;

	@Override
	public String call() throws Exception {
		return "abc";
	}
}

class Foo {
	@Resource
	Callable<String> callable;
}

public class CallableInjectionTest extends TestCommons {

	@Test
	public void shouldInject() throws Exception {
		U.manage(MyCallable.class);

		Foo foo = U.inject(Foo.class);

		notNullAll(foo, foo.callable);
		hasType(foo.callable, MyCallable.class);

		MyCallable myCallable = (MyCallable) foo.callable;
		notNull(myCallable.foo);

		eq(myCallable.foo, foo);

		eq(foo.callable.call(), "abc");
	}

}
