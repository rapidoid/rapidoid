package org.rapidoid.web.config.bean;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.4.2")
public interface GuiConfig {

	PageGuiType type();

	String caption();

	String header();

	String footer();

	String sql();

	String uri();

	boolean single();

	int pageSize();
}
