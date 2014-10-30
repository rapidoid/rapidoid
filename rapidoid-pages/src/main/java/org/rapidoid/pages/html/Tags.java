package org.rapidoid.pages.html;

/*
 * #%L
 * rapidoid-pages
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

import org.rapidoid.pages.Tag;
import org.rapidoid.pages.Var;
import org.rapidoid.pages.impl.GuiContext;
import org.rapidoid.pages.impl.TagInterceptor;
import org.rapidoid.pages.impl.VarImpl;

public class Tags {

	protected final GuiContext ctx = new GuiContext();

	public Object _(String multiLanguageText) {
		// TODO implement internationalization
		return var(multiLanguageText);
	}

	public <T> Var<T> var(T value) {
		return new VarImpl<T>(ctx, value);
	}

	public <TAG extends Tag<?>> TAG tag(Class<TAG> clazz, String tagName, Object... contents) {
		return TagInterceptor.create(ctx, clazz, tagName, contents);
	}

	public ATag a(Object... contents) {
		return tag(ATag.class, "a", contents);
	}

	public AbbrTag abbr(Object... contents) {
		return tag(AbbrTag.class, "abbr", contents);
	}

	public AcronymTag acronym(Object... contents) {
		return tag(AcronymTag.class, "acronym", contents);
	}

	public AddressTag address(Object... contents) {
		return tag(AddressTag.class, "address", contents);
	}

	public AppletTag applet(Object... contents) {
		return tag(AppletTag.class, "applet", contents);
	}

	public AreaTag area(Object... contents) {
		return tag(AreaTag.class, "area", contents);
	}

	public ArticleTag article(Object... contents) {
		return tag(ArticleTag.class, "article", contents);
	}

	public AsideTag aside(Object... contents) {
		return tag(AsideTag.class, "aside", contents);
	}

	public AudioTag audio(Object... contents) {
		return tag(AudioTag.class, "audio", contents);
	}

	public BTag b(Object... contents) {
		return tag(BTag.class, "b", contents);
	}

	public BaseTag base(Object... contents) {
		return tag(BaseTag.class, "base", contents);
	}

	public BasefontTag basefont(Object... contents) {
		return tag(BasefontTag.class, "basefont", contents);
	}

	public BdiTag bdi(Object... contents) {
		return tag(BdiTag.class, "bdi", contents);
	}

	public BdoTag bdo(Object... contents) {
		return tag(BdoTag.class, "bdo", contents);
	}

	public BgsoundTag bgsound(Object... contents) {
		return tag(BgsoundTag.class, "bgsound", contents);
	}

	public BigTag big(Object... contents) {
		return tag(BigTag.class, "big", contents);
	}

	public BlinkTag blink(Object... contents) {
		return tag(BlinkTag.class, "blink", contents);
	}

	public BlockquoteTag blockquote(Object... contents) {
		return tag(BlockquoteTag.class, "blockquote", contents);
	}

	public BodyTag body(Object... contents) {
		return tag(BodyTag.class, "body", contents);
	}

	public BrTag br(Object... contents) {
		return tag(BrTag.class, "br", contents);
	}

	public ButtonTag button(Object... contents) {
		return tag(ButtonTag.class, "button", contents);
	}

	public CanvasTag canvas(Object... contents) {
		return tag(CanvasTag.class, "canvas", contents);
	}

	public CaptionTag caption(Object... contents) {
		return tag(CaptionTag.class, "caption", contents);
	}

	public CenterTag center(Object... contents) {
		return tag(CenterTag.class, "center", contents);
	}

	public CiteTag cite(Object... contents) {
		return tag(CiteTag.class, "cite", contents);
	}

	public CodeTag code(Object... contents) {
		return tag(CodeTag.class, "code", contents);
	}

	public ColTag col(Object... contents) {
		return tag(ColTag.class, "col", contents);
	}

	public ColgroupTag colgroup(Object... contents) {
		return tag(ColgroupTag.class, "colgroup", contents);
	}

	public ContentTag content(Object... contents) {
		return tag(ContentTag.class, "content", contents);
	}

	public DataTag data(Object... contents) {
		return tag(DataTag.class, "data", contents);
	}

	public DatalistTag datalist(Object... contents) {
		return tag(DatalistTag.class, "datalist", contents);
	}

	public DdTag dd(Object... contents) {
		return tag(DdTag.class, "dd", contents);
	}

	public DecoratorTag decorator(Object... contents) {
		return tag(DecoratorTag.class, "decorator", contents);
	}

	public DelTag del(Object... contents) {
		return tag(DelTag.class, "del", contents);
	}

	public DetailsTag details(Object... contents) {
		return tag(DetailsTag.class, "details", contents);
	}

	public DfnTag dfn(Object... contents) {
		return tag(DfnTag.class, "dfn", contents);
	}

	public DialogTag dialog(Object... contents) {
		return tag(DialogTag.class, "dialog", contents);
	}

	public DirTag dir(Object... contents) {
		return tag(DirTag.class, "dir", contents);
	}

	public DivTag div(Object... contents) {
		return tag(DivTag.class, "div", contents);
	}

	public DlTag dl(Object... contents) {
		return tag(DlTag.class, "dl", contents);
	}

	public DtTag dt(Object... contents) {
		return tag(DtTag.class, "dt", contents);
	}

	public ElementTag element(Object... contents) {
		return tag(ElementTag.class, "element", contents);
	}

	public EmTag em(Object... contents) {
		return tag(EmTag.class, "em", contents);
	}

	public EmbedTag embed(Object... contents) {
		return tag(EmbedTag.class, "embed", contents);
	}

	public FieldsetTag fieldset(Object... contents) {
		return tag(FieldsetTag.class, "fieldset", contents);
	}

	public FigcaptionTag figcaption(Object... contents) {
		return tag(FigcaptionTag.class, "figcaption", contents);
	}

	public FigureTag figure(Object... contents) {
		return tag(FigureTag.class, "figure", contents);
	}

	public FontTag font(Object... contents) {
		return tag(FontTag.class, "font", contents);
	}

	public FooterTag footer(Object... contents) {
		return tag(FooterTag.class, "footer", contents);
	}

	public FormTag form(Object... contents) {
		return tag(FormTag.class, "form", contents);
	}

	public FrameTag frame(Object... contents) {
		return tag(FrameTag.class, "frame", contents);
	}

	public FramesetTag frameset(Object... contents) {
		return tag(FramesetTag.class, "frameset", contents);
	}

	public H1Tag h1(Object... contents) {
		return tag(H1Tag.class, "h1", contents);
	}

	public H2Tag h2(Object... contents) {
		return tag(H2Tag.class, "h2", contents);
	}

	public H3Tag h3(Object... contents) {
		return tag(H3Tag.class, "h3", contents);
	}

	public H4Tag h4(Object... contents) {
		return tag(H4Tag.class, "h4", contents);
	}

	public H5Tag h5(Object... contents) {
		return tag(H5Tag.class, "h5", contents);
	}

	public H6Tag h6(Object... contents) {
		return tag(H6Tag.class, "h6", contents);
	}

	public HeadTag head(Object... contents) {
		return tag(HeadTag.class, "head", contents);
	}

	public HeaderTag header(Object... contents) {
		return tag(HeaderTag.class, "header", contents);
	}

	public HgroupTag hgroup(Object... contents) {
		return tag(HgroupTag.class, "hgroup", contents);
	}

	public HrTag hr(Object... contents) {
		return tag(HrTag.class, "hr", contents);
	}

	public HtmlTag html(Object... contents) {
		return tag(HtmlTag.class, "html", contents);
	}

	public ITag i(Object... contents) {
		return tag(ITag.class, "i", contents);
	}

	public IframeTag iframe(Object... contents) {
		return tag(IframeTag.class, "iframe", contents);
	}

	public ImgTag img(Object... contents) {
		return tag(ImgTag.class, "img", contents);
	}

	public InputTag input(Object... contents) {
		return tag(InputTag.class, "input", contents);
	}

	public InsTag ins(Object... contents) {
		return tag(InsTag.class, "ins", contents);
	}

	public IsindexTag isindex(Object... contents) {
		return tag(IsindexTag.class, "isindex", contents);
	}

	public KbdTag kbd(Object... contents) {
		return tag(KbdTag.class, "kbd", contents);
	}

	public KeygenTag keygen(Object... contents) {
		return tag(KeygenTag.class, "keygen", contents);
	}

	public LabelTag label(Object... contents) {
		return tag(LabelTag.class, "label", contents);
	}

	public LegendTag legend(Object... contents) {
		return tag(LegendTag.class, "legend", contents);
	}

	public LiTag li(Object... contents) {
		return tag(LiTag.class, "li", contents);
	}

	public LinkTag link(Object... contents) {
		return tag(LinkTag.class, "link", contents);
	}

	public ListingTag listing(Object... contents) {
		return tag(ListingTag.class, "listing", contents);
	}

	public MainTag main(Object... contents) {
		return tag(MainTag.class, "main", contents);
	}

	public MapTag map(Object... contents) {
		return tag(MapTag.class, "map", contents);
	}

	public MarkTag mark(Object... contents) {
		return tag(MarkTag.class, "mark", contents);
	}

	public MarqueeTag marquee(Object... contents) {
		return tag(MarqueeTag.class, "marquee", contents);
	}

	public MenuTag menu(Object... contents) {
		return tag(MenuTag.class, "menu", contents);
	}

	public MenuitemTag menuitem(Object... contents) {
		return tag(MenuitemTag.class, "menuitem", contents);
	}

	public MetaTag meta(Object... contents) {
		return tag(MetaTag.class, "meta", contents);
	}

	public MeterTag meter(Object... contents) {
		return tag(MeterTag.class, "meter", contents);
	}

	public NavTag nav(Object... contents) {
		return tag(NavTag.class, "nav", contents);
	}

	public NobrTag nobr(Object... contents) {
		return tag(NobrTag.class, "nobr", contents);
	}

	public NoframesTag noframes(Object... contents) {
		return tag(NoframesTag.class, "noframes", contents);
	}

	public NoscriptTag noscript(Object... contents) {
		return tag(NoscriptTag.class, "noscript", contents);
	}

	public ObjectTag object(Object... contents) {
		return tag(ObjectTag.class, "object", contents);
	}

	public OlTag ol(Object... contents) {
		return tag(OlTag.class, "ol", contents);
	}

	public OptgroupTag optgroup(Object... contents) {
		return tag(OptgroupTag.class, "optgroup", contents);
	}

	public OptionTag option(Object... contents) {
		return tag(OptionTag.class, "option", contents);
	}

	public OutputTag output(Object... contents) {
		return tag(OutputTag.class, "output", contents);
	}

	public PTag p(Object... contents) {
		return tag(PTag.class, "p", contents);
	}

	public ParamTag param(Object... contents) {
		return tag(ParamTag.class, "param", contents);
	}

	public PictureTag picture(Object... contents) {
		return tag(PictureTag.class, "picture", contents);
	}

	public PlaintextTag plaintext(Object... contents) {
		return tag(PlaintextTag.class, "plaintext", contents);
	}

	public PreTag pre(Object... contents) {
		return tag(PreTag.class, "pre", contents);
	}

	public ProgressTag progress(Object... contents) {
		return tag(ProgressTag.class, "progress", contents);
	}

	public QTag q(Object... contents) {
		return tag(QTag.class, "q", contents);
	}

	public RpTag rp(Object... contents) {
		return tag(RpTag.class, "rp", contents);
	}

	public RtTag rt(Object... contents) {
		return tag(RtTag.class, "rt", contents);
	}

	public RubyTag ruby(Object... contents) {
		return tag(RubyTag.class, "ruby", contents);
	}

	public STag s(Object... contents) {
		return tag(STag.class, "s", contents);
	}

	public SampTag samp(Object... contents) {
		return tag(SampTag.class, "samp", contents);
	}

	public ScriptTag script(Object... contents) {
		return tag(ScriptTag.class, "script", contents);
	}

	public SectionTag section(Object... contents) {
		return tag(SectionTag.class, "section", contents);
	}

	public SelectTag select(Object... contents) {
		return tag(SelectTag.class, "select", contents);
	}

	public ShadowTag shadow(Object... contents) {
		return tag(ShadowTag.class, "shadow", contents);
	}

	public SmallTag small(Object... contents) {
		return tag(SmallTag.class, "small", contents);
	}

	public SourceTag source(Object... contents) {
		return tag(SourceTag.class, "source", contents);
	}

	public SpacerTag spacer(Object... contents) {
		return tag(SpacerTag.class, "spacer", contents);
	}

	public SpanTag span(Object... contents) {
		return tag(SpanTag.class, "span", contents);
	}

	public StrikeTag strike(Object... contents) {
		return tag(StrikeTag.class, "strike", contents);
	}

	public StrongTag strong(Object... contents) {
		return tag(StrongTag.class, "strong", contents);
	}

	public StyleTag style(Object... contents) {
		return tag(StyleTag.class, "style", contents);
	}

	public SubTag sub(Object... contents) {
		return tag(SubTag.class, "sub", contents);
	}

	public SummaryTag summary(Object... contents) {
		return tag(SummaryTag.class, "summary", contents);
	}

	public SupTag sup(Object... contents) {
		return tag(SupTag.class, "sup", contents);
	}

	public TableTag table(Object... contents) {
		return tag(TableTag.class, "table", contents);
	}

	public TbodyTag tbody(Object... contents) {
		return tag(TbodyTag.class, "tbody", contents);
	}

	public TdTag td(Object... contents) {
		return tag(TdTag.class, "td", contents);
	}

	public TemplateTag template(Object... contents) {
		return tag(TemplateTag.class, "template", contents);
	}

	public TextareaTag textarea(Object... contents) {
		return tag(TextareaTag.class, "textarea", contents);
	}

	public TfootTag tfoot(Object... contents) {
		return tag(TfootTag.class, "tfoot", contents);
	}

	public ThTag th(Object... contents) {
		return tag(ThTag.class, "th", contents);
	}

	public TheadTag thead(Object... contents) {
		return tag(TheadTag.class, "thead", contents);
	}

	public TimeTag time(Object... contents) {
		return tag(TimeTag.class, "time", contents);
	}

	public TitleTag title(Object... contents) {
		return tag(TitleTag.class, "title", contents);
	}

	public TrTag tr(Object... contents) {
		return tag(TrTag.class, "tr", contents);
	}

	public TrackTag track(Object... contents) {
		return tag(TrackTag.class, "track", contents);
	}

	public TtTag tt(Object... contents) {
		return tag(TtTag.class, "tt", contents);
	}

	public UTag u(Object... contents) {
		return tag(UTag.class, "u", contents);
	}

	public UlTag ul(Object... contents) {
		return tag(UlTag.class, "ul", contents);
	}

	public VideoTag video(Object... contents) {
		return tag(VideoTag.class, "video", contents);
	}

	public WbrTag wbr(Object... contents) {
		return tag(WbrTag.class, "wbr", contents);
	}

	public XmpTag xmp(Object... contents) {
		return tag(XmpTag.class, "xmp", contents);
	}


}