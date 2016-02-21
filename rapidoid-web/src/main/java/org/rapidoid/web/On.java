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
import org.rapidoid.http.processor.HttpProcessor;
import org.rapidoid.ioc.IoC;
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

	private static final Setup DEFAULT_SETUP = new Setup("http", "0.0.0.0", 8888, ServerSetupType.DEFAULT, IoC.defaultContext());

	private static final Setup ADMIN_SETUP = new Setup("admin", "0.0.0.0", 8889, ServerSetupType.ADMIN, IoC.defaultContext());

	private static final Setup DEV_SETUP = new Setup("dev", "127.0.0.1", 8887, ServerSetupType.DEV, IoC.defaultContext());

	public static synchronized OnAction get(String path) {
		return DEFAULT_SETUP.get(path);
	}

	public static synchronized OnAction post(String path) {
		return DEFAULT_SETUP.post(path);
	}

	public static synchronized OnAction put(String path) {
		return DEFAULT_SETUP.put(path);
	}

	public static synchronized OnAction delete(String path) {
		return DEFAULT_SETUP.delete(path);
	}

	public static synchronized OnAction patch(String path) {
		return DEFAULT_SETUP.patch(path);
	}

	public static synchronized OnAction options(String path) {
		return DEFAULT_SETUP.options(path);
	}

	public static synchronized OnAction head(String path) {
		return DEFAULT_SETUP.head(path);
	}

	public static synchronized OnAction trace(String path) {
		return DEFAULT_SETUP.trace(path);
	}

	public static synchronized OnPage page(String path) {
		return DEFAULT_SETUP.page(path);
	}

	public static synchronized Setup error(ErrorHandler onError) {
		return DEFAULT_SETUP.onError(onError);
	}

	public static synchronized Setup req(ReqHandler handler) {
		return DEFAULT_SETUP.req(handler);
	}

	public static synchronized Setup req(ReqRespHandler handler) {
		return DEFAULT_SETUP.req(handler);
	}

	public static synchronized Setup req(FastHttpHandler handler) {
		return DEFAULT_SETUP.req(handler);
	}

	public static synchronized Setup beans(Object... controllers) {
		return DEFAULT_SETUP.beans(controllers);
	}

	public static synchronized Setup port(int port) {
		return DEFAULT_SETUP.port(port);
	}

	public static synchronized Setup address(String address) {
		return DEFAULT_SETUP.address(address);
	}

	public static Setup path(String... path) {
		return DEFAULT_SETUP.path(path);
	}

	public static String[] path() {
		return DEFAULT_SETUP.path();
	}

	public static synchronized Setup wrap(HttpWrapper... wrappers) {
		return DEFAULT_SETUP.wrap(wrappers);
	}

	public static synchronized Setup processor(HttpProcessor listener) {
		return DEFAULT_SETUP.processor(listener);
	}

	public static synchronized Setup getDefaultSetup() {
		return DEFAULT_SETUP;
	}

	public static Setup createSetup(String name) {
		return new Setup(name, "0.0.0.0", 8888, ServerSetupType.CUSTOM, IoC.createContext());
	}

	public static synchronized Setup staticFilesLookIn(String... possibleLocations) {
		return DEFAULT_SETUP.staticFilesPath(possibleLocations);
	}

	public static synchronized Setup render(ViewRenderer renderer) {
		return DEFAULT_SETUP.render(renderer);
	}

	public static Setup args(String... args) {
		Conf.args(args);
		return DEFAULT_SETUP;
	}

	public static Setup bootstrap() {
		return DEFAULT_SETUP.bootstrap();
	}

	@SafeVarargs
	@SuppressWarnings({"varargs"})
	public static OnAnnotated annotated(Class<? extends Annotation>... annotated) {
		return DEFAULT_SETUP.annotated(annotated);
	}

	public static Setup admin() {
		return ADMIN_SETUP;
	}

	public static Setup dev() {
		return DEV_SETUP;
	}

	public static Setup deregister(String verb, String path) {
		return DEFAULT_SETUP.deregister(verb, path);
	}

	public static Setup deregister(Object... controllers) {
		return DEFAULT_SETUP.deregister(controllers);
	}

	public static OnChanges changes() {
		return Setup.onChanges();
	}

}
