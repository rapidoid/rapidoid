package org.rapidoid;

/*
 * #%L
 * rapidoid-commons
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.RapidoidModuleDesc;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public abstract class AbstractRapidoidModule extends RapidoidThing implements RapidoidModule {

	@Override
	public String name() {
		return desc().name();
	}

	@Override
	public int order() {
		return desc().order();
	}

	@Override
	public void boot() {
		// do nothing
	}

	@Override
	public abstract void cleanUp();

	@Override
	public void beforeTest(Object test) {
		cleanUp();
	}

	@Override
	public void initTest(Object test) {
		// do nothing
	}

	@Override
	public void afterTest(Object test) {
		cleanUp();
	}

	protected RapidoidModuleDesc desc() {
		RapidoidModuleDesc annotation = getClass().getAnnotation(RapidoidModuleDesc.class);
		U.must(annotation != null, "The Rapidoid module must be annotated with: %s", RapidoidModuleDesc.class);
		return annotation;
	}

}
