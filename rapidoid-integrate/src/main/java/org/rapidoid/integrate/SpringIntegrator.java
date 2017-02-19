package org.rapidoid.integrate;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.env.Env;
import org.rapidoid.ioc.BeanProvider;
import org.rapidoid.ioc.IoC;
import org.rapidoid.jpa.JPA;
import org.rapidoid.u.U;
import org.rapidoid.util.MscOpts;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.util.Collections;

/*
 * #%L
 * rapidoid-integrate
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
@Component
public abstract class SpringIntegrator extends RapidoidThing implements Runnable, BeanProvider {

	@Resource
	private ConfigurableApplicationContext ctx;

	@PostConstruct
	public void postConstructInitialization() {
		U.notNull(ctx, "ConfigurableApplicationContext ctx");

		if (useProfiles()) {
			initProfiles();
		}

		if (useEmf() && JPA.getEmf() == null && MscOpts.hasJPA()) {
			initJPA();
		}

		if (useBeans()) {
			initBeans();
		}

		run();
	}

	protected void initProfiles() {
		Collections.addAll(Env.profiles(), ctx.getEnvironment().getActiveProfiles());
	}

	protected void initJPA() {
		EntityManagerFactory emf;

		try {
			emf = ctx.getBean(EntityManagerFactory.class);
		} catch (Exception e) {
			return;
		}

		JPA.setEmf(emf);
	}

	protected void initBeans() {
		IoC.defaultContext().beanProvider(this);
	}

	@Override
	public <T> T getBean(Class<T> type, String name) {
		U.notNull(ctx, "ConfigurableApplicationContext ctx");

		try {
			return ctx.getBean(name, type);
		} catch (Exception e) {
			return null;
		}
	}

	protected boolean useProfiles() {
		return true;
	}

	protected boolean useEmf() {
		return true;
	}

	protected boolean useBeans() {
		return true;
	}

}
