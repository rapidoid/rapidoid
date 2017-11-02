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
import org.rapidoid.env.Env;
import org.rapidoid.http.*;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.http.handler.ParamsAwareReqHandler;
import org.rapidoid.http.handler.ParamsAwareReqRespHandler;
import org.rapidoid.http.handler.StaticResourcesHandler;
import org.rapidoid.io.Res;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.AnsiColor;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Msc;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
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

	private static final AtomicLong ID_GEN = new AtomicLong();

	private static final int ROUTE_SETUP_WAITING_TIME_MS = Env.test() ? 0 : 500;

	private static final Pattern PATTERN_PATTERN = Pattern.compile("[^\\w/-]");

	private static final byte[] _POST = Constants.POST.getBytes();
	private static final byte[] _PUT = Constants.PUT.getBytes();
	private static final byte[] _DELETE = Constants.DELETE.getBytes();
	private static final byte[] _PATCH = Constants.PATCH.getBytes();
	private static final byte[] _OPTIONS = Constants.OPTIONS.getBytes();
	private static final byte[] _HEAD = Constants.HEAD.getBytes();
	private static final byte[] _TRACE = Constants.TRACE.getBytes();

	final BufMap<HttpHandler> getHandlers = new BufMapImpl<>();
	final BufMap<HttpHandler> postHandlers = new BufMapImpl<>();
	final BufMap<HttpHandler> putHandlers = new BufMapImpl<>();
	final BufMap<HttpHandler> deleteHandlers = new BufMapImpl<>();
	final BufMap<HttpHandler> patchHandlers = new BufMapImpl<>();
	final BufMap<HttpHandler> optionsHandlers = new BufMapImpl<>();
	final BufMap<HttpHandler> headHandlers = new BufMapImpl<>();
	final BufMap<HttpHandler> traceHandlers = new BufMapImpl<>();
	final BufMap<HttpHandler> anyHandlers = new BufMapImpl<>();

	final Map<PathPattern, HttpHandler> patternGetHandlers = new TreeMap<>();
	final Map<PathPattern, HttpHandler> patternPostHandlers = new TreeMap<>();
	final Map<PathPattern, HttpHandler> patternPutHandlers = new TreeMap<>();
	final Map<PathPattern, HttpHandler> patternDeleteHandlers = new TreeMap<>();
	final Map<PathPattern, HttpHandler> patternPatchHandlers = new TreeMap<>();
	final Map<PathPattern, HttpHandler> patternOptionsHandlers = new TreeMap<>();
	final Map<PathPattern, HttpHandler> patternHeadHandlers = new TreeMap<>();
	final Map<PathPattern, HttpHandler> patternTraceHandlers = new TreeMap<>();
	final Map<PathPattern, HttpHandler> patternAnyHandlers = new TreeMap<>();

	private final long id;
	private final String setupName;
	private final Customization customization;

	private volatile byte[] path1, path2, path3;
	private volatile HttpHandler handler1, handler2, handler3;

	final List<HttpHandler> genericHandlers = Coll.synchronizedList();

	private volatile HttpHandler staticResourcesHandler;

	private volatile HttpHandler builtInResourcesHandler;

	private final Set<Route> routes = Coll.synchronizedSet();

	private volatile boolean initialized;
	private volatile Runnable onInit;

	private volatile boolean stable;
	private volatile Date lastChangedAt = new Date();

	public HttpRoutesImpl(String setupName, Customization customization) {
		this.id = ID_GEN.incrementAndGet();
		this.setupName = setupName;
		this.customization = customization;
		this.staticResourcesHandler = new StaticResourcesHandler(customization);
		this.builtInResourcesHandler = new StaticResourcesHandler(customization);
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
					patternGetHandlers.put(pathPattern, handler);
				}
				break;

			case POST:
				if (!isPattern) {
					postHandlers.put(path, handler);
				} else {
					patternPostHandlers.put(pathPattern, handler);
				}
				break;

			case PUT:
				if (!isPattern) {
					putHandlers.put(path, handler);
				} else {
					patternPutHandlers.put(pathPattern, handler);
				}
				break;

			case DELETE:
				if (!isPattern) {
					deleteHandlers.put(path, handler);
				} else {
					patternDeleteHandlers.put(pathPattern, handler);
				}
				break;

			case PATCH:
				if (!isPattern) {
					patchHandlers.put(path, handler);
				} else {
					patternPatchHandlers.put(pathPattern, handler);
				}
				break;

			case OPTIONS:
				if (!isPattern) {
					optionsHandlers.put(path, handler);
				} else {
					patternOptionsHandlers.put(pathPattern, handler);
				}
				break;

			case HEAD:
				if (!isPattern) {
					headHandlers.put(path, handler);
				} else {
					patternHeadHandlers.put(pathPattern, handler);
				}
				break;

			case TRACE:
				if (!isPattern) {
					traceHandlers.put(path, handler);
				} else {
					patternTraceHandlers.put(pathPattern, handler);
				}
				break;

			case ANY:
				if (!isPattern) {
					anyHandlers.put(path, handler);
				} else {
					patternAnyHandlers.put(pathPattern, handler);
				}
				break;

			default:
				throw Err.notExpected();
		}

		notifyChanged();
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
					patternGetHandlers.remove(pathPattern);
				}
				break;

			case POST:
				if (!isPattern) {
					postHandlers.remove(path);
				} else {
					patternPostHandlers.remove(pathPattern);
				}
				break;

			case PUT:
				if (!isPattern) {
					putHandlers.remove(path);
				} else {
					patternPutHandlers.remove(pathPattern);
				}
				break;

			case DELETE:
				if (!isPattern) {
					deleteHandlers.remove(path);
				} else {
					patternDeleteHandlers.remove(pathPattern);
				}
				break;

			case PATCH:
				if (!isPattern) {
					patchHandlers.remove(path);
				} else {
					patternPatchHandlers.remove(pathPattern);
				}
				break;

			case OPTIONS:
				if (!isPattern) {
					optionsHandlers.remove(path);
				} else {
					patternOptionsHandlers.remove(pathPattern);
				}
				break;

			case HEAD:
				if (!isPattern) {
					headHandlers.remove(path);
				} else {
					patternHeadHandlers.remove(pathPattern);
				}
				break;

			case TRACE:
				if (!isPattern) {
					traceHandlers.remove(path);
				} else {
					patternTraceHandlers.remove(pathPattern);
				}
				break;

			case ANY:
				if (!isPattern) {
					anyHandlers.remove(path);
				} else {
					patternAnyHandlers.remove(pathPattern);
				}
				break;

			default:
				throw Err.notExpected();
		}

		notifyChanged();
	}

	private boolean isPattern(String path) {
		return PATTERN_PATTERN.matcher(path).find();
	}

	@Override
	public synchronized void addGenericHandler(HttpHandler handler) {
		Log.info("Registering generic handler", "!setup", setupName);
		genericHandlers.add(handler);
		notifyChanged();
	}

	@Override
	public synchronized void removeGenericHandler(HttpHandler handler) {
		genericHandlers.remove(handler);
		notifyChanged();
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

				if (handler == null) handler = anyHandlers.get(buf, path);
				if (handler == null) handler = matchByPattern(patternGetHandlers, buf.get(path));

				return handler;
			}

		} else if (BytesUtil.matches(bytes, verb, _POST, true)) {

			HandlerMatch handler = postHandlers.get(buf, path);

			if (handler == null) handler = anyHandlers.get(buf, path);
			if (handler == null) handler = matchByPattern(patternPostHandlers, buf.get(path));

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _PUT, true)) {

			HandlerMatch handler = putHandlers.get(buf, path);

			if (handler == null) handler = anyHandlers.get(buf, path);
			if (handler == null) handler = matchByPattern(patternPutHandlers, buf.get(path));

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _DELETE, true)) {

			HandlerMatch handler = deleteHandlers.get(buf, path);

			if (handler == null) handler = anyHandlers.get(buf, path);
			if (handler == null) handler = matchByPattern(patternDeleteHandlers, buf.get(path));

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _PATCH, true)) {

			HandlerMatch handler = patchHandlers.get(buf, path);

			if (handler == null) handler = anyHandlers.get(buf, path);
			if (handler == null) handler = matchByPattern(patternPatchHandlers, buf.get(path));

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _OPTIONS, true)) {

			HandlerMatch handler = optionsHandlers.get(buf, path);

			if (handler == null) handler = anyHandlers.get(buf, path);
			if (handler == null) handler = matchByPattern(patternOptionsHandlers, buf.get(path));

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _HEAD, true)) {

			HandlerMatch handler = headHandlers.get(buf, path);

			if (handler == null) handler = anyHandlers.get(buf, path);
			if (handler == null) handler = matchByPattern(patternHeadHandlers, buf.get(path));

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _TRACE, true)) {

			HandlerMatch handler = traceHandlers.get(buf, path);

			if (handler == null) handler = anyHandlers.get(buf, path);
			if (handler == null) handler = matchByPattern(patternTraceHandlers, buf.get(path));

			return handler;
		}

		return null; // no handler
	}

	private HandlerMatch matchByPattern(Map<PathPattern, HttpHandler> handlers, String path) {
		for (Map.Entry<PathPattern, HttpHandler> e : handlers.entrySet()) {

			PathPattern pattern = e.getKey();
			Map<String, String> params = pattern.match(path);

			if (params != null) {
				HttpHandler handler = e.getValue();
				Route route = handler.getRoute();

				return new HandlerMatchWithParams(handler, params, route);
			}
		}

		if (handlers != patternAnyHandlers) return matchByPattern(patternAnyHandlers, path);

		return null;
	}

	@Override
	public synchronized void on(String verb, String path, HttpHandler handler) {
		addOrRemove(true, verb, path, handler);
	}

	@Override
	public synchronized void on(String verb, String path, ReqHandler handler) {
		HttpHandler hnd = new ParamsAwareReqHandler(null, null, new RouteOptions(), handler);
		addOrRemove(true, verb, path, hnd);
	}

	@Override
	public synchronized void on(String verb, String path, ReqRespHandler handler) {
		HttpHandler hnd = new ParamsAwareReqRespHandler(null, null, new RouteOptions(), handler);
		addOrRemove(true, verb, path, hnd);
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

			TransactionMode txm = opts.transaction();
			String tx = txm != TransactionMode.NONE ? AnsiColor.bold(txm.name()) : txm.name();

			int space = Math.max(45 - verbs.length() - path.length(), 1);
			Log.info(httpVerbColor(verbs) + AnsiColor.bold(" " + path) + Str.mul(" ", space), "setup", setupName,
				"!roles", opts.roles(), "transaction", tx, "mvc", opts.mvc(), "cacheTTL", opts.cacheTTL());

		} else {
			Log.info("Deregistering handler", "setup", setupName, "!verbs", verbs, "!path", path);
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

		notifyChanged();
	}

	private String httpVerbColor(String verb) {
		switch (verb.toUpperCase()) {
			case "ANY":
			case "GET,POST":
				return AnsiColor.yellow(verb);

			case "GET":
				return AnsiColor.lightBlue(verb);

			default:
				return AnsiColor.lightPurple(verb);
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
		anyHandlers.clear();
		genericHandlers.clear();

		patternGetHandlers.clear();
		patternPostHandlers.clear();
		patternPutHandlers.clear();
		patternDeleteHandlers.clear();
		patternPatchHandlers.clear();
		patternOptionsHandlers.clear();
		patternHeadHandlers.clear();
		patternTraceHandlers.clear();
		patternAnyHandlers.clear();

		staticResourcesHandler = new StaticResourcesHandler(customization);

		routes.clear();

		initialized = false;
		onInit = null;

		customization.reset();
		stable = false;
		lastChangedAt = null;

		notifyChanged();
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

	public HttpHandler builtInResourcesHandler() {
		return builtInResourcesHandler;
	}

	@Override
	public Runnable onInit() {
		return onInit;
	}

	@Override
	public void onInit(Runnable onInit) {
		this.onInit = onInit;
		notifyChanged();
	}

	@Override
	public boolean isEmpty() {
		return routes.isEmpty() && genericHandlers.isEmpty() && staticResourcesHandler == null;
	}

	private synchronized void initialize() {
		if (initialized) return;

		initialized = true;
		Runnable initializer = onInit;

		if (initializer != null) initializer.run();

		notifyChanged();
	}

	public Date lastChangedAt() {
		return lastChangedAt;
	}

	private void notifyChanged() {
		lastChangedAt = new Date();
	}

	public boolean ready() {
		long lastChangedAt = lastChangedAt().getTime();
		return !isEmpty() && Msc.timedOut(lastChangedAt, ROUTE_SETUP_WAITING_TIME_MS);
	}

	public void waitToStabilize() {
		while (!stable) {
			U.sleep(1);
			if (ready()) {
				synchronized (this) {
					if (!stable) {
						stable = true;
						Log.debug("Stabilized HTTP routes");
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "HttpRoutesImpl{" +
			"id=" + id +
			", setup='" + setupName + '\'' +
			'}';
	}
}
