package org.rapidoid.log.commons;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class RapidoidLogFactory extends LogFactory {

	@Override
	public Object getAttribute(String name) {
		return null;
	}

	@Override
	public String[] getAttributeNames() {
		return new String[0];
	}

	@Override
	public Log getInstance(Class clazz) throws LogConfigurationException {
		return new RapidoidLog(clazz.getName());
	}

	@Override
	public Log getInstance(String name) throws LogConfigurationException {
		return new RapidoidLog(name);
	}

	@Override
	public void release() {

	}

	@Override
	public void removeAttribute(String name) {

	}

	@Override
	public void setAttribute(String name, Object value) {

	}
}
