package org.rapidoid.pages.widget;

import org.rapidoid.pages.Var;
import org.rapidoid.test.TestCommons;

public class WidgetTestCommons extends TestCommons {

	protected void has(Widget widget, String... containingTexts) {
		String html = widget.toString();
		for (String text : containingTexts) {
			isTrue(html.contains(text));
		}
	}

	protected void eq(Var<?> var, Object value) {
		eq(var.get(), value);
	}

}
