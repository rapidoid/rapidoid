package org.rapidoid.gui.menu;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.u.U;

import java.util.Map;

/*
 * #%L
 * rapidoid-gui
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
@Since("4.1.0")
public abstract class AbstractPageMenuItem extends RapidoidThing {

	protected String caption;

	protected String target;

	protected String javascript;

	protected String icon;

	protected boolean divider = false;

	protected boolean active = false;

	protected final Map<String, Object> extra;

	public AbstractPageMenuItem(String caption, Object target, Map<String, Object> extra) {
		this.caption = caption.trim();
		this.extra = extra;

		Object iconExtra = extra != null ? extra.get("icon") : null;
		if (iconExtra != null) {
			this.icon = Cls.str(iconExtra);
		}

		if (!caption.isEmpty() && caption.replace("-", "").isEmpty()) {
			// e.g. ---
			this.divider = true;
		}

		if (Cls.isSimple(target)) {
			String targ = U.safe(Cls.str(target)).trim();

			if (targ.startsWith("javascript:")) {
				this.javascript = targ.substring(11).trim();
			} else {
				this.target = targ.trim();
			}
		}
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

	public String getJavascript() {
		return javascript;
	}

	public void setJavascript(String javascript) {
		this.javascript = javascript;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Map<String, Object> getExtra() {
		return extra;
	}

	public boolean isDivider() {
		return divider;
	}

	public boolean isActive() {
		return active;
	}

	public void setDivider(boolean divider) {
		this.divider = divider;
	}

	public boolean isVisible() {
		return U.notEmpty(caption);
	}

	void setActiveUri(String uri) {
		active = U.eq(uri, target);
	}
}
