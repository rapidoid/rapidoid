package org.rapidoid.jpa;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.log.Log;
import org.rapidoid.scan.Scan;
import org.rapidoid.util.UTILS;

import javax.persistence.Entity;
import java.util.List;
import java.util.Properties;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class EMFUtil {

	public static synchronized List<String> createEMF(String path[], Class<?>... entities) {

		List<String> entityTypes = Scan.annotated(Entity.class).in(path).getAll();

		for (Class<?> entity : entities) {
			String type = entity.getName();
			if (!entityTypes.contains(type)) {
				entityTypes.add(type);
			}
		}

		UTILS.logSection("Total " + entityTypes.size() + " JPA Entities:");
		for (String entityType : entityTypes) {
			Log.info("Entity", "type", entityType);
		}

		return entityTypes;
	}

	public static Properties hibernateProperties() {
		return Conf.HIBERNATE.toProperties();
	}

}
