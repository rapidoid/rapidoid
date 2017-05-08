package org.rapidoid.benchmark.highlevel;

/*
 * #%L
 * rapidoid-benchmark
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

import org.rapidoid.benchmark.common.Fortune;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.render.Template;
import org.rapidoid.render.Templates;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

public class FortunesHandler implements ReqRespHandler {

	private static final String SQL = "SELECT id, message FROM fortune";

	private static final Mapper<ResultSet, Fortune> resultMapper = rs -> new Fortune(rs.getInt(1), rs.getString(2));

	private static final Template template = Templates.load("fortunes.html");

	private final JdbcClient jdbc;

	public FortunesHandler(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public Object execute(Req req, Resp resp) throws Exception {
		req.async();

		jdbc.execute(resultMapper, (List<Fortune> fortunes, Throwable err) -> {

			if (err == null) {
				fortunes.add(new Fortune(0, "Additional fortune added at request time."));

				Collections.sort(fortunes);

				resp.result(template.renderToBytes(fortunes));

			} else {
				resp.result(err);
			}

			resp.done();

		}, SQL);

		return req;
	}

}
