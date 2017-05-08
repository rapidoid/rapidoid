package org.rapidoid.setup;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.http.HttpRoutes;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.http.impl.RouteOptions;

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
@Since("4.3.0")
public class On extends RapidoidThing {

	public static synchronized Setup setup() {
		return Setup.on();
	}

	public static synchronized OnRoute route(String verb, String path) {
		return setup().route(verb, path);
	}

	public static synchronized OnRoute any(String path) {
		return setup().any(path);
	}

	public static synchronized OnRoute get(String path) {
		return setup().get(path);
	}

	public static synchronized OnRoute post(String path) {
		return setup().post(path);
	}

	public static synchronized OnRoute put(String path) {
		return setup().put(path);
	}

	public static synchronized OnRoute delete(String path) {
		return setup().delete(path);
	}

	public static synchronized OnRoute patch(String path) {
		return setup().patch(path);
	}

	public static synchronized OnRoute options(String path) {
		return setup().options(path);
	}

	public static synchronized OnRoute head(String path) {
		return setup().head(path);
	}

	public static synchronized OnRoute trace(String path) {
		return setup().trace(path);
	}

	public static synchronized OnRoute page(String path) {
		return setup().page(path);
	}

	public static synchronized Setup req(ReqHandler handler) {
		return setup().req(handler);
	}

	public static synchronized Setup req(ReqRespHandler handler) {
		return setup().req(handler);
	}

	public static synchronized Setup req(HttpHandler handler) {
		return setup().req(handler);
	}

	public static synchronized ServerSetup port(int port) {
		return new ServerSetup(Conf.ON).port(port);
	}

	public static synchronized ServerSetup address(String address) {
		return new ServerSetup(Conf.ON).address(address);
	}

	public static synchronized OnError error(Class<? extends Throwable> error) {
		return setup().error(error);
	}

	public static Setup deregister(String verb, String path) {
		return setup().deregister(verb, path);
	}

	public static Setup deregister(Object... controllers) {
		return setup().deregister(controllers);
	}

	public static Config config() {
		return setup().config();
	}

	public static Customization custom() {
		return setup().custom();
	}

	public static HttpRoutes routes() {
		return setup().routes();
	}

	public static RouteOptions defaults() {
		return setup().defaults();
	}

	public static OnChanges changes() {
		return OnChanges.INSTANCE;
	}
}
