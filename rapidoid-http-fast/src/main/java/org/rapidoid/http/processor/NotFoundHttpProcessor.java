package org.rapidoid.http.processor;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.impl.lowlevel.HttpIO;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.render.Templates;
import org.rapidoid.u.U;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class NotFoundHttpProcessor extends AbstractHttpProcessor {

	private static final Map<String, Map<String, String>> MODEL = U.map("req", U.map("contextPath", ""));

	public NotFoundHttpProcessor() {
		super(null);
	}

	@Override
	public void onRequest(Channel channel, RapidoidHelper data) {
		boolean isKeepAlive = data.isKeepAlive.value;

		String content = Templates.load("404.html").render(MODEL);

		HttpIO.INSTANCE.respond(HttpUtils.noReq(), channel, -1, 404, isKeepAlive, MediaType.HTML_UTF_8, content.getBytes(), null, null);

		channel.send().closeIf(!isKeepAlive);
	}

}
