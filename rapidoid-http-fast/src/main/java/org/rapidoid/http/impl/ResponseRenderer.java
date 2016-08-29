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
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Resp;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.MasterPage;
import org.rapidoid.http.customize.ViewRenderer;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.ByteArrayOutputStream;
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

		return renderPage(req, resp, content);
	}

	private static boolean shouldRenderView(Resp resp) {
		if (!resp.mvc()) return false;

		if (((RespImpl) resp).hasCustomView() || resp.result() == null) return true;

		TypeKind kind = Cls.kindOf(resp.result());

		return !(resp.result() instanceof String)
			&& !(resp.result() instanceof byte[])
			&& !kind.isPrimitive()
			&& !kind.isArray()
			&& !kind.isNumber();
	}

	public static String renderView(ReqImpl req, Resp resp, Object result) {

		String viewName = resp.view();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		ViewRenderer viewRenderer = Customization.of(req).viewRenderer();
		U.must(viewRenderer != null, "A view renderer wasn't configured!");

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
			viewRenderer.render(req, viewName, mvcModel, out);
		} catch (Throwable e) {
			throw U.rte("Error while rendering view: " + viewName, e);
		}

		return new String(out.toByteArray());
	}

	public static byte[] renderPage(ReqImpl req, Resp resp, String content) {

		MasterPage masterPage = Customization.of(req).masterPage();
		U.must(masterPage != null, "A page renderer wasn't configured!");

		try {
			Object response = U.or(masterPage.renderPage(req, resp, content), "");
			return HttpUtils.responseToBytes(req, response, MediaType.HTML_UTF_8, null);

		} catch (Exception e) {
			throw U.rte("Error while rendering page!", e);
		}
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
