package org.rapidoid.html;

/*
 * #%L
 * rapidoid-html
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.AnyObj;
import org.rapidoid.html.impl.ConstantTag;
import org.rapidoid.html.tag.*;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HTML extends Tags {

	public static final Tag NBSP = constant("&nbsp;");

	public static final Tag LT = constant("&lt;");

	public static final Tag GT = constant("&gt;");

	public static final Tag LAQUO = constant("&laquo;");

	public static final Tag RAQUO = constant("&raquo;");

	public static boolean isTag(Object target) {
		return target instanceof Tag;
	}

	public static boolean isTag(Object target, String tagKind) {
		return isTag(target) && ((Tag) target).tagKind().equals(tagKind);
	}

	public static Tag hardcoded(String content) {
		return new ConstantTag(content);
	}

	public static ATag a_void(Object... contents) {
		return a(contents).href("javascript:void(0);");
	}

	public static Tag ul_li(Object... listItems) {
		listItems = AnyObj.flat(listItems);

		Tag list = ul();

		for (Object item : listItems) {
			if (!isTag(item, "li")) {
				item = li(item);
			}
			list = list.append(item);
		}

		return list;
	}

	public static ATag a(Object... contents) {
		return tag(ATag.class, "a", contents);
	}

	public static Tag abbr(Object... contents) {
		return tag(Tag.class, "abbr", contents);
	}

	public static Tag acronym(Object... contents) {
		return tag(Tag.class, "acronym", contents);
	}

	public static Tag address(Object... contents) {
		return tag(Tag.class, "address", contents);
	}

	public static Tag applet(Object... contents) {
		return tag(Tag.class, "applet", contents);
	}

	public static Tag area(Object... contents) {
		return tag(Tag.class, "area", contents);
	}

	public static Tag article(Object... contents) {
		return tag(Tag.class, "article", contents);
	}

	public static Tag aside(Object... contents) {
		return tag(Tag.class, "aside", contents);
	}

	public static Tag audio(Object... contents) {
		return tag(Tag.class, "audio", contents);
	}

	public static Tag b(Object... contents) {
		return tag(Tag.class, "b", contents);
	}

	public static Tag base(Object... contents) {
		return tag(Tag.class, "base", contents);
	}

	public static Tag basefont(Object... contents) {
		return tag(Tag.class, "basefont", contents);
	}

	public static Tag bdi(Object... contents) {
		return tag(Tag.class, "bdi", contents);
	}

	public static Tag bdo(Object... contents) {
		return tag(Tag.class, "bdo", contents);
	}

	public static Tag bgsound(Object... contents) {
		return tag(Tag.class, "bgsound", contents);
	}

	public static Tag big(Object... contents) {
		return tag(Tag.class, "big", contents);
	}

	public static Tag blink(Object... contents) {
		return tag(Tag.class, "blink", contents);
	}

	public static Tag blockquote(Object... contents) {
		return tag(Tag.class, "blockquote", contents);
	}

	public static Tag body(Object... contents) {
		return tag(Tag.class, "body", contents);
	}

	public static Tag br(Object... contents) {
		return tag(Tag.class, "br", contents);
	}

	public static ButtonTag button(Object... contents) {
		return tag(ButtonTag.class, "button", contents);
	}

	public static CanvasTag canvas(Object... contents) {
		return tag(CanvasTag.class, "canvas", contents);
	}

	public static Tag caption(Object... contents) {
		return tag(Tag.class, "caption", contents);
	}

	public static Tag center(Object... contents) {
		return tag(Tag.class, "center", contents);
	}

	public static Tag cite(Object... contents) {
		return tag(Tag.class, "cite", contents);
	}

	public static Tag code(Object... contents) {
		return tag(Tag.class, "code", contents);
	}

	public static Tag col(Object... contents) {
		return tag(Tag.class, "col", contents);
	}

	public static Tag colgroup(Object... contents) {
		return tag(Tag.class, "colgroup", contents);
	}

	public static Tag content(Object... contents) {
		return tag(Tag.class, "content", contents);
	}

	public static Tag data(Object... contents) {
		return tag(Tag.class, "data", contents);
	}

	public static Tag datalist(Object... contents) {
		return tag(Tag.class, "datalist", contents);
	}

	public static Tag dd(Object... contents) {
		return tag(Tag.class, "dd", contents);
	}

	public static Tag decorator(Object... contents) {
		return tag(Tag.class, "decorator", contents);
	}

	public static Tag del(Object... contents) {
		return tag(Tag.class, "del", contents);
	}

	public static Tag details(Object... contents) {
		return tag(Tag.class, "details", contents);
	}

	public static Tag dfn(Object... contents) {
		return tag(Tag.class, "dfn", contents);
	}

	public static Tag dialog(Object... contents) {
		return tag(Tag.class, "dialog", contents);
	}

	public static Tag dir(Object... contents) {
		return tag(Tag.class, "dir", contents);
	}

	public static Tag div(Object... contents) {
		return tag(Tag.class, "div", contents);
	}

	public static Tag dl(Object... contents) {
		return tag(Tag.class, "dl", contents);
	}

	public static Tag dt(Object... contents) {
		return tag(Tag.class, "dt", contents);
	}

	public static Tag element(Object... contents) {
		return tag(Tag.class, "element", contents);
	}

	public static Tag em(Object... contents) {
		return tag(Tag.class, "em", contents);
	}

	public static EmbedTag embed(Object... contents) {
		return tag(EmbedTag.class, "embed", contents);
	}

	public static Tag fieldset(Object... contents) {
		return tag(Tag.class, "fieldset", contents);
	}

	public static Tag figcaption(Object... contents) {
		return tag(Tag.class, "figcaption", contents);
	}

	public static Tag figure(Object... contents) {
		return tag(Tag.class, "figure", contents);
	}

	public static Tag font(Object... contents) {
		return tag(Tag.class, "font", contents);
	}

	public static Tag footer(Object... contents) {
		return tag(Tag.class, "footer", contents);
	}

	public static FormTag form(Object... contents) {
		return tag(FormTag.class, "form", contents);
	}

	public static Tag frame(Object... contents) {
		return tag(Tag.class, "frame", contents);
	}

	public static Tag frameset(Object... contents) {
		return tag(Tag.class, "frameset", contents);
	}

	public static Tag h1(Object... contents) {
		return tag(Tag.class, "h1", contents);
	}

	public static Tag h2(Object... contents) {
		return tag(Tag.class, "h2", contents);
	}

	public static Tag h3(Object... contents) {
		return tag(Tag.class, "h3", contents);
	}

	public static Tag h4(Object... contents) {
		return tag(Tag.class, "h4", contents);
	}

	public static Tag h5(Object... contents) {
		return tag(Tag.class, "h5", contents);
	}

	public static Tag h6(Object... contents) {
		return tag(Tag.class, "h6", contents);
	}

	public static Tag head(Object... contents) {
		return tag(Tag.class, "head", contents);
	}

	public static Tag header(Object... contents) {
		return tag(Tag.class, "header", contents);
	}

	public static Tag hgroup(Object... contents) {
		return tag(Tag.class, "hgroup", contents);
	}

	public static Tag hr(Object... contents) {
		return tag(Tag.class, "hr", contents);
	}

	public static Tag html(Object... contents) {
		return tag(Tag.class, "html", contents);
	}

	public static Tag i(Object... contents) {
		return tag(Tag.class, "i", contents);
	}

	public static IframeTag iframe(Object... contents) {
		return tag(IframeTag.class, "iframe", contents);
	}

	public static ImgTag img(Object... contents) {
		return tag(ImgTag.class, "img", contents);
	}

	public static InputTag input(Object... contents) {
		return tag(InputTag.class, "input", contents);
	}

	public static Tag ins(Object... contents) {
		return tag(Tag.class, "ins", contents);
	}

	public static Tag isindex(Object... contents) {
		return tag(Tag.class, "isindex", contents);
	}

	public static Tag kbd(Object... contents) {
		return tag(Tag.class, "kbd", contents);
	}

	public static Tag keygen(Object... contents) {
		return tag(Tag.class, "keygen", contents);
	}

	public static Tag label(Object... contents) {
		return tag(Tag.class, "label", contents);
	}

	public static Tag legend(Object... contents) {
		return tag(Tag.class, "legend", contents);
	}

	public static Tag li(Object... contents) {
		return tag(Tag.class, "li", contents);
	}

	public static LinkTag link(Object... contents) {
		return tag(LinkTag.class, "link", contents);
	}

	public static Tag listing(Object... contents) {
		return tag(Tag.class, "listing", contents);
	}

	public static Tag main(Object... contents) {
		return tag(Tag.class, "main", contents);
	}

	public static Tag map(Object... contents) {
		return tag(Tag.class, "map", contents);
	}

	public static Tag mark(Object... contents) {
		return tag(Tag.class, "mark", contents);
	}

	public static Tag marquee(Object... contents) {
		return tag(Tag.class, "marquee", contents);
	}

	public static Tag menu(Object... contents) {
		return tag(Tag.class, "menu", contents);
	}

	public static Tag menuitem(Object... contents) {
		return tag(Tag.class, "menuitem", contents);
	}

	public static Tag meta(Object... contents) {
		return tag(Tag.class, "meta", contents);
	}

	public static Tag meter(Object... contents) {
		return tag(Tag.class, "meter", contents);
	}

	public static Tag nav(Object... contents) {
		return tag(Tag.class, "nav", contents);
	}

	public static Tag nobr(Object... contents) {
		return tag(Tag.class, "nobr", contents);
	}

	public static Tag noframes(Object... contents) {
		return tag(Tag.class, "noframes", contents);
	}

	public static Tag noscript(Object... contents) {
		return tag(Tag.class, "noscript", contents);
	}

	public static ObjectTag object(Object... contents) {
		return tag(ObjectTag.class, "object", contents);
	}

	public static Tag ol(Object... contents) {
		return tag(Tag.class, "ol", contents);
	}

	public static Tag optgroup(Object... contents) {
		return tag(Tag.class, "optgroup", contents);
	}

	public static OptionTag option(Object... contents) {
		return tag(OptionTag.class, "option", contents);
	}

	public static Tag output(Object... contents) {
		return tag(Tag.class, "output", contents);
	}

	public static Tag p(Object... contents) {
		return tag(Tag.class, "p", contents);
	}

	public static Tag param(Object... contents) {
		return tag(Tag.class, "param", contents);
	}

	public static Tag picture(Object... contents) {
		return tag(Tag.class, "picture", contents);
	}

	public static Tag plaintext(Object... contents) {
		return tag(Tag.class, "plaintext", contents);
	}

	public static Tag pre(Object... contents) {
		return tag(Tag.class, "pre", contents);
	}

	public static Tag progress(Object... contents) {
		return tag(Tag.class, "progress", contents);
	}

	public static Tag q(Object... contents) {
		return tag(Tag.class, "q", contents);
	}

	public static Tag rp(Object... contents) {
		return tag(Tag.class, "rp", contents);
	}

	public static Tag rt(Object... contents) {
		return tag(Tag.class, "rt", contents);
	}

	public static Tag ruby(Object... contents) {
		return tag(Tag.class, "ruby", contents);
	}

	public static Tag s(Object... contents) {
		return tag(Tag.class, "s", contents);
	}

	public static Tag samp(Object... contents) {
		return tag(Tag.class, "samp", contents);
	}

	public static ScriptTag script(Object... contents) {
		return tag(ScriptTag.class, "script", contents);
	}

	public static Tag section(Object... contents) {
		return tag(Tag.class, "section", contents);
	}

	public static SelectTag select(Object... contents) {
		return tag(SelectTag.class, "select", contents);
	}

	public static Tag shadow(Object... contents) {
		return tag(Tag.class, "shadow", contents);
	}

	public static Tag small(Object... contents) {
		return tag(Tag.class, "small", contents);
	}

	public static Tag source(Object... contents) {
		return tag(Tag.class, "source", contents);
	}

	public static Tag spacer(Object... contents) {
		return tag(Tag.class, "spacer", contents);
	}

	public static Tag span(Object... contents) {
		return tag(Tag.class, "span", contents);
	}

	public static Tag strike(Object... contents) {
		return tag(Tag.class, "strike", contents);
	}

	public static Tag strong(Object... contents) {
		return tag(Tag.class, "strong", contents);
	}

	public static Tag style(Object... contents) {
		return tag(Tag.class, "style", contents);
	}

	public static Tag sub(Object... contents) {
		return tag(Tag.class, "sub", contents);
	}

	public static Tag summary(Object... contents) {
		return tag(Tag.class, "summary", contents);
	}

	public static Tag sup(Object... contents) {
		return tag(Tag.class, "sup", contents);
	}

	public static TableTag table(Object... contents) {
		return tag(TableTag.class, "table", contents);
	}

	public static Tag tbody(Object... contents) {
		return tag(Tag.class, "tbody", contents);
	}

	public static TdTag td(Object... contents) {
		return tag(TdTag.class, "td", contents);
	}

	public static Tag template(Object... contents) {
		return tag(Tag.class, "template", contents);
	}

	public static TextareaTag textarea(Object... contents) {
		return tag(TextareaTag.class, "textarea", contents);
	}

	public static Tag tfoot(Object... contents) {
		return tag(Tag.class, "tfoot", contents);
	}

	public static ThTag th(Object... contents) {
		return tag(ThTag.class, "th", contents);
	}

	public static Tag thead(Object... contents) {
		return tag(Tag.class, "thead", contents);
	}

	public static Tag time(Object... contents) {
		return tag(Tag.class, "time", contents);
	}

	public static Tag title(Object... contents) {
		return tag(Tag.class, "title", contents);
	}

	public static Tag tr(Object... contents) {
		return tag(Tag.class, "tr", contents);
	}

	public static Tag track(Object... contents) {
		return tag(Tag.class, "track", contents);
	}

	public static Tag tt(Object... contents) {
		return tag(Tag.class, "tt", contents);
	}

	public static Tag u(Object... contents) {
		return tag(Tag.class, "u", contents);
	}

	public static Tag ul(Object... contents) {
		return tag(Tag.class, "ul", contents);
	}

	public static Tag video(Object... contents) {
		return tag(Tag.class, "video", contents);
	}

	public static Tag wbr(Object... contents) {
		return tag(Tag.class, "wbr", contents);
	}

	public static Tag xmp(Object... contents) {
		return tag(Tag.class, "xmp", contents);
	}

}
