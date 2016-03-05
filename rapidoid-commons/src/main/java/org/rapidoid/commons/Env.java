package org.rapidoid.commons;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.scan.ClasspathUtil;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Env {

	public static boolean production() {
		return Conf.ROOT.is("production");
	}

	public static boolean dev() {
		return !production() && !ClasspathUtil.getClasspathFolders().isEmpty();
	}

}
