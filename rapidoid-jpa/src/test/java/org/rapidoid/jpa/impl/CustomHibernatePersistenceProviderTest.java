package org.rapidoid.jpa.impl;

import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.junit.Test;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.u.U;

import javax.sql.DataSource;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

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
