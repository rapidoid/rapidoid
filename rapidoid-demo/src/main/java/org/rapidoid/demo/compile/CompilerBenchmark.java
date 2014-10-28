package org.rapidoid.demo.compile;

/*
 * #%L
 * rapidoid-demo
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.compile.Compile;
import org.rapidoid.util.U;
import org.rapidoid.wrap.Int;

public class CompilerBenchmark {

	public static void main(String[] args) throws Throwable {
		U.args(args);

		int count = U.option("count", 1000);
		int threads = U.option("threads", U.cpus());

		final String src1 = "public class Main%s { public static void main(String[] args) { U.info(\"abc%s\"); } }";
		final String src2 = "public class Book%s { String title=\"%s\"; int year = 0;} class Foo%s {}";
		final String src3 = "public class Bar%s extends Foo%s {}";

		final Int n = new Int();

		U.benchmarkMT(threads, "compile", count, new Runnable() {
			@Override
			public void run() {
				int x = n.value++;
				Compile.compile(U.format(src1, x, x), U.format(src2, x, x, x), U.format(src3, x, x));
			}
		});
	}

}
