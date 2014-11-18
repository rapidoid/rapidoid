package org.rapidoid.html;

/*
 * #%L
 * rapidoid-html
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.html.tag.*;

public class HTML extends Tags {

	public static final Tag<?> NBSP = constant("&nbsp;");

	public static final Tag<?> LT = constant("&lt;");

	public static final Tag<?> GT = constant("&gt;");

	public static final Tag<?> LAQUO = constant("&laquo;");

	public static final Tag<?> RAQUO = constant("&raquo;");

	public static UlTag ul_li(Object... listItems) {
		UlTag list = ul();

		for (Object item : listItems) {
			list = list.append(li(item));
		}

		return list;
	}

	public static ATag a(Object... contents) {
		return tag(ATag.class, "a", contents).href("#");
	}

	public static AbbrTag abbr(Object... contents) {
		return tag(AbbrTag.class, "abbr", contents);
	}

	public static AcronymTag acronym(Object... contents) {
		return tag(AcronymTag.class, "acronym", contents);
	}

	public static AddressTag address(Object... contents) {
		return tag(AddressTag.class, "address", contents);
	}

	public static AppletTag applet(Object... contents) {
		return tag(AppletTag.class, "applet", contents);
	}

	public static AreaTag area(Object... contents) {
		return tag(AreaTag.class, "area", contents);
	}

	public static ArticleTag article(Object... contents) {
		return tag(ArticleTag.class, "article", contents);
	}

	public static AsideTag aside(Object... contents) {
		return tag(AsideTag.class, "aside", contents);
	}

	public static AudioTag audio(Object... contents) {
		return tag(AudioTag.class, "audio", contents);
	}

	public static BTag b(Object... contents) {
		return tag(BTag.class, "b", contents);
	}

	public static BaseTag base(Object... contents) {
		return tag(BaseTag.class, "base", contents);
	}

	public static BasefontTag basefont(Object... contents) {
		return tag(BasefontTag.class, "basefont", contents);
	}

	public static BdiTag bdi(Object... contents) {
		return tag(BdiTag.class, "bdi", contents);
	}

	public static BdoTag bdo(Object... contents) {
		return tag(BdoTag.class, "bdo", contents);
	}

	public static BgsoundTag bgsound(Object... contents) {
		return tag(BgsoundTag.class, "bgsound", contents);
	}

	public static BigTag big(Object... contents) {
		return tag(BigTag.class, "big", contents);
	}

	public static BlinkTag blink(Object... contents) {
		return tag(BlinkTag.class, "blink", contents);
	}

	public static BlockquoteTag blockquote(Object... contents) {
		return tag(BlockquoteTag.class, "blockquote", contents);
	}

	public static BodyTag body(Object... contents) {
		return tag(BodyTag.class, "body", contents);
	}

	public static BrTag br(Object... contents) {
		return tag(BrTag.class, "br", contents);
	}

	public static ButtonTag button(Object... contents) {
		return tag(ButtonTag.class, "button", contents);
	}

	public static CanvasTag canvas(Object... contents) {
		return tag(CanvasTag.class, "canvas", contents);
	}

	public static CaptionTag caption(Object... contents) {
		return tag(CaptionTag.class, "caption", contents);
	}

	public static CenterTag center(Object... contents) {
		return tag(CenterTag.class, "center", contents);
	}

	public static CiteTag cite(Object... contents) {
		return tag(CiteTag.class, "cite", contents);
	}

	public static CodeTag code(Object... contents) {
		return tag(CodeTag.class, "code", contents);
	}

	public static ColTag col(Object... contents) {
		return tag(ColTag.class, "col", contents);
	}

	public static ColgroupTag colgroup(Object... contents) {
		return tag(ColgroupTag.class, "colgroup", contents);
	}

	public static ContentTag content(Object... contents) {
		return tag(ContentTag.class, "content", contents);
	}

	public static DataTag data(Object... contents) {
		return tag(DataTag.class, "data", contents);
	}

	public static DatalistTag datalist(Object... contents) {
		return tag(DatalistTag.class, "datalist", contents);
	}

	public static DdTag dd(Object... contents) {
		return tag(DdTag.class, "dd", contents);
	}

	public static DecoratorTag decorator(Object... contents) {
		return tag(DecoratorTag.class, "decorator", contents);
	}

	public static DelTag del(Object... contents) {
		return tag(DelTag.class, "del", contents);
	}

	public static DetailsTag details(Object... contents) {
		return tag(DetailsTag.class, "details", contents);
	}

	public static DfnTag dfn(Object... contents) {
		return tag(DfnTag.class, "dfn", contents);
	}

	public static DialogTag dialog(Object... contents) {
		return tag(DialogTag.class, "dialog", contents);
	}

	public static DirTag dir(Object... contents) {
		return tag(DirTag.class, "dir", contents);
	}

	public static DivTag div(Object... contents) {
		return tag(DivTag.class, "div", contents);
	}

	public static DlTag dl(Object... contents) {
		return tag(DlTag.class, "dl", contents);
	}

	public static DtTag dt(Object... contents) {
		return tag(DtTag.class, "dt", contents);
	}

	public static ElementTag element(Object... contents) {
		return tag(ElementTag.class, "element", contents);
	}

	public static EmTag em(Object... contents) {
		return tag(EmTag.class, "em", contents);
	}

	public static EmbedTag embed(Object... contents) {
		return tag(EmbedTag.class, "embed", contents);
	}

	public static FieldsetTag fieldset(Object... contents) {
		return tag(FieldsetTag.class, "fieldset", contents);
	}

	public static FigcaptionTag figcaption(Object... contents) {
		return tag(FigcaptionTag.class, "figcaption", contents);
	}

	public static FigureTag figure(Object... contents) {
		return tag(FigureTag.class, "figure", contents);
	}

	public static FontTag font(Object... contents) {
		return tag(FontTag.class, "font", contents);
	}

	public static FooterTag footer(Object... contents) {
		return tag(FooterTag.class, "footer", contents);
	}

	public static FormTag form(Object... contents) {
		return tag(FormTag.class, "form", contents);
	}

	public static FrameTag frame(Object... contents) {
		return tag(FrameTag.class, "frame", contents);
	}

	public static FramesetTag frameset(Object... contents) {
		return tag(FramesetTag.class, "frameset", contents);
	}

	public static H1Tag h1(Object... contents) {
		return tag(H1Tag.class, "h1", contents);
	}

	public static H2Tag h2(Object... contents) {
		return tag(H2Tag.class, "h2", contents);
	}

	public static H3Tag h3(Object... contents) {
		return tag(H3Tag.class, "h3", contents);
	}

	public static H4Tag h4(Object... contents) {
		return tag(H4Tag.class, "h4", contents);
	}

	public static H5Tag h5(Object... contents) {
		return tag(H5Tag.class, "h5", contents);
	}

	public static H6Tag h6(Object... contents) {
		return tag(H6Tag.class, "h6", contents);
	}

	public static HeadTag head(Object... contents) {
		return tag(HeadTag.class, "head", contents);
	}

	public static HeaderTag header(Object... contents) {
		return tag(HeaderTag.class, "header", contents);
	}

	public static HgroupTag hgroup(Object... contents) {
		return tag(HgroupTag.class, "hgroup", contents);
	}

	public static HrTag hr(Object... contents) {
		return tag(HrTag.class, "hr", contents);
	}

	public static HtmlTag html(Object... contents) {
		return tag(HtmlTag.class, "html", contents);
	}

	public static ITag i(Object... contents) {
		return tag(ITag.class, "i", contents);
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

	public static InsTag ins(Object... contents) {
		return tag(InsTag.class, "ins", contents);
	}

	public static IsindexTag isindex(Object... contents) {
		return tag(IsindexTag.class, "isindex", contents);
	}

	public static KbdTag kbd(Object... contents) {
		return tag(KbdTag.class, "kbd", contents);
	}

	public static KeygenTag keygen(Object... contents) {
		return tag(KeygenTag.class, "keygen", contents);
	}

	public static LabelTag label(Object... contents) {
		return tag(LabelTag.class, "label", contents);
	}

	public static LegendTag legend(Object... contents) {
		return tag(LegendTag.class, "legend", contents);
	}

	public static LiTag li(Object... contents) {
		return tag(LiTag.class, "li", contents);
	}

	public static LinkTag link(Object... contents) {
		return tag(LinkTag.class, "link", contents);
	}

	public static ListingTag listing(Object... contents) {
		return tag(ListingTag.class, "listing", contents);
	}

	public static MainTag main(Object... contents) {
		return tag(MainTag.class, "main", contents);
	}

	public static MapTag map(Object... contents) {
		return tag(MapTag.class, "map", contents);
	}

	public static MarkTag mark(Object... contents) {
		return tag(MarkTag.class, "mark", contents);
	}

	public static MarqueeTag marquee(Object... contents) {
		return tag(MarqueeTag.class, "marquee", contents);
	}

	public static MenuTag menu(Object... contents) {
		return tag(MenuTag.class, "menu", contents);
	}

	public static MenuitemTag menuitem(Object... contents) {
		return tag(MenuitemTag.class, "menuitem", contents);
	}

	public static MetaTag meta(Object... contents) {
		return tag(MetaTag.class, "meta", contents);
	}

	public static MeterTag meter(Object... contents) {
		return tag(MeterTag.class, "meter", contents);
	}

	public static NavTag nav(Object... contents) {
		return tag(NavTag.class, "nav", contents);
	}

	public static NobrTag nobr(Object... contents) {
		return tag(NobrTag.class, "nobr", contents);
	}

	public static NoframesTag noframes(Object... contents) {
		return tag(NoframesTag.class, "noframes", contents);
	}

	public static NoscriptTag noscript(Object... contents) {
		return tag(NoscriptTag.class, "noscript", contents);
	}

	public static ObjectTag object(Object... contents) {
		return tag(ObjectTag.class, "object", contents);
	}

	public static OlTag ol(Object... contents) {
		return tag(OlTag.class, "ol", contents);
	}

	public static OptgroupTag optgroup(Object... contents) {
		return tag(OptgroupTag.class, "optgroup", contents);
	}

	public static OptionTag option(Object... contents) {
		return tag(OptionTag.class, "option", contents);
	}

	public static OutputTag output(Object... contents) {
		return tag(OutputTag.class, "output", contents);
	}

	public static PTag p(Object... contents) {
		return tag(PTag.class, "p", contents);
	}

	public static ParamTag param(Object... contents) {
		return tag(ParamTag.class, "param", contents);
	}

	public static PictureTag picture(Object... contents) {
		return tag(PictureTag.class, "picture", contents);
	}

	public static PlaintextTag plaintext(Object... contents) {
		return tag(PlaintextTag.class, "plaintext", contents);
	}

	public static PreTag pre(Object... contents) {
		return tag(PreTag.class, "pre", contents);
	}

	public static ProgressTag progress(Object... contents) {
		return tag(ProgressTag.class, "progress", contents);
	}

	public static QTag q(Object... contents) {
		return tag(QTag.class, "q", contents);
	}

	public static RpTag rp(Object... contents) {
		return tag(RpTag.class, "rp", contents);
	}

	public static RtTag rt(Object... contents) {
		return tag(RtTag.class, "rt", contents);
	}

	public static RubyTag ruby(Object... contents) {
		return tag(RubyTag.class, "ruby", contents);
	}

	public static STag s(Object... contents) {
		return tag(STag.class, "s", contents);
	}

	public static SampTag samp(Object... contents) {
		return tag(SampTag.class, "samp", contents);
	}

	public static ScriptTag script(Object... contents) {
		return tag(ScriptTag.class, "script", contents);
	}

	public static SectionTag section(Object... contents) {
		return tag(SectionTag.class, "section", contents);
	}

	public static SelectTag select(Object... contents) {
		return tag(SelectTag.class, "select", contents);
	}

	public static ShadowTag shadow(Object... contents) {
		return tag(ShadowTag.class, "shadow", contents);
	}

	public static SmallTag small(Object... contents) {
		return tag(SmallTag.class, "small", contents);
	}

	public static SourceTag source(Object... contents) {
		return tag(SourceTag.class, "source", contents);
	}

	public static SpacerTag spacer(Object... contents) {
		return tag(SpacerTag.class, "spacer", contents);
	}

	public static SpanTag span(Object... contents) {
		return tag(SpanTag.class, "span", contents);
	}

	public static StrikeTag strike(Object... contents) {
		return tag(StrikeTag.class, "strike", contents);
	}

	public static StrongTag strong(Object... contents) {
		return tag(StrongTag.class, "strong", contents);
	}

	public static StyleTag style(Object... contents) {
		return tag(StyleTag.class, "style", contents);
	}

	public static SubTag sub(Object... contents) {
		return tag(SubTag.class, "sub", contents);
	}

	public static SummaryTag summary(Object... contents) {
		return tag(SummaryTag.class, "summary", contents);
	}

	public static SupTag sup(Object... contents) {
		return tag(SupTag.class, "sup", contents);
	}

	public static TableTag table(Object... contents) {
		return tag(TableTag.class, "table", contents);
	}

	public static TbodyTag tbody(Object... contents) {
		return tag(TbodyTag.class, "tbody", contents);
	}

	public static TdTag td(Object... contents) {
		return tag(TdTag.class, "td", contents);
	}

	public static TemplateTag template(Object... contents) {
		return tag(TemplateTag.class, "template", contents);
	}

	public static TextareaTag textarea(Object... contents) {
		return tag(TextareaTag.class, "textarea", contents);
	}

	public static TfootTag tfoot(Object... contents) {
		return tag(TfootTag.class, "tfoot", contents);
	}

	public static ThTag th(Object... contents) {
		return tag(ThTag.class, "th", contents);
	}

	public static TheadTag thead(Object... contents) {
		return tag(TheadTag.class, "thead", contents);
	}

	public static TimeTag time(Object... contents) {
		return tag(TimeTag.class, "time", contents);
	}

	public static TitleTag title(Object... contents) {
		return tag(TitleTag.class, "title", contents);
	}

	public static TrTag tr(Object... contents) {
		return tag(TrTag.class, "tr", contents);
	}

	public static TrackTag track(Object... contents) {
		return tag(TrackTag.class, "track", contents);
	}

	public static TtTag tt(Object... contents) {
		return tag(TtTag.class, "tt", contents);
	}

	public static UTag u(Object... contents) {
		return tag(UTag.class, "u", contents);
	}

	public static UlTag ul(Object... contents) {
		return tag(UlTag.class, "ul", contents);
	}

	public static VideoTag video(Object... contents) {
		return tag(VideoTag.class, "video", contents);
	}

	public static WbrTag wbr(Object... contents) {
		return tag(WbrTag.class, "wbr", contents);
	}

	public static XmpTag xmp(Object... contents) {
		return tag(XmpTag.class, "xmp", contents);
	}

}
