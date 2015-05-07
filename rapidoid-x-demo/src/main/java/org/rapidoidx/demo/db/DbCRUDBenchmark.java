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
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoidx.db.DB;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbCRUDBenchmark {

	public static void main(String[] args) {

		Conf.args(args);

		int size = Conf.option("size", 10000);

		UTILS.benchmarkMT(Conf.cpus(), "insert+read", size, new Runnable() {
			@Override
			public void run() {
				String name = "Niko";
				long id = DB.insert(new Person(name, 30));
				Person p = DB.get(id);
				U.must(p.name.equals(name));
			}
		});

		DB.shutdown();
		DB.destroy();
	}

}
