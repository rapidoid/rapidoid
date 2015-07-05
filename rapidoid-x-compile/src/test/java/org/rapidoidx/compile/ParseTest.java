package org.rapidoidx.compile;

/*
 * #%L
 * rapidoid-x-compile
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import org.rapidoid.annotation.Since;
import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class ParseTest extends TestCommons {

	@Test
	public void testParsing() throws Exception {
		notNull(Parse.expression("1 + (2 * 3 % 4)"));
		isNull(Parse.expression(""));
		isNull(Parse.expression("class {{{{"));

		notNull(Parse.statements("int x = 1; int hh=435+x;"));
		notNull(Parse.statements(""));

		notNull(Parse.unit("public class Abc {}"));
		notNull(Parse.unit("public class Abc {{{{{"));
	}

}
