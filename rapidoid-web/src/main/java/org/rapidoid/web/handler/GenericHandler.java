package org.rapidoid.web.handler;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.datamodel.Results;
import org.rapidoid.http.Current;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.jdbc.JDBC;

@Authors("Nikolche Mihajlovski")
@Since("5.3.3")
public abstract class GenericHandler extends RapidoidThing implements ReqRespHandler {

	protected Results sqlItems(String sql) {
		return JDBC.query(sql, HttpUtils.webParams(req()));
	}

	protected int executeSql(String sql) {
		return JDBC.execute(sql, HttpUtils.webParams(req()));
	}

	protected Req req() {
		return Current.request();
	}

}
