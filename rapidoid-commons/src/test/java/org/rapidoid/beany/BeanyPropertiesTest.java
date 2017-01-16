package org.rapidoid.beany;

import org.junit.Assert;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

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

enum XY {
	X, Y
}

class Baz {

	public int x = 12;

	public String g = "gg";

	public XY xy = XY.X;

	int abcd = 111;

	public long id = 114;

	@SuppressWarnings("unused")
	private int invisible2 = 111;

	protected int invisible3 = 111;
}

@Authors("Daniel Kalevski")
@Since("4.0.1")
public class BeanyPropertiesTest extends BeanyTestCommons {

	@Test
	public void testGetPropValue() {
		Baz baz = new Baz();
		Assert.assertEquals(baz.x, Beany.getPropValue(baz, "x"));
	}

	public void testGetIdValue() {
		Baz baz = new Baz();
		Assert.assertEquals("114", Beany.getId(baz));
	}
}
