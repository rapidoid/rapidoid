package org.rapidoid.http.impl;

/*-
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.ErrorHandler;

public class ErrorHandlerResolver extends RapidoidThing {

	public ErrorHandler findErrorHandlerByType(Customization custom, Throwable error) {
		ErrorHandler handler;

		do {
			handler = findHandlerForChainOfErrors(custom, error);

			custom = custom.defaults(); // if no success -> try with more generic customization

		} while (handler == null && custom != null);

		return handler;
	}

	private ErrorHandler findHandlerForChainOfErrors(Customization custom, Throwable error) {

		ErrorHandler handler = custom.errorHandlers().findByType(error.getClass());

		if (handler == null && error.getCause() != null) {
			handler = findHandlerForChainOfErrors(custom, error.getCause());
		}

		return handler;
	}

}
