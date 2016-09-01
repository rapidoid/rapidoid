package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.activity.RapidoidThreadLocals;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

/*
 * #%L
 * rapidoid-render
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RapidoidTemplate extends RapidoidThing implements Template {

	private final String filename;

	private final TemplateRenderer template;

	private final TemplateFactory factory;

	public RapidoidTemplate(String filename, TemplateRenderer template, TemplateFactory factory) {
		this.filename = filename;
		this.template = template;
		this.factory = factory;
	}

	void doRenderMulti(RapidoidThreadLocals locals, OutputStream output, List<Object> model) {
		RenderCtxImpl renderCtx = initRenderCtx(locals);

		renderCtx.out(output).factory(factory).filename(filename).multiModel(model);

		template.render(renderCtx);

		renderCtx.reset();
	}

	void doRender(RapidoidThreadLocals locals, OutputStream output, Object model) {
		RenderCtxImpl renderCtx = initRenderCtx(locals);

		renderCtx.out(output).factory(factory).filename(filename).model(model);

		template.render(renderCtx);

		renderCtx.reset();
	}

	private RenderCtxImpl initRenderCtx(RapidoidThreadLocals locals) {
		RenderCtxImpl renderCtx = (RenderCtxImpl) locals.renderContext;

		if (renderCtx == null) {
			renderCtx = new RenderCtxImpl();
			locals.renderContext = renderCtx;
		}

		return renderCtx;
	}

	public void renderMultiModel(OutputStream output, Object... model) {
		doRenderMulti(Msc.locals(), output, U.list(model));
	}

	@Override
	public void renderTo(OutputStream output, Object model) {
		doRender(Msc.locals(), output, model);
	}

	@Override
	public byte[] renderToBytes(Object model) {
		RapidoidThreadLocals locals = Msc.locals();

		ByteArrayOutputStream out = locals.templateRenderingStream();

		doRender(locals, out, model);

		return out.toByteArray();
	}

	@Override
	public String render(Object model) {
		return new String(renderToBytes(model));
	}

	public void renderInContext(RenderCtxImpl renderCtx) {
		template.render(renderCtx);
	}

}
