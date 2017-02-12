package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.group.GroupOf;
import org.rapidoid.group.Groups;
import org.rapidoid.group.Manageable;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Grid;
import org.rapidoid.lambda.Mapper;
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
public class ManageablesOverviewPage extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		List<Object> info = U.list();

		for (GroupOf<?> group : Groups.all()) {
			List<? extends Manageable> items = group.items();

			if (U.notEmpty(items)) {
				List<String> columns = U.first(items).getManageableProperties();

				if (U.notEmpty(columns)) {
					addInfo(info, group, items, columns);
				}
			}
		}

		info.add(autoRefresh(1000));
		return multi(info);
	}

	private void addInfo(List<Object> info, final GroupOf<?> group, List<? extends Manageable> items, List<String> columns) {
		columns.add("(Actions)");
		final String groupName = group.name();

		String type = U.first(items).getManageableType();
		info.add(breadcrumb(type, groupName));

		Grid grid = grid(items)
			.columns(columns)
			.headers(columns)
			.toUri(new Mapper<Manageable, String>() {
				@Override
				public String map(Manageable handle) throws Exception {
					return Msc.uri("_manageables", handle.getClass().getSimpleName(), Msc.urlEncode(handle.id()));
				}
			})
			.pageSize(20);

		info.add(grid);
	}

}
