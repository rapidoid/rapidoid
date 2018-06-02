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

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.scan.ClasspathUtil;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class CustomHibernatePersistenceProvider extends HibernatePersistenceProvider {

	private final String persistenceUnitName;
	private final DataSource dataSource;
	private final List<String> entityTypes;
	private final ClassLoader classLoader;

	public CustomHibernatePersistenceProvider(DataSource dataSource, List<String> entityTypes) {
		this("rapidoid", dataSource, entityTypes, ClasspathUtil.getDefaultClassLoader());
	}

	public CustomHibernatePersistenceProvider(String persistenceUnitName, DataSource dataSource,
	                                          List<String> entityTypes, ClassLoader classLoader) {
		this.persistenceUnitName = persistenceUnitName;
		this.dataSource = dataSource;
		this.entityTypes = entityTypes;
		this.classLoader = classLoader;
	}

	public EntityManagerFactory createEMF(Map properties) {
		PersistenceUnitInfo info = new RapidoidPersistenceUnitInfo(persistenceUnitName, dataSource, classLoader);
		PersistenceUnitInfoDescriptor infoDescriptor = new PersistenceUnitInfoDescriptor(info);

		CustomDescriptor customDescriptor = new CustomDescriptor(infoDescriptor, entityTypes);
		EntityManagerFactoryBuilder builder = getEntityManagerFactoryBuilder(customDescriptor, properties, classLoader);

		return builder.build();
	}

}
