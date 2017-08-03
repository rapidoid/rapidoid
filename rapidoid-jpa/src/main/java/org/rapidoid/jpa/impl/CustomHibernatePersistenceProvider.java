package org.rapidoid.jpa.impl;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-jpa
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
public class CustomHibernatePersistenceProvider extends HibernatePersistenceProvider {

	private final DataSource dataSource;

	private final List<String> names = U.list();

	public CustomHibernatePersistenceProvider(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitDescriptor persistenceUnitDescriptor, Map integration, ClassLoader cl) {
		return emfBuilder(persistenceUnitDescriptor, integration, cl);
	}

	@Override
	protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(String persistenceUnitName, Map properties, ClassLoader providedClassLoader) {

		PersistenceUnitInfo info = new RapidoidPersistenceUnitInfo(persistenceUnitName, dataSource, providedClassLoader);
		PersistenceUnitInfoDescriptor persistenceUnit = new PersistenceUnitInfoDescriptor(info);
		final Map integration = wrap(properties);

		return emfBuilder(persistenceUnit, integration, providedClassLoader);
	}

	private EntityManagerFactoryBuilder emfBuilder(PersistenceUnitDescriptor persistenceUnitDescriptor, Map integration, ClassLoader cl) {
		CustomDescriptor descriptor = new CustomDescriptor(persistenceUnitDescriptor, names);
		return super.getEntityManagerFactoryBuilder(descriptor, integration, cl);
	}

	public List<String> names() {
		return names;
	}
}
