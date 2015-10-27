package org.rapidoidx.demo.compile;

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
import org.rapidoid.wrap.IntWrap;
import org.rapidoidx.compile.Compile;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class CompilerBenchmark {

	public static void main(String[] args) throws Throwable {
		Conf.args(args);

		int count = Conf.option("count", 10000);
		int threads = Conf.option("threads", Conf.cpus());

		final String src1 = "public class Main%s { public static void main(String[] args) { Log.info(\"abc%s\"); } }";
		final String src2 = "public class Book%s { String title=\"%s\"; int year = 0;} class Foo%s {}";
		final String src3 = "public class Bar%s extends Foo%s {}";

		final IntWrap n = new IntWrap();

		UTILS.benchmarkMT(threads, "compile", count, new Runnable() {
			@Override
			public void run() {
				int x = n.value++;
				Compile.compile(U.frmt(src1, x, x), U.frmt(src2, x, x, x), U.frmt(src3, x, x));
			}
		});
	}

}
