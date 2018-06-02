/*-
 * #%L
 * rapidoid-jpa
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.jpa.impl;

import org.hibernate.internal.util.config.ConfigurationException;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.u.U;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@Authors({"Florian Boulay", "Nikolche Mihajlovski"})
@Since("5.5.4")
public class CustomHibernatePersistenceProviderTest {

	private static final String CFG_FILE = "hibernate.ejb.cfgfile";

	@Test
	public void testAlternativeHibernateConfigFile() {
		Map props = U.map(CFG_FILE, "hibernate.xml");

		EntityManagerFactory emf = provider().createEMF(props);

		assertNotNull(emf);
	}

	@Test(expected = ConfigurationException.class)
	public void shouldFailOnWrongConfigFile() {
		Map props = U.map(CFG_FILE, "non-existing.xml");

		provider().createEMF(props);
	}

	private CustomHibernatePersistenceProvider provider() {
		JdbcClient h2 = JDBC.h2("test").init();
		DataSource dataSource = h2.bootstrapDatasource();

		return new CustomHibernatePersistenceProvider(dataSource, U.list());
	}

}
