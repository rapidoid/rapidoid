package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.net.TCPServer;

public interface HTTPServer extends TCPServer {

	HTTPServer route(String cmd, String url, Handler handler);

	HTTPServer route(String cmd, String url, String response);

	HTTPServer serve(String response);

	HTTPServer serve(Handler handler);

	HTTPServer get(String url, Handler handler);

	HTTPServer post(String url, Handler handler);

	HTTPServer put(String url, Handler handler);

	HTTPServer delete(String url, Handler handler);

	HTTPServer start();

	HTTPServer shutdown();

	HTTPInterceptor interceptor();

	HTTPServer interceptor(HTTPInterceptor interceptor);

}
