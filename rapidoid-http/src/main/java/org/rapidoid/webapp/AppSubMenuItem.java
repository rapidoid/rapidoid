package org.rapidoid.webapp;

/*
 * #%L
 * rapidoid-http
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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class AppSubMenuItem {

	private String caption;

	private String target;

	private final Map<String, Object> extra;

	public AppSubMenuItem(String caption, Object target, Map<String, Object> extra) {
		this.caption = caption;
		this.extra = extra;
		this.target = target != null ? Cls.str(target) : "";
	}

	@Override
	public String toString() {
		return "AppSubMenuItem [" + caption + " => " + target + ", extra=" + extra + "]";
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Map<String, Object> getExtra() {
		return extra;
	}

}
