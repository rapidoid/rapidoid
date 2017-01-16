package org.rapidoid.http.handler.param;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.Req;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.u.U;

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
@Since("5.1.0")
public class BeanParamRetriever extends RapidoidThing implements ParamRetriever {

	private final Customization customization;
	private final Class<?> type;
	private final boolean validate;
	private final String name;

	public BeanParamRetriever(Customization customization, Class<?> type, String name, boolean validate) {
		this.customization = customization;
		this.type = type;
		this.name = name;
		this.validate = validate;
	}

	@Override
	public Object getParamValue(Req req) {
		Object bean;

		try {
			bean = customization.beanParameterFactory().getParamValue(req, type, name, req.data());
		} catch (Exception e) {
			throw U.rte(e);
		}

		if (validate) {
			customization.validator().validate(req, bean);
		}

		return bean;
	}

}
