package org.rapidoid.jdbc;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.u.U;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

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
@Since("4.1.0")
public class C3P0Factory extends RapidoidThing {

	public static DataSource createDataSourceFor(JdbcClient jdbc) {
		ComboPooledDataSource pool = new ComboPooledDataSource();

		pool.setJdbcUrl(jdbc.url());
		pool.setUser(jdbc.username());
		pool.setPassword(jdbc.password());

		try {
			pool.setDriverClass(jdbc.driver());
		} catch (PropertyVetoException e) {
			throw U.rte("Cannot load JDBC driver!", e);
		}

		Conf.C3P0.applyTo(pool);

		return pool;
	}

}
