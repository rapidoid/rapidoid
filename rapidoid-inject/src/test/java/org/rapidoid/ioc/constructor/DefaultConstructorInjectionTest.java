package org.rapidoid.ioc.constructor;

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
import org.rapidoid.annotation.Since;
import org.rapidoid.ioc.AbstractInjectTest;
import org.rapidoid.ioc.IoC;

import javax.inject.Inject;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class DefaultConstructorInjectionTest extends AbstractInjectTest {

	static class A {
	}

	static class B {
	}

	class XYZ {

		final A a;
		final B b;
		final boolean def;

		@Inject
		public XYZ(A a, B b) {
			this.a = a;
			this.b = b;
			this.def = false;
		}

		public XYZ() {
			this.a = null;
			this.b = null;
			this.def = true;
		}
	}

	@Test
	public void shouldIgnoreTheDefaultConstructorWhenAnnotatedExists() {
		XYZ xyz = IoC.singleton(XYZ.class);

		notNull(xyz.a);
		notNull(xyz.b);
		isFalse(xyz.def);
	}

}

