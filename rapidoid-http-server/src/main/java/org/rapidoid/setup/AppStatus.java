package org.rapidoid.setup;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.4.0")
public enum AppStatus {
	NOT_STARTED, INITIALIZING, RUNNING, STOPPING, STOPPED
}
