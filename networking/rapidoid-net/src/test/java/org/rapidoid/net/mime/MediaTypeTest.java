package org.rapidoid.net.mime;

/*
 * #%L
 * rapidoid-net
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

import org.junit.Test;
import org.rapidoid.NetTestCommons;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.MediaType;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class MediaTypeTest extends NetTestCommons {

	@Test
	public void testCommonMediaTypes() {
		eq(new String(MediaType.PLAIN_TEXT_UTF_8.getBytes()), "text/plain; charset=utf-8");
		eq(MediaType.getByFileExtension("txt"), MediaType.PLAIN_TEXT_UTF_8);

		eq(new String(MediaType.HTML_UTF_8.getBytes()), "text/html; charset=utf-8");
		eq(MediaType.getByFileExtension("html"), MediaType.HTML_UTF_8);

		eq(new String(MediaType.XHTML_XML_UTF8.getBytes()), "application/xhtml+xml; charset=utf-8");
		eq(MediaType.getByFileExtension("xhtml"), MediaType.XHTML_XML_UTF8);

		eq(new String(MediaType.CSS_UTF_8.getBytes()), "text/css; charset=utf-8");
		eq(MediaType.getByFileExtension("css"), MediaType.CSS_UTF_8);

		eq(new String(MediaType.JSON.getBytes()), "application/json");
		eq(MediaType.getByFileExtension("json"), MediaType.JSON);

		eq(new String(MediaType.JAVASCRIPT_UTF8.getBytes()), "application/javascript; charset=utf-8");
		eq(MediaType.getByFileExtension("js"), MediaType.JAVASCRIPT_UTF8);

		eq(new String(MediaType.PDF.getBytes()), "application/pdf");
		eq(MediaType.getByFileExtension("pdf"), MediaType.PDF);

		eq(new String(MediaType.ZIP.getBytes()), "application/zip");
		eq(MediaType.getByFileExtension("zip"), MediaType.ZIP);

		eq(new String(MediaType.SWF.getBytes()), "application/x-shockwave-flash");
		eq(MediaType.getByFileExtension("swf"), MediaType.SWF);

		eq(new String(MediaType.JPEG.getBytes()), "image/jpeg");
		eq(MediaType.getByFileExtension("jpg"), MediaType.JPEG);
		eq(MediaType.getByFileExtension("jpeg"), MediaType.JPEG);

		eq(new String(MediaType.PNG.getBytes()), "image/png");
		eq(MediaType.getByFileExtension("png"), MediaType.PNG);

		eq(new String(MediaType.GIF.getBytes()), "image/gif");
		eq(MediaType.getByFileExtension("gif"), MediaType.GIF);

		eq(new String(MediaType.BMP.getBytes()), "image/bmp");
		eq(MediaType.getByFileExtension("bmp"), MediaType.BMP);

		eq(new String(MediaType.SVG.getBytes()), "image/svg+xml; charset=utf-8");
		eq(MediaType.getByFileExtension("svg"), MediaType.SVG);
	}

	@Test
	public void testCustomMediaType() {
		String[] attrss = {"abc=xy"};
		MediaType myType = MediaType.create("text/some", attrss, "some");

		eq(new String(myType.getBytes()), "text/some; abc=xy");

		eq(MediaType.getByFileName("abc.some"), myType);
		eq(MediaType.getByFileName(".some"), myType);
		eq(MediaType.getByFileName("some"), MediaType.DEFAULT);
		eq(MediaType.getByFileName("someX"), MediaType.DEFAULT);
	}

	@Test
	public void testCustomMediaType2() {
		String[] attrss = {"a=1", "b=abc"};
		MediaType myType = MediaType.create("app/my", attrss, "my1", "my2");

		eq(new String(myType.getBytes()), "app/my; a=1; b=abc");

		eq(MediaType.getByFileExtension("my1"), myType);
		eq(MediaType.getByFileExtension("my2"), myType);
		eq(MediaType.getByFileExtension("myX"), null);
	}
}
