package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.buffer.Buf;
import org.rapidoid.bufstruct.BufMap;
import org.rapidoid.bufstruct.BufMapImpl;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Err;
import org.rapidoid.commons.Str;
import org.rapidoid.data.BufRange;
import org.rapidoid.http.HttpRoutes;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.Route;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.http.handler.ParamsAwareReqHandler;
import org.rapidoid.http.handler.StaticResourcesHandler;
import org.rapidoid.io.Res;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.AnsiColor;
import org.rapidoid.util.Constants;

import java.util.*;
import java.util.regex.Pattern;

/*
 * #%L
 * rapidoid-http-fast
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
@Since("5.1.0")
public class HttpRoutesImpl extends RapidoidThing implements HttpRoutes {

	private static final Pattern PATTERN_PATTERN = Pattern.compile("[^\\w/-]");

	private static final byte[] _POST = Constants.POST.getBytes();
	private static final byte[] _PUT = Constants.PUT.getBytes();
	private static final byte[] _DELETE = Constants.DELETE.getBytes();
	private static final byte[] _PATCH = Constants.PATCH.getBytes();
	private static final byte[] _OPTIONS = Constants.OPTIONS.getBytes();
	private static final byte[] _HEAD = Constants.HEAD.getBytes();
	private static final byte[] _TRACE = Constants.TRACE.getBytes();

	final BufMap<HttpHandler> getHandlers = new BufMapImpl<HttpHandler>();
	final BufMap<HttpHandler> postHandlers = new BufMapImpl<HttpHandler>();
	final BufMap<HttpHandler> putHandlers = new BufMapImpl<HttpHandler>();
	final BufMap<HttpHandler> deleteHandlers = new BufMapImpl<HttpHandler>();
	final BufMap<HttpHandler> patchHandlers = new BufMapImpl<HttpHandler>();
	final BufMap<HttpHandler> optionsHandlers = new BufMapImpl<HttpHandler>();
	final BufMap<HttpHandler> headHandlers = new BufMapImpl<HttpHandler>();
	final BufMap<HttpHandler> traceHandlers = new BufMapImpl<HttpHandler>();

	final Map<PathPattern, HttpHandler> paternGetHandlers = new LinkedHashMap<PathPattern, HttpHandler>();
	final Map<PathPattern, HttpHandler> paternPostHandlers = new LinkedHashMap<PathPattern, HttpHandler>();
	final Map<PathPattern, HttpHandler> paternPutHandlers = new LinkedHashMap<PathPattern, HttpHandler>();
	final Map<PathPattern, HttpHandler> paternDeleteHandlers = new LinkedHashMap<PathPattern, HttpHandler>();
	final Map<PathPattern, HttpHandler> paternPatchHandlers = new LinkedHashMap<PathPattern, HttpHandler>();
	final Map<PathPattern, HttpHandler> paternOptionsHandlers = new LinkedHashMap<PathPattern, HttpHandler>();
	final Map<PathPattern, HttpHandler> paternHeadHandlers = new LinkedHashMap<PathPattern, HttpHandler>();
	final Map<PathPattern, HttpHandler> paternTraceHandlers = new LinkedHashMap<PathPattern, HttpHandler>();

	private final Customization customization;

	private volatile byte[] path1, path2, path3;
	private volatile HttpHandler handler1, handler2, handler3;

	final List<HttpHandler> genericHandlers = Coll.synchronizedList();

	private volatile HttpHandler staticResourcesHandler;

	private final Set<Route> routes = Coll.synchronizedSet();

	private volatile boolean initialized;
	private volatile Runnable onInit;

	public HttpRoutesImpl(Customization customization) {
		this.customization = customization;
		staticResourcesHandler = new StaticResourcesHandler(customization);
	}

	private void register(HttpVerb verb, String path, HttpHandler handler) {
		boolean isPattern = isPattern(path);
		PathPattern pathPattern = isPattern ? PathPattern.from(path) : null;

		RouteImpl route = new RouteImpl(verb, path, handler, handler.options());
		handler.setRoute(route);
		routes.add(route);

		switch (verb) {
			case GET:
				if (!isPattern) {
					if (path1 == null) {
						path1 = path.getBytes();
						handler1 = handler;

					} else if (path2 == null) {
						path2 = path.getBytes();
						handler2 = handler;

					} else if (path3 == null) {
						path3 = path.getBytes();
						handler3 = handler;

					} else {
						getHandlers.put(path, handler);
					}
				} else {
					paternGetHandlers.put(pathPattern, handler);
				}
				break;

			case POST:
				if (!isPattern) {
					postHandlers.put(path, handler);
				} else {
					paternPostHandlers.put(pathPattern, handler);
				}
				break;

			case PUT:
				if (!isPattern) {
					putHandlers.put(path, handler);
				} else {
					paternPutHandlers.put(pathPattern, handler);
				}
				break;

			case DELETE:
				if (!isPattern) {
					deleteHandlers.put(path, handler);
				} else {
					paternDeleteHandlers.put(pathPattern, handler);
				}
				break;

			case PATCH:
				if (!isPattern) {
					patchHandlers.put(path, handler);
				} else {
					paternPatchHandlers.put(pathPattern, handler);
				}
				break;

			case OPTIONS:
				if (!isPattern) {
					optionsHandlers.put(path, handler);
				} else {
					paternOptionsHandlers.put(pathPattern, handler);
				}
				break;

			case HEAD:
				if (!isPattern) {
					headHandlers.put(path, handler);
				} else {
					paternHeadHandlers.put(pathPattern, handler);
				}
				break;

			case TRACE:
				if (!isPattern) {
					traceHandlers.put(path, handler);
				} else {
					paternTraceHandlers.put(pathPattern, handler);
				}
				break;

			default:
				throw Err.notExpected();
		}
	}

	private void deregister(HttpVerb verb, String path) {
		boolean isPattern = isPattern(path);
		PathPattern pathPattern = isPattern ? PathPattern.from(path) : null;

		routes.remove(RouteImpl.matching(verb, path));

		switch (verb) {
			case GET:
				if (!isPattern) {
					if (path1 != null && new String(path1).equals(path)) {
						path1 = null;
					}

					if (path2 != null && new String(path2).equals(path)) {
						path2 = null;
					}

					if (path3 != null && new String(path3).equals(path)) {
						path3 = null;
					}

					getHandlers.remove(path);
				} else {
					paternGetHandlers.remove(pathPattern);
				}
				break;

			case POST:
				if (!isPattern) {
					postHandlers.remove(path);
				} else {
					paternPostHandlers.remove(pathPattern);
				}
				break;

			case PUT:
				if (!isPattern) {
					putHandlers.remove(path);
				} else {
					paternPutHandlers.remove(pathPattern);
				}
				break;

			case DELETE:
				if (!isPattern) {
					deleteHandlers.remove(path);
				} else {
					paternDeleteHandlers.remove(pathPattern);
				}
				break;

			case PATCH:
				if (!isPattern) {
					patchHandlers.remove(path);
				} else {
					paternPatchHandlers.remove(pathPattern);
				}
				break;

			case OPTIONS:
				if (!isPattern) {
					optionsHandlers.remove(path);
				} else {
					paternOptionsHandlers.remove(pathPattern);
				}
				break;

			case HEAD:
				if (!isPattern) {
					headHandlers.remove(path);
				} else {
					paternHeadHandlers.remove(pathPattern);
				}
				break;

			case TRACE:
				if (!isPattern) {
					traceHandlers.remove(path);
				} else {
					paternTraceHandlers.remove(pathPattern);
				}
				break;

			default:
				throw Err.notExpected();
		}

	}

	private boolean isPattern(String path) {
		return PATTERN_PATTERN.matcher(path).find();
	}

	@Override
	public synchronized void addGenericHandler(HttpHandler handler) {
		genericHandlers.add(handler);
	}

	@Override
	public synchronized void removeGenericHandler(HttpHandler handler) {
		genericHandlers.remove(handler);
	}

	public HandlerMatch findHandler(Buf buf, boolean isGet, BufRange verb, BufRange path) {
		Bytes bytes = buf.bytes();

		if (isGet) {
			if (path1 != null && BytesUtil.matches(bytes, path, path1, true)) {
				return handler1;

			} else if (path2 != null && BytesUtil.matches(bytes, path, path2, true)) {
				return handler2;

			} else if (path3 != null && BytesUtil.matches(bytes, path, path3, true)) {
				return handler3;

			} else {
				HandlerMatch handler = getHandlers.get(buf, path);

				if (handler == null && !paternGetHandlers.isEmpty()) {
					handler = matchByPattern(HttpVerb.GET, paternGetHandlers, buf.get(path));
				}

				return handler;
			}

		} else if (BytesUtil.matches(bytes, verb, _POST, true)) {
			HandlerMatch handler = postHandlers.get(buf, path);

			if (handler == null && !paternPostHandlers.isEmpty()) {
				handler = matchByPattern(HttpVerb.POST, paternPostHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _PUT, true)) {
			HandlerMatch handler = putHandlers.get(buf, path);

			if (handler == null && !paternPutHandlers.isEmpty()) {
				handler = matchByPattern(HttpVerb.PUT, paternPutHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _DELETE, true)) {
			HandlerMatch handler = deleteHandlers.get(buf, path);

			if (handler == null && !paternDeleteHandlers.isEmpty()) {
				handler = matchByPattern(HttpVerb.DELETE, paternDeleteHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _PATCH, true)) {
			HandlerMatch handler = patchHandlers.get(buf, path);

			if (handler == null && !paternPatchHandlers.isEmpty()) {
				handler = matchByPattern(HttpVerb.PATCH, paternPatchHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _OPTIONS, true)) {
			HandlerMatch handler = optionsHandlers.get(buf, path);

			if (handler == null && !paternOptionsHandlers.isEmpty()) {
				handler = matchByPattern(HttpVerb.OPTIONS, paternOptionsHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _HEAD, true)) {
			HandlerMatch handler = headHandlers.get(buf, path);

			if (handler == null && !paternHeadHandlers.isEmpty()) {
				handler = matchByPattern(HttpVerb.HEAD, paternHeadHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _TRACE, true)) {
			HandlerMatch handler = traceHandlers.get(buf, path);

			if (handler == null && !paternTraceHandlers.isEmpty()) {
				handler = matchByPattern(HttpVerb.TRACE, paternTraceHandlers, buf.get(path));
			}

			return handler;
		}

		return null; // no handler
	}

	private HandlerMatch matchByPattern(HttpVerb verb, Map<PathPattern, HttpHandler> handlers, String path) {
		for (Map.Entry<PathPattern, HttpHandler> e : handlers.entrySet()) {

			PathPattern pattern = e.getKey();
			Map<String, String> params = pattern.match(path);

			if (params != null) {
				HttpHandler handler = e.getValue();
				Route route = handler.getRoute();

				return new HandlerMatchWithParams(handler, params, route);
			}
		}

		return null;
	}

	@Override
	public synchronized void on(String verb, String path, HttpHandler handler) {
		addOrRemove(true, verb, path, handler);
	}

	@Override
	public synchronized void on(String verb, String path, ReqHandler handler) {
		addOrRemove(true, verb, path, handler(handler, new RouteOptions()));
	}

	public HttpHandler handler(ReqHandler reqHandler, RouteOptions options) {
		return new ParamsAwareReqHandler(null, null, options, reqHandler);
	}

	@Override
	public synchronized void remove(String verb, String path) {
		addOrRemove(false, verb, path, null);
	}

	private void addOrRemove(boolean add, String verbs, String path, HttpHandler handler) {
		U.notNull(verbs, "HTTP verbs");
		U.notNull(path, "HTTP path");

		U.must(path.startsWith("/"), "The URI must start with '/', but found: '%s'", path);

		initialize();

		if (add) {
			U.notNull(handler, "HTTP handler");
		}

		verbs = verbs.toUpperCase();
		if (path.length() > 1) {
			path = Str.trimr(path, "/");
		}

		if (add) {
			RouteOptions opts = handler.options();

			TransactionMode txm = opts.transactionMode();
			String tx = txm != TransactionMode.NONE ? AnsiColor.bold(txm.name()) : txm.name();

			Log.info("Registering handler", "!setup", this.customization.name(), "!verbs", verbs, "!path", path,
				"!roles", opts.roles(), "tx", tx, "mvc", opts.mvc(), "cacheTTL", opts.cacheTTL());
		} else {
			Log.info("Deregistering handler", "setup", this.customization.name(), "!verbs", verbs, "!path", path);
		}

		for (String vrb : verbs.split(",")) {
			HttpVerb verb = HttpVerb.from(vrb);

			if (add) {
				deregister(verb, path);
				register(verb, path, handler);
			} else {
				deregister(verb, path);
			}
		}
	}

	@Override
	public synchronized void reset() {
		path1 = path2 = path3 = null;
		handler1 = handler2 = handler3 = null;

		getHandlers.clear();
		postHandlers.clear();
		putHandlers.clear();
		deleteHandlers.clear();
		optionsHandlers.clear();
		genericHandlers.clear();

		paternGetHandlers.clear();
		paternPostHandlers.clear();
		paternPutHandlers.clear();
		paternDeleteHandlers.clear();
		paternPatchHandlers.clear();
		paternOptionsHandlers.clear();
		paternHeadHandlers.clear();
		paternTraceHandlers.clear();

		staticResourcesHandler = new StaticResourcesHandler(customization);

		routes.clear();

		initialized = false;
		onInit = null;
	}

	@Override
	public Set<Route> all() {
		return Collections.unmodifiableSet(routes);
	}

	@Override
	public Set<Route> allAdmin() {
		Set<Route> routes = U.set(all());

		for (Iterator<Route> it = routes.iterator(); it.hasNext(); ) {
			Route route = it.next();
			if (!route.config().zone().equalsIgnoreCase("admin")) {
				it.remove();
			}
		}

		return routes;
	}

	@Override
	public Set<Route> allNonAdmin() {
		Set<Route> routes = U.set(all());

		for (Iterator<Route> it = routes.iterator(); it.hasNext(); ) {
			Route route = it.next();
			if (route.config().zone().equalsIgnoreCase("admin")) {
				it.remove();
			}
		}

		return routes;
	}

	@Override
	public Customization custom() {
		return customization;
	}

	@Override
	public Route find(HttpVerb verb, String path) {
		for (Route route : all()) {
			if (route.verb().equals(verb) && route.path().equals(path)) {
				return route;
			}
		}

		return null;
	}

	@Override
	public boolean hasRouteOrResource(HttpVerb verb, String uri) {
		if (verb == HttpVerb.GET) {
			String[] staticFilesLocations = custom().staticFilesPath();
			if (U.notEmpty(staticFilesLocations)) {
				String filename = Str.triml(uri, '/');
				if (filename.isEmpty()) filename = "index.html";
				if (Res.from(filename, staticFilesLocations).exists()) return true;
			}
		}

		return find(verb, uri) != null;
	}

	public List<HttpHandler> genericHandlers() {
		return genericHandlers;
	}

	public HttpHandler staticResourcesHandler() {
		return staticResourcesHandler;
	}

	@Override
	public Runnable onInit() {
		return onInit;
	}

	@Override
	public void onInit(Runnable onInit) {
		this.onInit = onInit;
	}

	private synchronized void initialize() {
		if (initialized) return;

		initialized = true;
		Runnable initializer = onInit;

		if (initializer != null) initializer.run();
	}

}
