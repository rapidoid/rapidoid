package org.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.DbEntity;
import org.rapidoid.annotation.Since;
import org.rapidoid.plugins.Plugins;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

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
public class RQLTest extends DbTestCommons {

	@BeforeTest
	public void init() {
		Plugins.bootstrap();
	}

	@Test
	public void testRQLEntityConstruction() {
		Abc x = XDB.entity("abc name=n1, n=123, z, lh=high");
		eq(x.name, "n1");
		eq(x.n, 123);
		isTrue(x.z);
		eq(x.lh, LowHigh.HIGH);
	}

	@Test
	public void testRQLEntityConstructionDefaults() {
		Abc x = XDB.entity("Abc");
		eq(x.name, "a");
		eq(x.n, 55);
		isFalse(x.z);
		eq(x.lh, null);
	}

	@Test
	public void testRQLParameterizedEntityConstruction() {
		Abc x = XDB.entity("abc name=?, n=?, z, lh=?", "n1", 123, LowHigh.HIGH);
		eq(x.name, "n1");
		eq(x.n, 123);
		isTrue(x.z);
		eq(x.lh, LowHigh.HIGH);
	}

	@Test
	public void testRQLInjection() {
		// RQL special characters
		String strange = "thename, n=789, z=?,  ? ? , ?,,";

		Abc x = XDB.entity("abc name=?, z, lh=?", strange, LowHigh.HIGH);
		eq(x.name, strange);
		eq(x.n, 55);
		isTrue(x.z);
		eq(x.lh, LowHigh.HIGH);
	}

	@Test
	public void testRQLInsert() {
		Long id = XDB.rql("INSERT Abc name=n1, n=123, z=false, lh=low");
		Abc x = XDB.get(id);

		eq(x.name, "n1");
		eq(x.n, 123);
		isFalse(x.z);
		eq(x.lh, LowHigh.LOW);
	}

	@Test
	public void testRQLParameterizedInsert() {
		Long id = XDB.rql("INSERT Abc name=?, n=123, z=?, lh=low", "n1", false);
		Abc x = XDB.get(id);

		eq(x.name, "n1");
		eq(x.n, 123);
		isFalse(x.z);
		eq(x.lh, LowHigh.LOW);
	}

}
