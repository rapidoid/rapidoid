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

import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.u.U;

import javax.sql.DataSource;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@Authors("Florian Boulay")
@Since("5.5.4")
public class CustomHibernatePersistenceProviderTest {

	@Test
	public void testAlternativeHibernateConfigFile() {
		JdbcClient h2 = JDBC.h2("test").init();
		DataSource dataSource = h2.bootstrapDatasource();
		CustomHibernatePersistenceProvider provider = new CustomHibernatePersistenceProvider(dataSource);

		Map props = U.map("hibernate.ejb.cfgfile", "hibernate.xml");
		EntityManagerFactoryBuilder emfBuilder = provider.getEntityManagerFactoryBuilderOrNull("test", props, this.getClass().getClassLoader());
		assertNotNull(emfBuilder);
	}

}
