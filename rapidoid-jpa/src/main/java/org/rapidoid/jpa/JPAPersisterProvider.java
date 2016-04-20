package org.rapidoid.jpa;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.PersisterProvider;
import org.rapidoid.u.U;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/*
 * #%L
 * rapidoid-jpa
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
@SuppressWarnings("deprecation")
public class JPAPersisterProvider extends RapidoidThing implements PersisterProvider {

	@SuppressWarnings("unchecked")
	@Override
	public <P> P openPersister() {
		EntityManagerFactory emf = JPA.emf();
		U.notNull(emf, "JPA.emf");
		return (P) emf.createEntityManager();
	}

	@Override
	public void closePersister(Object persister) {
		EntityManager em = (EntityManager) persister;
		em.close();
	}

}
