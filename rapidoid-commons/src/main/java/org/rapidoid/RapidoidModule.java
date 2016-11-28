package org.rapidoid;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public interface RapidoidModule {

	String name();

	void beforeTest(Object test);

	void afterTest(Object test);

}
