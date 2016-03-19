package org.rapidoid.jpa;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class CustomHibernatePersistenceProvider extends HibernatePersistenceProvider {

	private final List<String> names = U.list();

	@Override
	protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitDescriptor persistenceUnitDescriptor, Map integration, ClassLoader cl) {
		CustomDescriptor descriptor = new CustomDescriptor(persistenceUnitDescriptor, names);
		return super.getEntityManagerFactoryBuilder(descriptor, integration, ClasspathUtil.getDefaultClassLoader());
	}

	public List<String> names() {
		return names;
	}
}
