package org.rapidoid.demo.db;

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

import org.rapidoid.db.DB;
import org.rapidoid.util.U;

public class DbPersistenceBenchmark {

	public static void main(String[] args) {

		U.args(args);

		final int size = U.option("size", 10000);

		System.out.println("inserting...");

		U.startMeasure();

		U.benchmarkMT(U.cpus(), "insert", size, new Runnable() {
			@Override
			public void run() {
				DB.insert(new Person("abc", 10));
			}
		});

		System.out.println("updating...");

		U.benchmarkMT(U.cpus(), "update", size, new Runnable() {
			@Override
			public void run() {
				DB.update(U.rnd(size) + 1, new Person("xyz", 10));
			}
		});

		System.out.println("persisting...");

		DB.shutdown();

		U.endMeasure("total");
	}

}
