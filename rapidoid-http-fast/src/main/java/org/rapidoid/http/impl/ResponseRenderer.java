package org.rapidoid.http.impl;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Resp;
import org.rapidoid.http.View;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.PageDecorator;
import org.rapidoid.http.customize.ViewResolver;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;
import org.rapidoid.writable.ReusableWritable;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ResponseRenderer extends RapidoidThing {

	public static byte[] renderMvc(ReqImpl req, Resp resp) {

		Object result = resp.result();
		String content = null;

		if (shouldRenderView(resp)) {
			boolean mandatory = (((RespImpl) resp).hasCustomView());

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			boolean rendered = renderView(req, resp, result, mandatory, out);
			if (rendered) content = new String(out.toByteArray());
		}

		if (content == null) {
			Object respResult = U.or(result, "");
			content = new String(HttpUtils.responseToBytes(req, respResult, MediaType.HTML_UTF_8, null));
		}

		return renderPage(req, content);
	}

	private static boolean shouldRenderView(Resp resp) {
		if (!resp.mvc()) return false;

		Object result = resp.result();
		if (result == null) return true;

		if (((RespImpl) resp).hasCustomView()) return U.notEmpty(resp.view());

		return true;
	}

	public static boolean renderView(ReqImpl req, Resp resp, Object result, boolean mandatory, ByteArrayOutputStream out) {

		String viewName = resp.view();
		HttpUtils.validateViewName(viewName);

		Customization custom = Customization.of(req);

		ViewResolver viewResolver = custom.viewResolver();
		U.must(viewResolver != null, "A view resolver wasn't configured!");

		Object mvcModel;

		if (result != null) {
			mvcModel = result;

			if (result instanceof Map<?, ?>) {
				Map<String, Object> map = U.map();

				map.putAll(req.params());
				map.putAll((Map) result);
				map.putAll(resp.model());

				mvcModel = map;

			} else {
				U.must(resp.model().isEmpty(), "The result must be a Map when custom model properties are assigned!");
			}

		} else {
			mvcModel = resp.model();
		}

		View view;
		try {
			view = viewResolver.getView(viewName, custom.templateLoader());
		} catch (Throwable e) {
			throw U.rte("Error while retrieving view: " + viewName, e);
		}

		if (view != null) {
			try {
				view.render(mvcModel, out);
			} catch (Throwable e) {
				throw U.rte("Error while rendering view: " + viewName, e);
			}

		} else {
			U.must(!mandatory, "The view '%s' doesn't exist!", viewName);
		}

		return view != null;
	}

	public static byte[] renderPage(ReqImpl req, String content) {

		PageDecorator pageDecorator = Customization.of(req).pageDecorator();
		U.must(pageDecorator != null, "A page decorator wasn't configured!");

		ReusableWritable out = Msc.locals().pageRenderingStream();

		try {
			pageDecorator.renderPage(req, content, out);

		} catch (Exception e) {
			throw U.rte("Error while rendering page!", e);
		}

		return out.copy();
	}

	private static Object wrapGuiContent(Object content) {
		if (MscOpts.hasRapidoidGUI()) {
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
