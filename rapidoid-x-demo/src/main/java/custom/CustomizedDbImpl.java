package custom;

/*
 * #%L
 * rapidoid-x-demo
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
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoidx.db.impl.inmem.DbImpl;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class CustomizedDbImpl extends DbImpl {

	private static final long serialVersionUID = -3304900771653853896L;

	public CustomizedDbImpl(String name, String filename) {
		super(name, filename);
	}

	@Override
	public void delete(long id) {
		Log.warn("deleting record", "id", id);
		super.delete(id);
	}

}
