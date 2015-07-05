package custom.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
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
import org.junit.Test;

import custom.rapidoidx.db.model.Post;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbClassEqualityTest extends DbTestCommons {

	@Test
	public void testEntityEquality() {
		neq(post(null), post(null));
		neq(post(null), post(123L));
		neq(post(12L), post(null));
		eq(post(123L), post(123L));
		neq(post(123L), post(547L));
	}

	private Post post(Long id) {
		Post p = new Post();
		p.id(id);
		return p;
	}

}
