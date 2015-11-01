package org.rapidoid.app;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.util.Map;
import java.util.Map.Entry;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.BeanProperties;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchangeImpl;
import org.rapidoid.http.HttpProtocol;
import org.rapidoid.io.Res;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.2.0")
public class Scripting {

	public static boolean runDynamicScript(final HttpExchangeImpl x, final boolean hasEvent,
			final Map<String, Object> config) {
		final CompiledScript script = script(x);

		if (script != null) {
			x.async();
			Jobs.execute(new Runnable() {
				@Override
				public void run() {
					runScript(x, script, hasEvent, config);
				}
			});
			return true;
		} else {
			return false;
		}
	}

	private static CompiledScript script(HttpExchangeImpl x) {
		String scriptName = x.isGetReq() ? x.verbAndResourceName() : x.verb().toUpperCase() + "_" + x.name();
		String filename = scriptName + ".js";
		String firstFile = Conf.rootPath() + "/" + filename;
		String defaultFile = Conf.rootPathDefault() + "/" + filename;

		Res res = Res.from(filename, true, firstFile, defaultFile);
		if (!res.exists()) {
			return null;
		}

		CompiledScript compiled = res.attachment();

		if (compiled == null) {
			String js = res.getContent();
			try {
				compiled = U.compileJS(js);
			} catch (ScriptException e) {
				throw U.rte("Script compilation error!", e);
			}

			res.attach(compiled);
		}

		return compiled;
	}

	protected static void runScript(HttpExchangeImpl x, CompiledScript script, boolean hasEvent,
			Map<String, Object> config) {
		Map<String, Object> bindings = U.map();
		Dollar dollar = new Dollar(x, bindings);

		for (Entry<String, Object> e : x.data().entrySet()) {
			bindings.put("$" + e.getKey(), e.getValue());
		}

		// BeanProperties props = Beany.propertiesOf(Dollar.class);
		// for (Prop prop : props) {
		// Object val = prop.get(dollar);
		// bindings.put(prop.getName(), val);
		// }

		bindings.put("$", dollar);

		Object result;
		try {
			result = script.eval(new SimpleBindings(bindings));
		} catch (Throwable e) {
			Log.error("Script error", e);
			HttpProtocol.handleError(x, e);
			return;
		}

		if (result != null && !dollar.hasResult() && Cls.isSimple(result)) {
			dollar.result(result);
		}
	}

	public static void onScriptResult(HttpExchange x, Object result) {
		boolean rendered = calcFinalResult(x, result);

		if (!rendered) {
			x.result(result);
		}

		x.done();
	}

	private static boolean calcFinalResult(HttpExchange x, Object result) {
		Map<String, Object> config = U.map();

		if (result == x) {
			result = desc(x);

		} else if (result instanceof Dollar) {
			result = desc((Dollar) result);

		} else if (result instanceof DollarPage) {
			DollarPage page = (DollarPage) result;
			config = page.getConfig();
			result = page.getValue();

		} else if (result != null) {
			if (canDescribe(result)) {
				result = descObj(result);
			} else {
				return false;
			}
		}

		AppHandler.view(x, result, false, config);

		return true;
	}

	private static boolean canDescribe(Object obj) {
		if (!Cls.isBean(obj)) {
			return false;
		}

		// TODO maybe more checks here?

		return true;
	}

	public static Object desc(HttpExchange x) {
		Map<String, Object> desc = U.map();

		desc.put("verb", x.verb());
		desc.put("uri", x.uri());
		desc.put("path", x.path());
		desc.put("home", x.home());
		desc.put("dev", x.isDevMode());

		Ctx ctx = Ctxs.ctx();
		desc.put("loggedIn", ctx.isLoggedIn());
		desc.put("user", ctx.user());

		return GUI.multi(GUI.h2("Request details:"), GUI.grid(desc), GUI.h2("Request params:"), GUI.grid(x.data()),
				GUI.h2("Cookies:"), GUI.grid(x.cookies()));
	}

	public static Object desc(Dollar dollar) {
		Map<String, Object> desc = U.map();
		BeanProperties props = Beany.propertiesOf(Dollar.class);

		for (Prop prop : props) {
			Object val = prop.get(dollar);
			desc.put(prop.getName(), val != null ? val.getClass().getSimpleName() : "NULL");
		}

		return GUI.multi(GUI.h2("The $ properties:"), GUI.grid(desc), GUI.h2("Bindings:"), GUI.grid(dollar.bindings));
	}

	public static Object descObj(Object obj) {
		Map<String, Object> desc = U.map();
		BeanProperties props = Beany.propertiesOf(obj);

		for (Prop prop : props) {
			desc.put(prop.getName(), prop.getType());
		}

		String title = U.frmt("Properties of %s:", obj.getClass().getSimpleName());
		return GUI.multi(GUI.h2(title), GUI.grid(desc));
	}

}
