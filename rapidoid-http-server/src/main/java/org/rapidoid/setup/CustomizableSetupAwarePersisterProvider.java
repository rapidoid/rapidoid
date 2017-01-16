package org.rapidoid.setup;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.PersisterProvider;
import org.rapidoid.http.Req;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.EntityManagerFactoryProvider;
import org.rapidoid.http.customize.EntityManagerProvider;
import org.rapidoid.u.U;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/*
 * #%L
 * rapidoid-http-server
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
@Since("5.2.0")
public class CustomizableSetupAwarePersisterProvider extends RapidoidThing implements PersisterProvider {

	@SuppressWarnings("unchecked")
	@Override
	public <P> P openPersister(Ctx ctx) {
		Req req = ctx.exchange();
		Customization custom = Customization.of(req);

		EntityManagerProvider entityManagerProvider = custom.entityManagerProvider();

		if (entityManagerProvider != null) {
			try {
				return (P) entityManagerProvider.getEntityManager(req);
			} catch (Exception e) {
				throw U.rte("Error occurred in the EntityManager provider!", e);
			}
		}

		EntityManagerFactory emf;
		EntityManagerFactoryProvider entityManagerFactoryProvider = custom.entityManagerFactoryProvider();

		if (entityManagerFactoryProvider != null) {
			try {
				emf = entityManagerFactoryProvider.getEntityManagerFactory(req);
			} catch (Exception e) {
				throw U.rte("Error occurred in the EntityManagerFactory provider!", e);
			}

		} else {
			throw U.rte("No EntityManagerProvider nor EntityManagerFactoryProvider was configured!");
		}

		try {
			return (P) emf.createEntityManager();
		} catch (Exception e) {
			throw U.rte("Error occurred while creating an EntityManager!", e);
		}
	}

	@Override
	public void closePersister(Ctx ctx, Object persister) {
		EntityManager em = (EntityManager) persister;
		em.close();
	}

}
