package org.rapidoid.http;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;

import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class OnChanges {

	private final ServerSetup serverSetup;
	private final FastHttp[] fastHttps;

	public OnChanges(ServerSetup serverSetup, FastHttp[] fastHttps) {
		this.serverSetup = serverSetup;
		this.fastHttps = fastHttps;
	}

	public void reload() {
		if (Conf.dev()) {
			Set<String> classpathFolders = ClasspathUtil.getClasspathFolders();
			Log.info("Watching classpath for changes...", "classpath", classpathFolders);
			// FIXME complete this
		}
	}

}
