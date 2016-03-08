package org.rapidoid.http;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.commons.MediaType;

import java.util.List;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface RouteConfig {

	MediaType contentType();

	RouteConfig contentType(MediaType contentType);

	String view();

	RouteOptions view(String view);

	boolean mvc();

	RouteOptions mvc(boolean mvc);

	TransactionMode transactionMode();

	RouteOptions transactionMode(TransactionMode transactionMode);

	Set<String> roles();

	RouteOptions roles(Set<String> roles);

	List<HttpWrapper> wrappers();

	RouteOptions wrap(HttpWrapper[] wrappers);
}
