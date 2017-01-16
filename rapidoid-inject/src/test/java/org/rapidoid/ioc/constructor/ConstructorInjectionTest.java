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
public class ConstructorInjectionTest extends AbstractInjectTest {

	static class A {
		A() {
		}
	}

	class XYZ {

		final A x;
		private final B b;
		String y;
		boolean z;

		@Inject
		public XYZ(A x, B b) {
			this.x = x;
			this.b = b;
		}

		public XYZ(A x, String y, boolean z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.b = null;
		}
	}

	@Test
	public void shouldInjectUsingConstructor() {
		XYZ xyz = IoC.singleton(XYZ.class);
		XYZ xyz2 = IoC.singleton(XYZ.class);

		notNull(xyz.x);
		notNull(xyz.b);

		isTrue(xyz == xyz2);
	}

}

class B {
}
