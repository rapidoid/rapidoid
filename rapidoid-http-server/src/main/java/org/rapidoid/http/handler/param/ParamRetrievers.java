package org.rapidoid.http.handler.param;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.*;
import org.rapidoid.beany.Metadata;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Err;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.io.Upload;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.web.Screen;

import java.io.File;
import java.lang.annotation.Annotation;

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
public class ParamRetrievers extends RapidoidThing {

	public static ParamRetriever createParamRetriever(Customization customization, Class<?> type, String name, Annotation[] annotations) {

		Cookie cookie = Metadata.get(annotations, Cookie.class);
		Header header = Metadata.get(annotations, Header.class);
		Param param = Metadata.get(annotations, Param.class);
		Valid valid = Metadata.get(annotations, Valid.class);
		Required required = Metadata.get(annotations, Required.class);

		int annotationsCount = Msc.countNonNull(cookie, header, param);
		U.must(annotationsCount <= 1, "Expected maximum one of the @Cookie, @Header, @Param, @P annotations on the same parameter!");

		if (annotationsCount == 1) {
			if (cookie != null) {
				return new CookieParamRetriever(type, name(name, cookie.value()));

			} else if (header != null) {
				return new HeaderParamRetriever(type, name(name, header.value()));

			} else if (param != null) {
				name = name(name, param.value());

			} else {
				throw Err.notExpected();
			}
		}

		if (Req.class.isAssignableFrom(type)) {
			return new ReqParamRetriever();

		} else if (Resp.class.isAssignableFrom(type)) {
			return new RespParamRetriever();

		} else if (Screen.class.isAssignableFrom(type)) {
			return new ScreenParamRetriever();

		} else if (type.equals(byte[].class)) {
			return new ByteArrayParamRetriever(type, name);

		} else if (type.equals(byte[][].class)) {
			return new ByteArraysParamRetriever(type, name);

		} else if (type.equals(Upload.class)) {
			return new UploadParamRetriever(type, name);

		} else if (type.equals(Upload[].class)) {
			return new UploadsParamRetriever(type, name);

		} else if (type.equals(Object.class)) {
			throw U.rte("The 'Object' parameter type is too generic and not supported. Please use more specific parameter type!");

		} else if (type.equals(File.class)) {
			throw U.rte("The 'File' parameter type is not supported (yet). Try using the 'byte[]' parameter type!");

		} else {
			if (Cls.isAppBeanType(type)) {
				return new BeanParamRetriever(customization, type, name, valid != null);

			} else if (Cls.kindOf(type).isConcrete()) {
				return new DataParamRetriever(type, name, required != null);

			} else if (Cls.isDataStructure(type) || Cls.isJREClass(type.getCanonicalName())) {
				throw U.rte("Unsupported parameter type: %s", type);

			} else {
				return new DataParamRetriever(type, name, required != null);
			}
		}
	}

	private static String name(String methodParamName, String annotatedName) {
		return !U.isEmpty(annotatedName) ? annotatedName : methodParamName;
	}

}
