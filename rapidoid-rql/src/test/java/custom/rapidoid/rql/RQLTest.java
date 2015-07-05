package custom.rapidoid.rql;

/*
 * #%L
 * rapidoid-rql
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.junit.Before;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.DbEntity;
import org.rapidoid.annotation.Since;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.rql.RQL;
import org.rapidoid.test.TestCommons;

@DbEntity
class Abc {
	long id;
	String name = "a";
	int n = 55;
	boolean z = false;
	LowHigh lh;
}

enum LowHigh {
	LOW, HIGH
}

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class RQLTest extends TestCommons {

	@Before
	public void setup() {
		Plugins.bootstrap();
	}

	@Test
	public void testRQLEntityConstruction() {
		Abc x = RQL.entity("abc name=n1, n=123, z, lh=high");
		eq(x.name, "n1");
		eq(x.n, 123);
		isTrue(x.z);
		eq(x.lh, LowHigh.HIGH);
	}

	@Test
	public void testRQLEntityConstructionDefaults() {
		Abc x = RQL.entity("Abc");
		eq(x.name, "a");
		eq(x.n, 55);
		isFalse(x.z);
		eq(x.lh, null);
	}

	@Test
	public void testRQLParameterizedEntityConstruction() {
		Abc x = RQL.entity("abc name=?, n=?, z, lh=?", "n1", 123, LowHigh.HIGH);
		eq(x.name, "n1");
		eq(x.n, 123);
		isTrue(x.z);
		eq(x.lh, LowHigh.HIGH);
	}

	@Test
	public void testRQLInjection() {
		// RQL special characters
		String strange = "thename, n=789, z=?,  ? ? , ?,,";

		Abc x = RQL.entity("abc name=?, z, lh=?", strange, LowHigh.HIGH);
		eq(x.name, strange);
		eq(x.n, 55);
		isTrue(x.z);
		eq(x.lh, LowHigh.HIGH);
	}

}
