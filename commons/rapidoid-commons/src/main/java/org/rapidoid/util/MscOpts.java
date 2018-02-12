/*-
 * #%L
 * rapidoid-commons
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

package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class MscOpts extends RapidoidThing {

	private static final boolean hasDockerEnv = U.eq(System.getenv("RAPIDOID_JAR"), "/opt/rapidoid.jar")
		&& U.eq(System.getenv("RAPIDOID_TMP"), "/tmp/rapidoid")
		&& U.notEmpty(System.getenv("RAPIDOID_VERSION"));

	private static final boolean hasValidation = Cls.exists("javax.validation.Validation");
	private static final boolean hasJPA = Cls.exists("javax.persistence.Entity");
	private static final boolean hasHibernate = Cls.exists("org.hibernate.cfg.Configuration");

	private static final boolean hasRapidoidJPA = Cls.exists("org.rapidoid.jpa.JPA");
	private static final boolean hasRapidoidGUI = Cls.exists("org.rapidoid.gui.GUI");
	private static final boolean hasRapidoidRender = Cls.exists("org.rapidoid.render.Templates");
	private static final boolean hasRapidoidWatch = Cls.exists("org.rapidoid.reload.Reload");
	private static final boolean hasRapidoidPlatform = Cls.exists("org.rapidoid.standalone.Main");

	private static final boolean hasLogback = Cls.exists("ch.qos.logback.classic.Logger");
	private static final boolean hasSlf4jImpl = Cls.exists("org.slf4j.impl.StaticLoggerBinder");

	private static final boolean hasC3P0 = Cls.exists("com.mchange.v2.c3p0.PooledDataSource");
	private static final boolean hasHikari = Cls.exists("com.zaxxer.hikari.HikariDataSource");

	private static final boolean isRestOnly = !hasRapidoidGUI();

	public static boolean hasDockerEnv() {
		return hasDockerEnv;
	}

	public static boolean hasValidation() {
		return hasValidation;
	}

	public static boolean hasJPA() {
		return hasJPA;
	}

	public static boolean hasHibernate() {
		return hasHibernate;
	}

	public static boolean hasRapidoidJPA() {
		return hasRapidoidJPA;
	}

	public static boolean hasRapidoidGUI() {
		return hasRapidoidGUI;
	}

	public static boolean hasRapidoidRender() {
		return hasRapidoidRender;
	}

	public static boolean hasRapidoidWatch() {
		return hasRapidoidWatch;
	}

	public static boolean hasRapidoidPlatform() {
		return hasRapidoidPlatform;
	}

	public static boolean hasLogback() {
		return hasLogback;
	}

	public static boolean hasSlf4jImpl() {
		return hasSlf4jImpl;
	}

	public static boolean hasC3P0() {
		return hasC3P0;
	}

	public static boolean hasHikari() {
		return hasHikari;
	}

	public static boolean isRestOnly() {
		return isRestOnly;
	}

	public static boolean isTLSEnabled() {
		return Conf.TLS.is("enabled");
	}
}
