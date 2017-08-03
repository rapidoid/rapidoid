package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.group.GroupOf;
import org.rapidoid.group.Groups;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Grid;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.process.ProcessHandle;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.concurrent.Callable;

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
public class ProcessesHandler extends GUI implements Callable<Object> {

	public static final String[] COLUMNS = {
		"id",
		"cmd",
		"args",
		"$.params().in()",
		"alive",
		"exitCode",
		"$.duration() / 1000",
		"startedAt",
		"finishedAt",
		"$.group().kind()",
		"(actions)"
	};

	public static final Object[] COLUMN_NAMES = {
		"ID",
		"Command",
		"Arguments",
		"Location",
		"Is alive?",
		"Exit code",
		"Duration (sec)",
		"Started at",
		"Finished at",
		"Group",
		"Actions",
	};

	@Override
	public Object call() throws Exception {
		List<Object> info = U.list();

		info.add(h3("Managed processes:"));

		List<GroupOf<ProcessHandle>> gr = Groups.find(ProcessHandle.class);

		List<ProcessHandle> processes = U.list();

		for (GroupOf<ProcessHandle> group : gr) {
			processes.addAll(group.items());
		}

		Grid grid = grid(processes)
			.columns(COLUMNS)
			.headers(COLUMN_NAMES)
			.toUri(new Mapper<ProcessHandle, String>() {
				@Override
				public String map(ProcessHandle handle) throws Exception {
					return Msc.specialUri("processes/" + handle.id());
				}
			})
			.pageSize(100);

		info.add(grid);

		info.add(autoRefresh(2000));
		return multi(info);
	}

}
