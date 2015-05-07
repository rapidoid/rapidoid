package org.rapidoidx.fullstack;

/*
 * #%L
 * rapidoid-x-fullstack
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

import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Callback;
import org.rapidoid.plugins.DbPlugin;
import org.rapidoidx.db.Database;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class InMemDbPlugin implements DbPlugin {

	protected final Database db;

	public InMemDbPlugin(Database db) {
		this.db = db;
	}

	@Override
	public <T> T get(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getIfExists(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> getAll(Class<T> clazz) {
		return db.find(clazz, "");
	}

	@Override
	public void update(Object entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insert(Object entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> List<T> find(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void transaction(Runnable tx, boolean readonly, Callback<Void> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllData() {
		// TODO Auto-generated method stub

	}

}
