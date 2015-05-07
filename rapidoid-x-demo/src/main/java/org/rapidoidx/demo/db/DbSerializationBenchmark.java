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

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.util.UTILS;
import org.rapidoidx.db.impl.inmem.DbEntityConstructor;
import org.rapidoidx.db.impl.inmem.JacksonEntitySerializer;
import org.rapidoidx.inmem.InMem;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbSerializationBenchmark {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		Conf.args(args);

		final InMem db = new InMem(null, new JacksonEntitySerializer(null), new DbEntityConstructor(null),
				Collections.EMPTY_SET, null);

		int size = Conf.option("size", 100000);
		int loops = Conf.option("loops", 100);

		for (int i = 0; i < size; i++) {
			db.insert(new Person("john doe" + i, i));
		}

		System.out.println("measuring...");

		UTILS.benchmark("save " + size + " records", loops, new Runnable() {
			@Override
			public void run() {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				db.saveTo(out);
				out.toByteArray();
			}
		});
	}

}
