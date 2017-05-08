package org.rapidoid.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;

import javax.sql.DataSource;

/*
 * #%L
 * rapidoid-sql
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
@Since("5.3.4")
public class HikariFactory extends RapidoidThing {

	public static DataSource createDataSourceFor(JdbcClient jdbc) {
		HikariConfig config = new HikariConfig();

		config.setJdbcUrl(jdbc.url());
		config.setUsername(jdbc.username());
		config.setPassword(jdbc.password());
		config.setDriverClassName(jdbc.driver());

		Conf.HIKARI.applyTo(config);

		return new HikariDataSource(config);
	}

}
