package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.group.GroupOf;
import org.rapidoid.group.Groups;
import org.rapidoid.group.Manageable;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Grid;
import org.rapidoid.http.Current;
import org.rapidoid.http.Req;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Collection;
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

	private volatile Collection<? extends GroupOf<?>> groups;

	private volatile String baseUri;

	@Override
	public Object call() throws Exception {

		List<Object> info = U.list();
		Collection<? extends GroupOf<?>> targetGroups = groups != null ? groups : Groups.all();

		for (GroupOf<?> group : targetGroups) {
			List<? extends Manageable> items = group.items();

			List<String> nav = U.list(group.kind());

			info.add(h2(group.kind()));

			addInfo(baseUri, info, nav, items);
		}

		info.add(autoRefresh(2000));
		return multi(info);
	}

	public static void addInfo(String baseUri, List<Object> info, List<String> nav, List<? extends Manageable> items) {
		if (U.notEmpty(items)) {
			List<String> columns = U.list(U.first(items).getManageableProperties());

			if (U.notEmpty(columns)) {
				addInfo(baseUri, info, nav, items, columns);
			}
		}
	}

	protected static void addInfo(final String baseUri, List<Object> info, final List<String> nav, List<? extends Manageable> items, List<String> columns) {
		columns.add("(Actions)");

		for (Manageable item : items) {
			item.reloadManageable();
		}

		Grid grid = grid(items)
			.columns(columns)
			.headers(columns)
			.toUri(new Mapper<Manageable, String>() {
				@Override
				public String map(Manageable item) throws Exception {

					Req req = Current.request();

					final List<String> uri = U.list(nav);
					uri.add(0, baseUri);
					uri.add(item.id());

					return Msc.uri(U.arrayOf(uri));
				}
			})
			.pageSize(100);

		info.add(grid);
	}

	public Collection<? extends GroupOf<?>> groups() {
		return groups;
	}

	public ManageablesOverviewPage groups(Collection<? extends GroupOf<?>> groups) {
		this.groups = groups;
		return this;
	}

	public String baseUri() {
		return baseUri;
	}

	public ManageablesOverviewPage baseUri(String baseUri) {
		this.baseUri = baseUri;
		return this;
	}
}
