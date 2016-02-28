package org.rapidoid.setup;

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
import org.rapidoid.config.Config;
import org.rapidoid.http.*;
import org.rapidoid.http.handler.FastHttpHandler;
import org.rapidoid.http.processor.HttpProcessor;
import org.rapidoid.lambda.Mapper;

import java.lang.annotation.Annotation;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Admin {

	private static final Setup SETUP = Setup.ADMIN;

	public static synchronized OnRoute route(String verb, String path) {
		return SETUP.route(verb, path);
	}

	public static synchronized OnRoute get(String path) {
		return SETUP.get(path);
	}

	public static synchronized OnRoute post(String path) {
		return SETUP.post(path);
	}

	public static synchronized OnRoute put(String path) {
		return SETUP.put(path);
	}

	public static synchronized OnRoute delete(String path) {
		return SETUP.delete(path);
	}

	public static synchronized OnRoute patch(String path) {
		return SETUP.patch(path);
	}

	public static synchronized OnRoute options(String path) {
		return SETUP.options(path);
	}

	public static synchronized OnRoute head(String path) {
		return SETUP.head(path);
	}

	public static synchronized OnRoute trace(String path) {
		return SETUP.trace(path);
	}

	public static synchronized OnPage page(String path) {
		return SETUP.page(path);
	}

	public static synchronized Setup error(ErrorHandler onError) {
		return SETUP.onError(onError);
	}

	public static synchronized Setup req(ReqHandler handler) {
		return SETUP.req(handler);
	}

	public static synchronized Setup req(ReqRespHandler handler) {
		return SETUP.req(handler);
	}

	public static synchronized Setup req(FastHttpHandler handler) {
		return SETUP.req(handler);
	}

	public static synchronized Setup beans(Object... controllers) {
		return SETUP.beans(controllers);
	}

	public static synchronized Setup port(int port) {
		return SETUP.port(port);
	}

	public static synchronized Setup address(String address) {
		return SETUP.address(address);
	}

	public static Setup path(String... path) {
		return SETUP.path(path);
	}

	public static String[] path() {
		return SETUP.path();
	}

	public static synchronized Setup wrap(HttpWrapper... wrappers) {
		return SETUP.wrap(wrappers);
	}

	public static synchronized Setup processor(HttpProcessor listener) {
		return SETUP.processor(listener);
	}

	public static synchronized Setup instance() {
		return SETUP;
	}

	public static synchronized Setup staticFilesLookIn(String... possibleLocations) {
		return SETUP.staticFilesPath(possibleLocations);
	}

	public static synchronized Setup render(ViewRenderer renderer) {
		return SETUP.render(renderer);
	}

	public static Setup args(String... args) {
		return SETUP.args(args);
	}

	public static Setup bootstrap(String... args) {
		return SETUP.bootstrap(args);
	}

	@SuppressWarnings({"varargs"})
	public static OnAnnotated annotated(Class<? extends Annotation>... annotated) {
		return SETUP.annotated(annotated);
	}

	public static Setup deregister(String verb, String path) {
		return SETUP.deregister(verb, path);
	}

	public static Setup deregister(Object... controllers) {
		return SETUP.deregister(controllers);
	}

	public static Setup renderJson(Mapper<Object, String> jsonRenderer) {
		return SETUP.renderJson(jsonRenderer);
	}

	public static Setup parseJson(Mapper<String, Object> jsonParser) {
		return SETUP.parseJson(jsonParser);
	}

	public static Setup login(LoginProcessor loginProcessor) {
		return SETUP.login(loginProcessor);
	}

	public static Setup rolesOf(RolesProvider rolesProvider) {
		return SETUP.rolesOf(rolesProvider);
	}

	public static Config config() {
		return SETUP.config();
	}

}
