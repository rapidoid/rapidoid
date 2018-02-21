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

package org.rapidoid.jpa;

import org.rapidoid.AbstractRapidoidModule;
import org.rapidoid.ModuleBootstrapParams;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.RapidoidModuleDesc;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.6.0")
@RapidoidModuleDesc(name = "JPA", order = 550)
public class JPAModule extends AbstractRapidoidModule {

	@Override
	public void cleanUp() {
		JPAUtil.reset();
	}

	@Override
	public void bootstrap(ModuleBootstrapParams setup) {
		JPA.bootstrap(setup.path());
	}

	@Override
	public boolean preventsClassReload(String classname) {
		return JPA.entities().contains(classname);
	}

}
