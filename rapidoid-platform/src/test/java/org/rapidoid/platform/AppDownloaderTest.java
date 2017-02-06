package org.rapidoid.platform;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.TestCommons;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class AppDownloaderTest extends TestCommons {

	@Test
	public void testAppUrlConstruction() {
		eq(AppDownloader.getAppUrl("abc"), "https://github.com/rapidoid/abc/archive/master.zip");
		eq(AppDownloader.getAppUrl("foo/bar"), "https://github.com/foo/bar/archive/master.zip");
		eq(AppDownloader.getAppUrl("https://a.b.c.d.e"), "https://a.b.c.d.e");
		eq(AppDownloader.getAppUrl("a/"), "a/");
	}

}
