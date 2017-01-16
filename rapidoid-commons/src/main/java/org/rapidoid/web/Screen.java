package org.rapidoid.web;

/*
 * #%L
 * rapidoid-commons
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

import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

/**
 * GUI Screen (HTML Page) API.<br>
 * Provides a convenient access to some common GUI screen attributes of the underlying MVC model: <code>Resp#model()</code>.
 */
@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface Screen {

	/**
	 * Renders the screen.
	 */
	String render();

	/**
	 * Renders the screen to the specified OutputStream.
	 */
	void render(OutputStream out);

	/**
	 * Sets the "<b>title</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	Screen title(String title);

	/**
	 * Gets the "<b>title</b>" attribute from the MVC model of the response, used for GUI page rendering.
	 */
	String title();

	/**
	 * Sets the "<b>brand</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	Screen brand(Object brand);

	/**
	 * Gets the "<b>brand</b>" attribute from the MVC model of the response, used for GUI page rendering.
	 */
	Object brand();

	/**
	 * Sets the "<b>content</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	Object[] content();

	/**
	 * Sets the "<b>content</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	Screen content(Object... content);

	/**
	 * Sets the "<b>menu</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	Screen menu(Map<String, ?> menu);

	/**
	 * Gets the "<b>menu</b>" attribute from the MVC model of the response, used for GUI page rendering.
	 */
	Map<String, Object> menu();

	/**
	 * Sets the "<b>search</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	Screen search(boolean search);

	/**
	 * Gets the "<b>search</b>" attribute from the MVC model of the response, used for GUI page rendering.
	 */
	boolean search();

	/**
	 * Sets the "<b>embedded</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	boolean embedded();

	/**
	 * Sets the "<b>embedded</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	Screen embedded(boolean embedded);

	/**
	 * Sets the "<b>cdn</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	Screen cdn(boolean cdn);

	/**
	 * Gets the "<b>cdn</b>" attribute from the MVC model of the response, used for GUI page rendering.
	 */
	boolean cdn();

	/**
	 * Sets the "<b>navbar</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	Screen navbar(boolean navbar);

	/**
	 * Gets the "<b>navbar</b>" attribute from the MVC model of the response, used for GUI page rendering.
	 */
	boolean navbar();

	/**
	 * Sets the "<b>fluid</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	Screen fluid(boolean fluid);

	/**
	 * Gets the "<b>fluid</b>" attribute from the MVC model of the response, used for GUI page rendering.
	 */
	boolean fluid();

	/**
	 * Sets the "<b>home</b>" attribute in the MVC model of the response, used for GUI page rendering.
	 */
	Screen home(String home);

	/**
	 * Gets the "<b>home</b>" attribute from the MVC model of the response, used for GUI page rendering.
	 */
	String home();

	/**
	 * Retrieves a modifiable set of JavaScript asset URIs, used for GUI page rendering.
	 */
	Set<String> js();

	/**
	 * Retrieves a modifiable set of CSS asset URIs, used for GUI page rendering.
	 */
	Set<String> css();

}
