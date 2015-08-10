package org.rapidoid.annotation;

/*
 * #%L
 * rapidoid-annotations
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ METHOD })
@Retention(RUNTIME)
@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public @interface Page {

	String value() default "";

	boolean raw() default false;

	boolean navbar() default true;

	boolean search() default false;

	boolean profile() default true;

	boolean login() default true;

	String title() default "";

}
