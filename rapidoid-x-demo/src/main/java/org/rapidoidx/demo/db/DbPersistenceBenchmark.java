package org.rapidoidx.demo.db;

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
import org.rapidoid.config.Conf;
import org.rapidoid.util.Rnd;
import org.rapidoid.util.UTILS;
import org.rapidoidx.db.XDB;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbPersistenceBenchmark {

	public static void main(String[] args) {

		Conf.args(args);

		final int size = Conf.option("size", 10000);

		System.out.println("inserting...");

		UTILS.startMeasure();

		UTILS.benchmarkMT(Conf.cpus(), "insert", size, new Runnable() {
			@Override
			public void run() {
				XDB.insert(new Person("abc", 10));
			}
		});

		System.out.println("updating...");

		UTILS.benchmarkMT(Conf.cpus(), "update", size, new Runnable() {
			@Override
			public void run() {
				XDB.update(Rnd.rnd(size) + 1, new Person("xyz", 10));
			}
		});

		System.out.println("persisting...");

		XDB.shutdown();

		UTILS.endMeasure("total");
	}

}
