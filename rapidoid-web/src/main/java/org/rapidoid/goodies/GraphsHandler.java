package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.html.Tag;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.insight.Metrics;
import org.rapidoid.timeseries.TimeSeries;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

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
@Since("5.1.0")
public class GraphsHandler extends GUI implements ReqRespHandler {

	@Override
	public Object execute(Req req, Resp resp) throws Exception {
		int columns = req.data("columns", 4);
		return multi(graphs(columns));
	}

	public static List<Tag> graphs(int perRow) {
		List<Tag> rows = U.list();
		Map<String, TimeSeries> metrics = Metrics.all();

		synchronized (metrics) {
			for (List<Map.Entry<String, TimeSeries>> group : U.groupsOf(perRow, metrics.entrySet())) {
				Tag row = row();

				for (Map.Entry<String, TimeSeries> e : group) {
					String uri = e.getKey();
					TimeSeries ts = e.getValue();

					int cols = 12 / perRow;
					String divClass = cols <= 4 ? "rapidoid-dygraph-small" : "rapidoid-dygraph";
					row = row.append(col_(cols, dygraph(uri, ts, divClass)));
				}

				rows.add(row);
			}
		}

		return rows;
	}
}
