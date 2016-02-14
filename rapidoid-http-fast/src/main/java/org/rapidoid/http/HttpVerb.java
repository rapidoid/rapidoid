package org.rapidoid.http;

import org.rapidoid.u.U;

public enum HttpVerb {

	GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD, TRACE;

	public static HttpVerb from(String verb) {
		try {
			return valueOf(verb.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw U.rte("Unsupported HTTP verb: " + verb);
		}
	}

}
