/*-
 * #%L
 * rapidoid-rest
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.setup;

import org.rapidoid.config.Config;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpRoutes;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.http.processor.HttpProcessor;
import org.rapidoid.ioc.Beans;
import org.rapidoid.ioc.IoCContext;
import org.rapidoid.net.Server;
import org.rapidoid.web.Screen;
import org.rapidoid.web.WebSetup;

import java.util.Map;

public interface Setup extends WebSetup {

	void destroy();

	FastHttp http();

	void activate();

	OnRoute on(String verb, String path);

	OnRoute any(String path);

	OnRoute get(String path);

	OnRoute post(String path);

	OnRoute put(String path);

	OnRoute delete(String path);

	OnRoute patch(String path);

	OnRoute options(String path);

	OnRoute head(String path);

	OnRoute trace(String path);

	OnRoute page(String path);

	Setup req(ReqHandler handler);

	Setup req(ReqRespHandler handler);

	Setup req(HttpHandler handler);

	Setup beans(Object... beans);

	Setup port(int port);

	Setup address(String address);

	Setup processor(HttpProcessor processor);

	Setup shutdown();

	Setup halt();

	void reset();

	Server server();

	Map<String, Object> attributes();

	Setup scan(String... packages);

	Setup deregister(String verb, String path);

	Setup deregister(Object... controllers);

	IoCContext context();

	void reload();

	Config config();

	Customization custom();

	HttpRoutes routes();

	String name();

	RouteOptions defaults();

	String zone();

	Screen gui();

	boolean isRunning();

	int port();

	String address();

	OnError error(Class<? extends Throwable> error);

	void register(Beans beans);

	void onInit(Runnable onInit);

	boolean autoActivating();

	Setup autoActivating(boolean autoActivating);

}
