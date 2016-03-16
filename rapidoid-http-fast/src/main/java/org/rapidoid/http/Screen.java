package org.rapidoid.http;

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

import org.rapidoid.annotation.Since;

/**
 * Response GUI Screen API.<br>
 * Provides a convenient access to some common GUI screen attributes of the underlying MVC model: <code>Resp#model()</code>.
 */
@Since("5.1.0")
public interface Screen {

	/**
	 * Sets the "<b>title</b>" attribute in the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().put("title", title)</code>.
	 */
	Screen title(String title);

	/**
	 * Gets the "<b>title</b>" attribute from the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().get("title")</code>.
	 */
	String title();

	/**
	 * Sets the "<b>brand</b>" attribute in the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().put("brand", brand)</code>.
	 */
	Screen brand(Object brand);

	/**
	 * Gets the "<b>brand</b>" attribute from the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().get("brand")</code>.
	 */
	Object brand();

	/**
	 * Sets the "<b>menu</b>" attribute in the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().put("menu", menu)</code>.
	 */
	Screen menu(Object menu);

	/**
	 * Gets the "<b>menu</b>" attribute from the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().get("menu")</code>.
	 */
	Object menu();

	/**
	 * Sets the "<b>search</b>" attribute in the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().put("search", search)</code>.
	 */
	Screen search(boolean search);

	/**
	 * Gets the "<b>search</b>" attribute from the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().get("search")</code>.
	 */
	Boolean search();

	/**
	 * Sets the "<b>cdn</b>" attribute in the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().put("cdn", cdn)</code>.
	 */
	Screen cdn(boolean cdn);

	/**
	 * Gets the "<b>cdn</b>" attribute from the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().get("cdn")</code>.
	 */
	Boolean cdn();

	/**
	 * Sets the "<b>navbar</b>" attribute in the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().put("navbar", navbar)</code>.
	 */
	Screen navbar(boolean navbar);

	/**
	 * Gets the "<b>navbar</b>" attribute from the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().get("navbar")</code>.
	 */
	Boolean navbar();

	/**
	 * Sets the "<b>fluid</b>" attribute in the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().put("fluid", fluid)</code>.
	 */
	Screen fluid(boolean fluid);

	/**
	 * Gets the "<b>fluid</b>" attribute from the MVC model of the response, used for GUI screen rendering.<br>
	 * Equivalent to <code>model().get("fluid")</code>.
	 */
	Boolean fluid();

}
