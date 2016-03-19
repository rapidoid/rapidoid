package org.rapidoid.jpa;

/*
 * #%L
 * rapidoid-commons
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.PersisterProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
@SuppressWarnings("deprecation")
public class QuickJPA implements PersisterProvider {

	private final EntityManagerFactory emf;

	private final List<String> entityTypes;

	public QuickJPA(EntityManagerFactory emf, List<String> entityTypes) {
		this.emf = emf;
		this.entityTypes = entityTypes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> P openPersister() {
		return (P) emf.createEntityManager();
	}

	@Override
	public void closePersister(Object persister) {
		EntityManager em = (EntityManager) persister;
		em.close();
	}

	public EntityManagerFactory emf() {
		return emf;
	}

	public List<String> entityTypes() {
		return entityTypes;
	}
}
