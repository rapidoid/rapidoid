package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.activity.RapidoidThreadLocals;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.writable.ReusableWritable;
import org.rapidoid.writable.Writable;
import org.rapidoid.writable.WritableOutputStream;

import java.io.OutputStream;
import java.util.List;

/*
 * #%L
 * rapidoid-render
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
public class RapidoidTemplate extends RapidoidThing implements Template {

	private final String filename;

	private final TemplateRenderer template;

	private final TemplateFactory factory;

	public RapidoidTemplate(String filename, TemplateRenderer template, TemplateFactory factory) {
		this.filename = filename;
		this.template = template;
		this.factory = factory;
	}

	void doRenderMulti(RapidoidThreadLocals locals, Writable output, List<Object> model) {
		// start using the render context
		RenderCtxImpl renderCtx = getRenderCtx(locals);

		renderCtx.out(output).factory(factory).filename(filename).multiModel(model);
		template.render(renderCtx);

		// stop using the render context
		renderCtx.reset();
	}

	void doRender(RapidoidThreadLocals locals, Writable output, Object model) {
		// start using the render context
		RenderCtxImpl renderCtx = getRenderCtx(locals);

		renderCtx.out(output).factory(factory).filename(filename).model(model);
		template.render(renderCtx);

		// stop using the render context
		renderCtx.reset();
	}

	private RenderCtxImpl getRenderCtx(RapidoidThreadLocals locals) {
		RenderCtxImpl renderCtx = (RenderCtxImpl) locals.renderContext;

		if (renderCtx == null) {
			renderCtx = new RenderCtxImpl();
			locals.renderContext = renderCtx;
		}

		if (!renderCtx.busy()) {
			renderCtx.claim();
			return renderCtx;
		} else {
			return new RenderCtxImpl();
		}
	}

	public void renderMultiModel(OutputStream output, Object... model) {
		doRenderMulti(Msc.locals(), new WritableOutputStream(output), U.list(model));
	}

	@Override
	public void renderTo(OutputStream output, Object model) {
		renderTo(new WritableOutputStream(output), model);
	}

	@Override
	public void renderTo(Writable output, Object model) {
		doRender(Msc.locals(), output, model);
	}

	@Override
	public byte[] renderToBytes(Object model) {
		RapidoidThreadLocals locals = Msc.locals();

		ReusableWritable out = locals.templateRenderingOutput();

		doRender(locals, out, model);

		return out.copy();
	}

	@Override
	public String render(Object model) {
		return new String(renderToBytes(model));
	}

	public void renderInContext(RenderCtxImpl renderCtx) {
		template.render(renderCtx);
	}

}
