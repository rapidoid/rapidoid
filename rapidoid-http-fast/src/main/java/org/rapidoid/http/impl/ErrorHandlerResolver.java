package org.rapidoid.http.impl;

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
