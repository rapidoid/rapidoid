package org.rapidoidx.demo.db;

/*
 * #%L
 * rapidoid-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.util.Rnd;
import org.rapidoid.util.UTILS;
import org.rapidoidx.db.XDB;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
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
