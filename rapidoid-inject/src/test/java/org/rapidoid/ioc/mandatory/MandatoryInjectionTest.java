package org.rapidoid.ioc.mandatory;

/*
 * #%L
 * rapidoid-inject
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.ioc.Wired;
import org.rapidoid.annotation.Since;
import org.rapidoid.ioc.AbstractInjectTest;
import org.rapidoid.ioc.IoC;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class MandatoryInjectionTest extends AbstractInjectTest {

	private static final String ERR_MSG = "Didn't find a value for type 'interface java.lang.Runnable' and name 'runnable'!";

	@Test
	public void testMandatoryInjection() {
		try {
			IoC.singleton(Foo.class);
		} catch (RuntimeException e) {
			eq(e.getMessage(), ERR_MSG);
			verifyIoC();
			return;
		}

		expectedException();
	}

	@Test
	public void testMandatoryInjection2() {
		Foo foo = new Foo();

		try {
			IoC.autowire(foo);
		} catch (RuntimeException e) {
			eq(e.getMessage(), "Didn't find a value for type 'interface java.lang.Runnable' and name 'runnable'!");
			verifyIoC();
			return;
		}

		expectedException();
	}

	@Test
	public void testTransitiveMandatoryInjection() {
		try {
			IoC.singleton(Baz.class);
		} catch (RuntimeException e) {
			eq(e.getMessage(), ERR_MSG);
			verifyIoC();
			return;
		}

		expectedException();
	}

	@Test
	public void testTransitiveMandatoryInjection2() {
		Baz baz = new Baz();

		try {
			IoC.autowire(baz);
		} catch (RuntimeException e) {
			eq(e.getMessage(), "Didn't find a value for type 'interface java.lang.Runnable' and name 'runnable'!");
			verifyIoC();
			return;
		}

		expectedException();
	}

}

class Foo {
	@Wired
	Runnable runnable;
}

class Bar {
	@Wired
	Foo foo;
}

class Baz {
	@Wired
	Bar bar;
}

