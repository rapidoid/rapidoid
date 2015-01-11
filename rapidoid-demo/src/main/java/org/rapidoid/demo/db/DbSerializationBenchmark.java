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

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import org.rapidoid.db.impl.JacksonEntitySerializer;
import org.rapidoid.inmem.InMem;
import org.rapidoid.util.Conf;
import org.rapidoid.util.U;

public class DbSerializationBenchmark {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		U.args(args);

		final InMem db = new InMem(null, new JacksonEntitySerializer(null), Collections.EMPTY_SET);

		int size = Conf.option("size", 100000);
		int loops = Conf.option("loops", 100);

		for (int i = 0; i < size; i++) {
			db.insert(new Person("john doe" + i, i));
		}

		System.out.println("measuring...");

		U.benchmark("save " + size + " records", loops, new Runnable() {
			@Override
			public void run() {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				db.saveTo(out);
				out.toByteArray();
			}
		});
	}

}
