package org.rapidoid.http.impl;

/*
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Resp;
import org.rapidoid.http.View;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.PageDecorator;
import org.rapidoid.http.customize.ViewResolver;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ResponseRenderer extends RapidoidThing {

	public static byte[] render(ReqImpl req, Resp resp) {

		Object result = resp.result();

		String content;

		if (shouldRenderView(resp)) {
			content = renderView(req, resp, result);
		} else {
			Object cnt = U.or(result, "");
			content = new String(HttpUtils.responseToBytes(req, cnt, MediaType.HTML_UTF_8, null));
		}

		return renderPage(req, content);
	}

	private static boolean shouldRenderView(Resp resp) {
		if (!resp.mvc()) return false;

		Object result = resp.result();
		if (result == null) return true;

		if (((RespImpl) resp).hasCustomView()) return U.notEmpty(resp.view());

		return shouldRenderViewForResult(result);
	}

	private static boolean shouldRenderViewForResult(Object result) {

		if ((result instanceof String)
			|| (result instanceof byte[])
			|| (result instanceof ByteBuffer)) return false;

		if (GUI.isGUI(result)) return false;

		TypeKind kind = Cls.kindOf(result);
		return !(kind.isPrimitive() || kind.isNumber());
	}

	public static String renderView(ReqImpl req, Resp resp, Object result) {

		String viewName = resp.view();
		Customization custom = Customization.of(req);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		ViewResolver viewResolver = custom.viewResolver();
		U.must(viewResolver != null, "A view renderer wasn't configured!");

		Object mvcModel;

		if (result != null) {
			mvcModel = result;

			if (result instanceof Map<?, ?>) {
				Map<String, Object> map = U.cast(result);
				map.putAll(resp.model());

			} else {
				U.must(resp.model().isEmpty(), "The result must be a Map when custom model properties are assigned!");
			}

		} else {
			mvcModel = resp.model();
		}

		try {
			View view = viewResolver.getView(viewName, custom.templateLoader());
			view.render(mvcModel, out);

		} catch (Throwable e) {
			throw U.rte("Error while rendering view: " + viewName, e);
		}

		return new String(out.toByteArray());
	}

	public static byte[] renderPage(ReqImpl req, String content) {

		PageDecorator pageDecorator = Customization.of(req).pageDecorator();
		U.must(pageDecorator != null, "A page decorator wasn't configured!");

		ByteArrayOutputStream out = Msc.locals().pageRenderingStream();

		try {
			pageDecorator.renderPage(req, content, out);

		} catch (Exception e) {
			throw U.rte("Error while rendering page!", e);
		}

		return out.toByteArray();
	}

	private static Object wrapGuiContent(Object content) {
		if (Msc.hasRapidoidGUI()) {
			Object[] items = null;

			if (content instanceof Collection<?>) {
				items = U.array((Collection<?>) content);

			} else if (content instanceof Object[]) {
				items = (Object[]) content;
			}

			if (items != null) {
				return Cls.newInstance(Cls.get("org.rapidoid.html.ElementGroup"), new Object[]{items});
			}
		}

		return content;
	}

}
