package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-server
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

	public static synchronized OnAction get(String path) {
		return Setup.DEFAULT.get(path);
	}

	public static synchronized OnAction post(String path) {
		return Setup.DEFAULT.post(path);
	}

	public static synchronized OnAction put(String path) {
		return Setup.DEFAULT.put(path);
	}

	public static synchronized OnAction delete(String path) {
		return Setup.DEFAULT.delete(path);
	}

	public static synchronized OnAction patch(String path) {
		return Setup.DEFAULT.patch(path);
	}

	public static synchronized OnAction options(String path) {
		return Setup.DEFAULT.options(path);
	}

	public static synchronized OnAction head(String path) {
		return Setup.DEFAULT.head(path);
	}

	public static synchronized OnAction trace(String path) {
		return Setup.DEFAULT.trace(path);
	}

	public static synchronized OnPage page(String path) {
		return Setup.DEFAULT.page(path);
	}

	public static synchronized Setup error(ErrorHandler onError) {
		return Setup.DEFAULT.onError(onError);
	}

	public static synchronized Setup req(ReqHandler handler) {
		return Setup.DEFAULT.req(handler);
	}

	public static synchronized Setup req(ReqRespHandler handler) {
		return Setup.DEFAULT.req(handler);
	}

	public static synchronized Setup req(FastHttpHandler handler) {
		return Setup.DEFAULT.req(handler);
	}

	public static synchronized Setup beans(Object... controllers) {
		return Setup.DEFAULT.beans(controllers);
	}

	public static synchronized Setup port(int port) {
		return Setup.DEFAULT.port(port);
	}

	public static synchronized Setup address(String address) {
		return Setup.DEFAULT.address(address);
	}

	public static Setup path(String... path) {
		return Setup.DEFAULT.path(path);
	}

	public static String[] path() {
		return Setup.DEFAULT.path();
	}

	public static synchronized Setup wrap(HttpWrapper... wrappers) {
		return Setup.DEFAULT.wrap(wrappers);
	}

	public static synchronized Setup processor(HttpProcessor listener) {
		return Setup.DEFAULT.processor(listener);
	}

	public static synchronized Setup getDefaultSetup() {
		return Setup.DEFAULT;
	}

	public static Setup createSetup(String name) {
		return new Setup(name, "0.0.0.0", 8888, ServerSetupType.CUSTOM, IoC.createContext());
	}

	public static synchronized Setup staticFilesLookIn(String... possibleLocations) {
		return Setup.DEFAULT.staticFilesPath(possibleLocations);
	}

	public static synchronized Setup render(ViewRenderer renderer) {
		return Setup.DEFAULT.render(renderer);
	}

	public static Setup args(String... args) {
		Conf.args(args);
		return Setup.DEFAULT;
	}

	public static Setup bootstrap() {
		return Setup.DEFAULT.bootstrap();
	}

	@SuppressWarnings({ "varargs" })
	public static OnAnnotated annotated(Class<? extends Annotation>... annotated) {
		return Setup.DEFAULT.annotated(annotated);
	}

	public static Setup admin() {
		return Setup.ADMIN;
	}

	public static Setup dev() {
		return Setup.DEV;
	}

	public static Setup deregister(String verb, String path) {
		return Setup.DEFAULT.deregister(verb, path);
	}

	public static Setup deregister(Object... controllers) {
		return Setup.DEFAULT.deregister(controllers);
	}

	public static OnChanges changes() {
		return Setup.onChanges();
	}

}
