package org.rapidoid.http.handler.param;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.Req;
import org.rapidoid.io.Upload;
import org.rapidoid.u.U;

import java.util.List;

/*
 * #%L
 * rapidoid-http-server
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
public class UploadsParamRetriever extends RapidoidThing implements ParamRetriever {

	private final Class<?> type;
	private final String name;

	public UploadsParamRetriever(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	@Override
	public Upload[] getParamValue(Req req) {
		List<Upload> uploads = req.files(name);
		return U.arrayOf(Upload.class, uploads);
	}

}
