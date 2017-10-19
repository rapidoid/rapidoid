package org.rapidoid.process;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.io.IO;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class ProcTest extends TestCommons {

	@Test
	public void testProcessExecution() {
		Processes processes = new Processes();

		for (int i = 0; i < 5; i++) {
			ProcessHandle proc = Proc.group(processes).run("java", "-version").waitFor();

			String out = proc.outAndError().toString();
			isTrue(out.contains("Java") || out.contains("JDK") || out.contains("JRE"));

			String out2 = proc.outAndError().toString();
			eq(out2, out);
		}

		eq(processes.size(), 5);
	}

	@Test
	public void testProcessTermination() throws URISyntaxException {
		String jar = counterJar();

		ProcessHandle proc = Proc.run("java", "-cp", jar, "com.example.Main");
		proc.terminate();

		isFalse(proc.isAlive());

		List<String> lines = Proc.run("ps", "aux").waitFor().out();
		List<String> javaPs = Str.grep("counter.jar", lines);

		for (String p : javaPs) {
			U.print(p);
		}

		eq(javaPs.size(), 0);
	}

	private String counterJar() {
		File jar;

		try {
			jar = new File(IO.resource("counter.jar").toURI());
		} catch (URISyntaxException e) {
			throw U.rte(e);
		}

		isTrue(jar.exists());
		return jar.getAbsolutePath();
	}

	@Test
	public void testProcessOutput() {
		ProcessHandle proc = Proc.run("echo", "ABC\nXY").waitFor();

		eq(proc.out(), U.list("ABC", "XY"));
	}

}
