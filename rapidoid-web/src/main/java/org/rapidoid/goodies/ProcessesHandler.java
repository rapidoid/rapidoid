package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Grid;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.process.ProcessHandle;
import org.rapidoid.process.Processes;
import org.rapidoid.u.U;

import java.util.List;
import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
public class ProcessesHandler extends GUI implements Callable<Object> {

	public static final String[] COLUMNS = {
		"cmd",
		"args",
		"$.params().in()",
		"alive",
		"exitCode",
		"duration",
		"startedAt",
		"finishedAt",
		"$.group().name()",
	};

	public static final Object[] COLUMN_NAMES = {
		"Command",
		"Arguments",
		"Location",
		"Is alive?",
		"Exit code",
		"Duration (ms)",
		"Started at",
		"Finished at",
		"Group",
	};

	@Override
	public Object call() throws Exception {
		List<Object> info = U.list();

		info.add(h3("Managed processes:"));

		List<ProcessHandle> processes = Processes.DEFAULT.items();

		Grid grid = grid(processes)
			.columns(COLUMNS)
			.headers(COLUMN_NAMES)
			.toUri(new Mapper<ProcessHandle, String>() {
				@Override
				public String map(ProcessHandle handle) throws Exception {
					return U.frmt("/_processes/%s/%s", handle.group().name(), handle.id());
				}
			})
			.pageSize(100);

		info.add(grid);

		return multi(info);
	}

}
