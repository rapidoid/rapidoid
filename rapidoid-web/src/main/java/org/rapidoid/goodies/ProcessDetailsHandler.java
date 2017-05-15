package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.group.Manageables;
import org.rapidoid.gui.GUI;
import org.rapidoid.html.Tag;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.process.ProcessHandle;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;

/*
 * #%L
 * rapidoid-web
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

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class ProcessDetailsHandler extends GUI implements ReqRespHandler {

	@Override
	public Object execute(Req req, Resp resp) {
		List<Object> info = U.list();

		String id = req.data("id");

		ProcessHandle handle = Manageables.find(ProcessHandle.class, id);
		U.must(handle != null, "Cannot find the process!");

		info.add(h1("Process details"));

		info.add(right(cmd("View all processes").small().go(Msc.specialUri("processes"))));
		info.add(code(U.join(" ", handle.params().command())));

		info.add(h2("Standard output:"));
		info.add(showOutput(handle.out()));

		info.add(h2("Error output:"));
		info.add(showOutput(handle.err()));

		return multi(info);
	}

	public static List<Tag> showOutput(List<String> lines) {
		List<Tag> els = U.list();

		for (String line : lines) {
			line = line.trim();

			els.add(pre(line).class_(getOutputLineClass(line)));
		}

		return els;
	}

	public static String getOutputLineClass(String line) {
		String upperLine = line.toUpperCase();

		if (upperLine.contains("[SEVERE]") || upperLine.contains(" SEVERE ")) return "proc-out proc-out-severe";
		if (upperLine.contains("[FATAL]") || upperLine.contains(" FATAL ")) return "proc-out proc-out-severe";

		if (upperLine.contains("[WARNING]") || upperLine.contains("[WARN]") ||
			upperLine.contains(" WARNING ") || upperLine.contains(" WARN ")) return "proc-out proc-out-warning";

		if (upperLine.contains("[ERROR]") || upperLine.contains(" ERROR ")) return "proc-out proc-out-error";

		return "proc-out proc-out-default";
	}

}
