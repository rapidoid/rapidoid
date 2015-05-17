package org.rapidoid.plugins.impl;

import org.rapidoid.plugins.spec.LanguagesPlugin;
import org.rapidoid.util.English;

/*
 * #%L
 * rapidoid-plugins
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

/**
 * @author Nikolche Mihajlovski
 * @since 3.0.0
 */
public class DefaultLanguagesPlugin implements LanguagesPlugin {

	@Override
	public String singularToPlural(String noun) {
		return English.plural(noun);
	}

	@Override
	public String pluralToSingular(String noun) {
		return English.singular(noun);
	}

}
