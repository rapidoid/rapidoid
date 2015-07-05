package org.rapidoidx.demo.db;

/*
 * #%L
 * rapidoid-x-demo
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
import org.rapidoid.config.Conf;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoidx.db.XDB;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbCRUDBenchmark {

	public static void main(String[] args) {

		Conf.args(args);

		int size = Conf.option("size", 10000);

		UTILS.benchmarkMT(Conf.cpus(), "insert+read", size, new Runnable() {
			@Override
			public void run() {
				String name = "Niko";
				long id = XDB.insert(new Person(name, 30));
				Person p = XDB.get(id);
				U.must(p.name.equals(name));
			}
		});

		XDB.shutdown();
		XDB.destroy();
	}

}
