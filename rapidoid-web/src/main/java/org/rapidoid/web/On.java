package org.rapidoid.web;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.data.JSON;
import org.rapidoid.http.*;
import org.rapidoid.http.handler.FastHttpHandler;
import org.rapidoid.http.listener.FastHttpListener;
import org.rapidoid.job.Jobs;

import java.lang.annotation.Annotation;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class On {

	static {
		RapidoidInitializer.initialize();

		Jobs.execute(new Runnable() {
			@Override
			public void run() {
				JSON.warmup();
			}
		});
	}

	private static final ServerSetup DEFAULT_SERVER = new ServerSetup("http", "0.0.0.0", 8888, ServerSetupType.DEFAULT);

	private static final ServerSetup ADMIN_SERVER = new ServerSetup("admin", "0.0.0.0", 8889, ServerSetupType.ADMIN);

	private static final ServerSetup DEV_SERVER = new ServerSetup("dev", "127.0.0.1", 8887, ServerSetupType.DEV);

	public static synchronized OnAction get(String path) {
		return DEFAULT_SERVER.get(path);
	}

	public static synchronized OnAction post(String path) {
		return DEFAULT_SERVER.post(path);
	}

	public static synchronized OnAction put(String path) {
		return DEFAULT_SERVER.put(path);
	}

	public static synchronized OnAction delete(String path) {
		return DEFAULT_SERVER.delete(path);
	}

	public static synchronized OnAction patch(String path) {
		return DEFAULT_SERVER.patch(path);
	}

	public static synchronized OnAction options(String path) {
		return DEFAULT_SERVER.options(path);
	}

	public static synchronized OnAction head(String path) {
		return DEFAULT_SERVER.head(path);
	}

	public static synchronized OnAction trace(String path) {
		return DEFAULT_SERVER.trace(path);
	}

	public static synchronized OnPage page(String path) {
		return DEFAULT_SERVER.page(path);
	}

	public static synchronized ServerSetup error(ErrorHandler onError) {
		return DEFAULT_SERVER.onError(onError);
	}

	public static synchronized ServerSetup req(ReqHandler handler) {
		return DEFAULT_SERVER.req(handler);
	}

	public static synchronized ServerSetup req(ReqRespHandler handler) {
		return DEFAULT_SERVER.req(handler);
	}

	public static synchronized ServerSetup req(FastHttpHandler handler) {
		return DEFAULT_SERVER.req(handler);
	}

	public static synchronized ServerSetup req(Object... controllers) {
		return DEFAULT_SERVER.req(controllers);
	}

	public static synchronized ServerSetup port(int port) {
		return DEFAULT_SERVER.port(port);
	}

	public static synchronized ServerSetup address(String address) {
		return DEFAULT_SERVER.address(address);
	}

	public static ServerSetup path(String... path) {
		return DEFAULT_SERVER.path(path);
	}

	public static String[] path() {
		return DEFAULT_SERVER.path();
	}

	public static synchronized ServerSetup defaultWrap(HttpWrapper... wrappers) {
		return DEFAULT_SERVER.defaultWrap(wrappers);
	}

	public static synchronized ServerSetup listener(FastHttpListener listener) {
		return DEFAULT_SERVER.listener(listener);
	}

	public static synchronized ServerSetup getDefaultSetup() {
		return DEFAULT_SERVER;
	}

	public static ServerSetup createServer(String name) {
		return new ServerSetup(name, "0.0.0.0", 8888, ServerSetupType.CUSTOM);
	}

	public static synchronized ServerSetup staticFilesLookIn(String... possibleLocations) {
		return DEFAULT_SERVER.staticFilesPath(possibleLocations);
	}

	public static synchronized ServerSetup render(ViewRenderer renderer) {
		return DEFAULT_SERVER.render(renderer);
	}

	public static ServerSetup args(String... args) {
		Conf.args(args);
		return DEFAULT_SERVER;
	}

	public static ServerSetup bootstrap() {
		return DEFAULT_SERVER.bootstrap();
	}

	@SafeVarargs
	@SuppressWarnings({"varargs"})
	public static OnAnnotated annotated(Class<? extends Annotation>... annotated) {
		return DEFAULT_SERVER.annotated(annotated);
	}

	public static ServerSetup admin() {
		return ADMIN_SERVER;
	}

	public static ServerSetup dev() {
		return DEV_SERVER;
	}

	public static ServerSetup deregister(String verb, String path) {
		return DEFAULT_SERVER.deregister(verb, path);
	}

	public static ServerSetup deregister(Object... controllers) {
		return DEFAULT_SERVER.deregister(controllers);
	}

	public static OnChanges changes() {
		return DEFAULT_SERVER.changes();
	}

}
