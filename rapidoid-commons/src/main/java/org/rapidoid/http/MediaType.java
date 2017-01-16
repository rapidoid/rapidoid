package org.rapidoid.http;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.Arrays;
import java.util.Map;

/*
 * #%L
 * rapidoid-commons
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class MediaType extends RapidoidThing {

	private static final Map<String, MediaType> FILE_EXTENSISONS = U.map();
	private static final String[] NO_ATTR = new String[0];
	private static final String[] UTF8_ATTR = {"charset=utf-8"};

	/*******************************************************/

	public static final MediaType ANY = create("*/*");
	public static final MediaType TEXT_ANY = create("text/*");
	public static final MediaType APPLICATION_ANY = create("application/*");
	public static final MediaType IMAGE_ANY = create("image/*");
	public static final MediaType VIDEO_ANY = create("video/*");
	public static final MediaType AUDIO_ANY = create("audio/*");

	/*******************************************************/

	public static final MediaType APPLICATION_ANDREW_INSET = create("application/andrew-inset", "ez");
	public static final MediaType APPLICATION_ANNODEX = create("application/annodex", "anx");
	public static final MediaType APPLICATION_APPLIXWARE = create("application/applixware", "aw");
	public static final MediaType APPLICATION_ATOMCAT_XML_UTF8 = createUTF8("application/atomcat+xml", "atomcat");
	public static final MediaType APPLICATION_ATOMSERV_XML_UTF8 = createUTF8("application/atomserv+xml", "atomsrv");
	public static final MediaType APPLICATION_ATOMSVC_XML_UTF8 = createUTF8("application/atomsvc+xml", "atomsvc");
	public static final MediaType APPLICATION_ATOM_XML_UTF8 = createUTF8("application/atom+xml", "atom");
	public static final MediaType APPLICATION_BBOLIN = create("application/bbolin", "lin");
	public static final MediaType APPLICATION_CAP = create("application/cap", "cap", "pcap");
	public static final MediaType APPLICATION_CCXML_XML = create("application/ccxml+xml", "ccxml");
	public static final MediaType APPLICATION_CDMI_CAPABILITY = create("application/cdmi-capability", "cdmia");
	public static final MediaType APPLICATION_CDMI_CONTAINER = create("application/cdmi-container", "cdmic");
	public static final MediaType APPLICATION_CDMI_DOMAIN = create("application/cdmi-domain", "cdmid");
	public static final MediaType APPLICATION_CDMI_OBJECT = create("application/cdmi-object", "cdmio");
	public static final MediaType APPLICATION_CDMI_QUEUE = create("application/cdmi-queue", "cdmiq");
	public static final MediaType APPLICATION_CU_SEEME = create("application/cu-seeme", "cu");
	public static final MediaType APPLICATION_DAVMOUNT_XML = create("application/davmount+xml", "davmount");
	public static final MediaType APPLICATION_DOCBOOK_XML = create("application/docbook+xml", "dbk");
	public static final MediaType APPLICATION_DSPTYPE = create("application/dsptype", "tsp");
	public static final MediaType APPLICATION_DSSC_DER = create("application/dssc+der", "dssc");
	public static final MediaType APPLICATION_DSSC_XML = create("application/dssc+xml", "xdssc");
	public static final MediaType APPLICATION_ECMASCRIPT_UTF8 = createUTF8("application/ecmascript", "ecma", "es");
	public static final MediaType APPLICATION_EMMA_XML = create("application/emma+xml", "emma");
	public static final MediaType APPLICATION_EPUB_ZIP = create("application/epub+zip", "epub");
	public static final MediaType APPLICATION_EXI = create("application/exi", "exi");
	public static final MediaType APPLICATION_FONT_TDPFR = create("application/font-tdpfr", "pfr");
	public static final MediaType APPLICATION_FUTURESPLASH = create("application/futuresplash", "spl");
	public static final MediaType APPLICATION_GML_XML = create("application/gml+xml", "gml");
	public static final MediaType APPLICATION_GPX_XML = create("application/gpx+xml", "gpx");
	public static final MediaType APPLICATION_GXF = create("application/gxf", "gxf");
	public static final MediaType APPLICATION_HTA = create("application/hta", "hta");
	public static final MediaType APPLICATION_HYPERSTUDIO = create("application/hyperstudio", "stk");
	public static final MediaType APPLICATION_INKML_XML = create("application/inkml+xml", "ink", "inkml");
	public static final MediaType APPLICATION_IPFIX = create("application/ipfix", "ipfix");
	public static final MediaType APPLICATION_JAVA_ARCHIVE = create("application/java-archive", "jar");
	public static final MediaType APPLICATION_JAVASCRIPT_UTF8 = createUTF8("application/javascript", "js");
	public static final MediaType APPLICATION_JAVA_SERIALIZED_OBJECT = create("application/java-serialized-object",
		"ser");
	public static final MediaType APPLICATION_JAVA_VM = create("application/java-vm", "class");
	public static final MediaType APPLICATION_JSON = create("application/json", "json", "map");
	public static final MediaType APPLICATION_JSONML_JSON = create("application/jsonml+json", "jsonml");
	public static final MediaType APPLICATION_LOST_XML = create("application/lost+xml", "lostxml");
	public static final MediaType APPLICATION_M3G = create("application/m3g", "m3g");
	public static final MediaType APPLICATION_MAC_BINHEX40 = create("application/mac-binhex40", "hqx");
	public static final MediaType APPLICATION_MAC_COMPACTPRO = create("application/mac-compactpro", "cpt");
	public static final MediaType APPLICATION_MADS_XML = create("application/mads+xml", "mads");
	public static final MediaType APPLICATION_MARC = create("application/marc", "mrc");
	public static final MediaType APPLICATION_MARCXML_XML = create("application/marcxml+xml", "mrcx");
	public static final MediaType APPLICATION_MATHEMATICA = create("application/mathematica", "ma", "mb", "nb", "nbp");
	public static final MediaType APPLICATION_MATHML_XML = create("application/mathml+xml", "mathml");
	public static final MediaType APPLICATION_MBOX = create("application/mbox", "mbox");
	public static final MediaType APPLICATION_MEDIASERVERCONTROL_XML = create("application/mediaservercontrol+xml",
		"MSCML");
	public static final MediaType APPLICATION_METALINK4_XML = create("application/metalink4+xml", "meta4");
	public static final MediaType APPLICATION_METALINK_XML = create("application/metalink+xml", "metalink");
	public static final MediaType APPLICATION_METS_XML = create("application/mets+xml", "mets");
	public static final MediaType APPLICATION_MODS_XML = create("application/mods+xml", "mods");
	public static final MediaType APPLICATION_MP21 = create("application/mp21", "m21", "mp21");
	public static final MediaType APPLICATION_MP4 = create("application/mp4", "mp4s");
	public static final MediaType APPLICATION_MSACCESS = create("application/msaccess", "mdb");
	public static final MediaType APPLICATION_MSWORD = create("application/msword", "doc", "dot");
	public static final MediaType APPLICATION_MXF = create("application/mxf", "mxf");
	public static final MediaType APPLICATION_OCTET_STREAM = create("application/octet-stream", "bin", "dms", "lrf",
		"MAR", "SO", "DIST", "DISTZ", "PKG", "BPK", "DUMP", "ELC", "DEPLOY");
	public static final MediaType APPLICATION_ODA = create("application/oda", "oda");
	public static final MediaType APPLICATION_OEBPS_PACKAGE_XML = create("application/oebps-package+xml", "opf");
	public static final MediaType APPLICATION_OGG = create("application/ogg", "ogx");
	public static final MediaType APPLICATION_OMDOC_XML = create("application/omdoc+xml", "omdoc");
	public static final MediaType APPLICATION_ONENOTE = create("application/onenote", "one", "onetoc", "onetoc2",
		"ONETMP", "ONEPKG");
	public static final MediaType APPLICATION_OXPS = create("application/oxps", "oxps");
	public static final MediaType APPLICATION_PATCH_OPS_ERROR_XML = create("application/patch-ops-error+xml", "xer");
	public static final MediaType APPLICATION_PDF = create("application/pdf", "pdf");
	public static final MediaType APPLICATION_PGP_ENCRYPTED = create("application/pgp-encrypted", "pgp");
	public static final MediaType APPLICATION_PGP_KEYS = create("application/pgp-keys", "key");
	public static final MediaType APPLICATION_PGP_SIGNATURE = create("application/pgp-signature", "asc", "pgp", "sig");
	public static final MediaType APPLICATION_PICS_RULES = create("application/pics-rules", "prf");
	public static final MediaType APPLICATION_PKCS10 = create("application/pkcs10", "p10");
	public static final MediaType APPLICATION_PKCS7_MIME = create("application/pkcs7-mime", "p7m", "p7c");
	public static final MediaType APPLICATION_PKCS7_SIGNATURE = create("application/pkcs7-signature", "p7s");
	public static final MediaType APPLICATION_PKCS8 = create("application/pkcs8", "p8");
	public static final MediaType APPLICATION_PKIX_ATTR_CERT = create("application/pkix-attr-cert", "ac");
	public static final MediaType APPLICATION_PKIX_CERT = create("application/pkix-cert", "cer");
	public static final MediaType APPLICATION_PKIXCMP = create("application/pkixcmp", "pki");
	public static final MediaType APPLICATION_PKIX_CRL = create("application/pkix-crl", "crl");
	public static final MediaType APPLICATION_PKIX_PKIPATH = create("application/pkix-pkipath", "pkipath");
	public static final MediaType APPLICATION_PLS_XML = create("application/pls+xml", "pls");
	public static final MediaType APPLICATION_POSTSCRIPT = create("application/postscript", "ps", "ai", "eps", "epsi",
		"EPSF", "EPS2", "EPS3");
	public static final MediaType APPLICATION_PRS_CWW = create("application/prs.cww", "cww");
	public static final MediaType APPLICATION_PSKC_XML = create("application/pskc+xml", "pskcxml");
	public static final MediaType APPLICATION_RAR = create("application/rar", "rar");
	public static final MediaType APPLICATION_RDF_XML = create("application/rdf+xml", "rdf");
	public static final MediaType APPLICATION_REGINFO_XML = create("application/reginfo+xml", "rif");
	public static final MediaType APPLICATION_RELAX_NG_COMPACT_SYNTAX = create("application/relax-ng-compact-syntax",
		"RNC");
	public static final MediaType APPLICATION_RESOURCE_LISTS_DIFF_XML = create("application/resource-lists-diff+xml",
		"RLD");
	public static final MediaType APPLICATION_RESOURCE_LISTS_XML = create("application/resource-lists+xml", "rl");
	public static final MediaType APPLICATION_RLS_SERVICES_XML = create("application/rls-services+xml", "rs");
	public static final MediaType APPLICATION_RPKI_GHOSTBUSTERS = create("application/rpki-ghostbusters", "gbr");
	public static final MediaType APPLICATION_RPKI_MANIFEST = create("application/rpki-manifest", "mft");
	public static final MediaType APPLICATION_RPKI_ROA = create("application/rpki-roa", "roa");
	public static final MediaType APPLICATION_RSD_XML_UTF8 = createUTF8("application/rsd+xml", "rsd");
	public static final MediaType APPLICATION_RSS_XML_UTF8 = createUTF8("application/rss+xml", "rss");
	public static final MediaType APPLICATION_RTF = create("application/rtf", "rtf");
	public static final MediaType APPLICATION_SBML_XML = create("application/sbml+xml", "sbml");
	public static final MediaType APPLICATION_SCVP_CV_REQUEST = create("application/scvp-cv-request", "scq");
	public static final MediaType APPLICATION_SCVP_CV_RESPONSE = create("application/scvp-cv-response", "scs");
	public static final MediaType APPLICATION_SCVP_VP_REQUEST = create("application/scvp-vp-request", "spq");
	public static final MediaType APPLICATION_SCVP_VP_RESPONSE = create("application/scvp-vp-response", "spp");
	public static final MediaType APPLICATION_SDP = create("application/sdp", "sdp");
	public static final MediaType APPLICATION_SET_PAYMENT_INITIATION = create("application/set-payment-initiation",
		"SETPAY");
	public static final MediaType APPLICATION_SET_REGISTRATION_INITIATION = create(
		"APPLICATION/SET-REGISTRATION-INITIATION", "SETREG");
	public static final MediaType APPLICATION_SHF_XML = create("application/shf+xml", "shf");
	public static final MediaType APPLICATION_SLA = create("application/sla", "stl");
	public static final MediaType APPLICATION_SMIL = create("application/smil", "smi", "smil");
	public static final MediaType APPLICATION_SMIL_XML = create("application/smil+xml", "smi", "smil");
	public static final MediaType APPLICATION_SPARQL_QUERY = create("application/sparql-query", "rq");
	public static final MediaType APPLICATION_SPARQL_RESULTS_XML = create("application/sparql-results+xml", "srx");
	public static final MediaType APPLICATION_SRGS = create("application/srgs", "gram");
	public static final MediaType APPLICATION_SRGS_XML = create("application/srgs+xml", "grxml");
	public static final MediaType APPLICATION_SRU_XML = create("application/sru+xml", "sru");
	public static final MediaType APPLICATION_SSDL_XML = create("application/ssdl+xml", "ssdl");
	public static final MediaType APPLICATION_SSML_XML = create("application/ssml+xml", "ssml");
	public static final MediaType APPLICATION_TEI_XML = create("application/tei+xml", "tei", "teicorpus");
	public static final MediaType APPLICATION_THRAUD_XML = create("application/thraud+xml", "tfi");
	public static final MediaType APPLICATION_TIMESTAMPED_DATA = create("application/timestamped-data", "tsd");
	public static final MediaType APPLICATION_VND_3GPP2_TCAP = create("application/vnd.3gpp2.tcap", "tcap");
	public static final MediaType APPLICATION_VND_3GPP_PIC_BW_LARGE = create("application/vnd.3gpp.pic-bw-large", "plb");
	public static final MediaType APPLICATION_VND_3GPP_PIC_BW_SMALL = create("application/vnd.3gpp.pic-bw-small", "psb");
	public static final MediaType APPLICATION_VND_3GPP_PIC_BW_VAR = create("application/vnd.3gpp.pic-bw-var", "pvb");
	public static final MediaType APPLICATION_VND_3M_POST_IT_NOTES = create("application/vnd.3m.post-it-notes", "pwn");
	public static final MediaType APPLICATION_VND_ACCPAC_SIMPLY_ASO = create("application/vnd.accpac.simply.aso", "aso");
	public static final MediaType APPLICATION_VND_ACCPAC_SIMPLY_IMP = create("application/vnd.accpac.simply.imp", "imp");
	public static final MediaType APPLICATION_VND_ACUCOBOL = create("application/vnd.acucobol", "acu");
	public static final MediaType APPLICATION_VND_ACUCORP = create("application/vnd.acucorp", "atc", "acutc");
	public static final MediaType APPLICATION_VND_ADOBE_AIR_APPLICATION_INSTALLER_PACKAGE_ZIP = create(
		"APPLICATION/VND.ADOBE.AIR-APPLICATION-INSTALLER-PACKAGE+ZIP", "AIR");
	public static final MediaType APPLICATION_VND_ADOBE_FORMSCENTRAL_FCDT = create(
		"APPLICATION/VND.ADOBE.FORMSCENTRAL.FCDT", "FCDT");
	public static final MediaType APPLICATION_VND_ADOBE_FXP = create("application/vnd.adobe.fxp", "fxp", "fxpl");
	public static final MediaType APPLICATION_VND_ADOBE_XDP_XML = create("application/vnd.adobe.xdp+xml", "xdp");
	public static final MediaType APPLICATION_VND_ADOBE_XFDF = create("application/vnd.adobe.xfdf", "xfdf");
	public static final MediaType APPLICATION_VND_AHEAD_SPACE = create("application/vnd.ahead.space", "ahead");
	public static final MediaType APPLICATION_VND_AIRZIP_FILESECURE_AZF = create(
		"application/vnd.airzip.filesecure.azf", "AZF");
	public static final MediaType APPLICATION_VND_AIRZIP_FILESECURE_AZS = create(
		"application/vnd.airzip.filesecure.azs", "AZS");
	public static final MediaType APPLICATION_VND_AMAZON_EBOOK = create("application/vnd.amazon.ebook", "azw");
	public static final MediaType APPLICATION_VND_AMERICANDYNAMICS_ACC = create("application/vnd.americandynamics.acc",
		"ACC");
	public static final MediaType APPLICATION_VND_AMIGA_AMI = create("application/vnd.amiga.ami", "ami");
	public static final MediaType APPLICATION_VND_ANDROID_PACKAGE_ARCHIVE = create(
		"APPLICATION/VND.ANDROID.PACKAGE-ARCHIVE", "APK");
	public static final MediaType APPLICATION_VND_ANSER_WEB_CERTIFICATE_ISSUE_INITIATION = create(
		"APPLICATION/VND.ANSER-WEB-CERTIFICATE-ISSUE-INITIATION", "CII");
	public static final MediaType APPLICATION_VND_ANSER_WEB_FUNDS_TRANSFER_INITIATION = create(
		"APPLICATION/VND.ANSER-WEB-FUNDS-TRANSFER-INITIATION", "FTI");
	public static final MediaType APPLICATION_VND_ANTIX_GAME_COMPONENT = create("application/vnd.antix.game-component",
		"ATX");
	public static final MediaType APPLICATION_VND_APPLE_INSTALLER_XML = create("application/vnd.apple.installer+xml",
		"MPKG");
	public static final MediaType APPLICATION_VND_APPLE_MPEGURL = create("application/vnd.apple.mpegurl", "m3u8");
	public static final MediaType APPLICATION_VND_ARISTANETWORKS_SWI = create("application/vnd.aristanetworks.swi",
		"swi");
	public static final MediaType APPLICATION_VND_ASTRAEA_SOFTWARE_IOTA = create(
		"application/vnd.astraea-software.iota", "IOTA");
	public static final MediaType APPLICATION_VND_AUDIOGRAPH = create("application/vnd.audiograph", "aep");
	public static final MediaType APPLICATION_VND_BLUEICE_MULTIPASS = create("application/vnd.blueice.multipass", "mpm");
	public static final MediaType APPLICATION_VND_BMI = create("application/vnd.bmi", "bmi");
	public static final MediaType APPLICATION_VND_BUSINESSOBJECTS = create("application/vnd.businessobjects", "rep");
	public static final MediaType APPLICATION_VND_CHEMDRAW_XML = create("application/vnd.chemdraw+xml", "cdxml");
	public static final MediaType APPLICATION_VND_CHIPNUTS_KARAOKE_MMD = create("application/vnd.chipnuts.karaoke-mmd",
		"MMD");
	public static final MediaType APPLICATION_VND_CINDERELLA = create("application/vnd.cinderella", "cdy");
	public static final MediaType APPLICATION_VND_CLAYMORE = create("application/vnd.claymore", "cla");
	public static final MediaType APPLICATION_VND_CLOANTO_RP9 = create("application/vnd.cloanto.rp9", "rp9");
	public static final MediaType APPLICATION_VND_CLONK_C4GROUP = create("application/vnd.clonk.c4group", "c4g", "c4d",
		"C4F", "C4P", "C4U");
	public static final MediaType APPLICATION_VND_CLUETRUST_CARTOMOBILE_CONFIG = create(
		"APPLICATION/VND.CLUETRUST.CARTOMOBILE-CONFIG", "C11AMC");
	public static final MediaType APPLICATION_VND_CLUETRUST_CARTOMOBILE_CONFIG_PKG = create(
		"APPLICATION/VND.CLUETRUST.CARTOMOBILE-CONFIG-PKG", "C11AMZ");
	public static final MediaType APPLICATION_VND_COMMONSPACE = create("application/vnd.commonspace", "csp");
	public static final MediaType APPLICATION_VND_CONTACT_CMSG = create("application/vnd.contact.cmsg", "cdbcmsg");
	public static final MediaType APPLICATION_VND_COSMOCALLER = create("application/vnd.cosmocaller", "cmc");
	public static final MediaType APPLICATION_VND_CRICK_CLICKER = create("application/vnd.crick.clicker", "clkx");
	public static final MediaType APPLICATION_VND_CRICK_CLICKER_KEYBOARD = create(
		"APPLICATION/VND.CRICK.CLICKER.KEYBOARD", "CLKK");
	public static final MediaType APPLICATION_VND_CRICK_CLICKER_PALETTE = create(
		"application/vnd.crick.clicker.palette", "CLKP");
	public static final MediaType APPLICATION_VND_CRICK_CLICKER_TEMPLATE = create(
		"APPLICATION/VND.CRICK.CLICKER.TEMPLATE", "CLKT");
	public static final MediaType APPLICATION_VND_CRICK_CLICKER_WORDBANK = create(
		"APPLICATION/VND.CRICK.CLICKER.WORDBANK", "CLKW");
	public static final MediaType APPLICATION_VND_CRITICALTOOLS_WBS_XML = create(
		"application/vnd.criticaltools.wbs+xml", "WBS");
	public static final MediaType APPLICATION_VND_CTC_POSML = create("application/vnd.ctc-posml", "pml");
	public static final MediaType APPLICATION_VND_CUPS_PPD = create("application/vnd.cups-ppd", "ppd");
	public static final MediaType APPLICATION_VND_CURL_CAR = create("application/vnd.curl.car", "car");
	public static final MediaType APPLICATION_VND_CURL_PCURL = create("application/vnd.curl.pcurl", "pcurl");
	public static final MediaType APPLICATION_VND_DART = create("application/vnd.dart", "dart");
	public static final MediaType APPLICATION_VND_DATA_VISION_RDZ = create("application/vnd.data-vision.rdz", "rdz");
	public static final MediaType APPLICATION_VND_DECE_DATA = create("application/vnd.dece.data", "uvf", "uvvf", "uvd",
		"UVVD");
	public static final MediaType APPLICATION_VND_DECE_TTML_XML = create("application/vnd.dece.ttml+xml", "uvt", "uvvt");
	public static final MediaType APPLICATION_VND_DECE_UNSPECIFIED = create("application/vnd.dece.unspecified", "uvx",
		"UVVX");
	public static final MediaType APPLICATION_VND_DECE_ZIP = create("application/vnd.dece.zip", "uvz", "uvvz");
	public static final MediaType APPLICATION_VND_DENOVO_FCSELAYOUT_LINK = create(
		"APPLICATION/VND.DENOVO.FCSELAYOUT-LINK", "FE_LAUNCH");
	public static final MediaType APPLICATION_VND_DNA = create("application/vnd.dna", "dna");
	public static final MediaType APPLICATION_VND_DOLBY_MLP = create("application/vnd.dolby.mlp", "mlp");
	public static final MediaType APPLICATION_VND_DPGRAPH = create("application/vnd.dpgraph", "dpg");
	public static final MediaType APPLICATION_VND_DREAMFACTORY = create("application/vnd.dreamfactory", "dfac");
	public static final MediaType APPLICATION_VND_DS_KEYPOINT = create("application/vnd.ds-keypoint", "kpxx");
	public static final MediaType APPLICATION_VND_DVB_AIT = create("application/vnd.dvb.ait", "ait");
	public static final MediaType APPLICATION_VND_DVB_SERVICE = create("application/vnd.dvb.service", "svc");
	public static final MediaType APPLICATION_VND_DYNAGEO = create("application/vnd.dynageo", "geo");
	public static final MediaType APPLICATION_VND_ECOWIN_CHART = create("application/vnd.ecowin.chart", "mag");
	public static final MediaType APPLICATION_VND_ENLIVEN = create("application/vnd.enliven", "nml");
	public static final MediaType APPLICATION_VND_EPSON_ESF = create("application/vnd.epson.esf", "esf");
	public static final MediaType APPLICATION_VND_EPSON_MSF = create("application/vnd.epson.msf", "msf");
	public static final MediaType APPLICATION_VND_EPSON_QUICKANIME = create("application/vnd.epson.quickanime", "qam");
	public static final MediaType APPLICATION_VND_EPSON_SALT = create("application/vnd.epson.salt", "slt");
	public static final MediaType APPLICATION_VND_EPSON_SSF = create("application/vnd.epson.ssf", "ssf");
	public static final MediaType APPLICATION_VND_ESZIGNO3_XML = create("application/vnd.eszigno3+xml", "es3", "et3");
	public static final MediaType APPLICATION_VND_EZPIX_ALBUM = create("application/vnd.ezpix-album", "ez2");
	public static final MediaType APPLICATION_VND_EZPIX_PACKAGE = create("application/vnd.ezpix-package", "ez3");
	public static final MediaType APPLICATION_VND_FDF = create("application/vnd.fdf", "fdf");
	public static final MediaType APPLICATION_VND_FDSN_MSEED = create("application/vnd.fdsn.mseed", "mseed");
	public static final MediaType APPLICATION_VND_FDSN_SEED = create("application/vnd.fdsn.seed", "seed", "dataless");
	public static final MediaType APPLICATION_VND_FLOGRAPHIT = create("application/vnd.flographit", "gph");
	public static final MediaType APPLICATION_VND_FLUXTIME_CLIP = create("application/vnd.fluxtime.clip", "ftc");
	public static final MediaType APPLICATION_VND_FRAMEMAKER = create("application/vnd.framemaker", "fm", "frame",
		"MAKER", "BOOK");
	public static final MediaType APPLICATION_VND_FROGANS_FNC = create("application/vnd.frogans.fnc", "fnc");
	public static final MediaType APPLICATION_VND_FROGANS_LTF = create("application/vnd.frogans.ltf", "ltf");
	public static final MediaType APPLICATION_VND_FSC_WEBLAUNCH = create("application/vnd.fsc.weblaunch", "fsc");
	public static final MediaType APPLICATION_VND_FUJITSU_OASYS2 = create("application/vnd.fujitsu.oasys2", "oa2");
	public static final MediaType APPLICATION_VND_FUJITSU_OASYS3 = create("application/vnd.fujitsu.oasys3", "oa3");
	public static final MediaType APPLICATION_VND_FUJITSU_OASYSGP = create("application/vnd.fujitsu.oasysgp", "fg5");
	public static final MediaType APPLICATION_VND_FUJITSU_OASYS = create("application/vnd.fujitsu.oasys", "oas");
	public static final MediaType APPLICATION_VND_FUJITSU_OASYSPRS = create("application/vnd.fujitsu.oasysprs", "bh2");
	public static final MediaType APPLICATION_VND_FUJIXEROX_DDD = create("application/vnd.fujixerox.ddd", "ddd");
	public static final MediaType APPLICATION_VND_FUJIXEROX_DOCUWORKS_BINDER = create(
		"APPLICATION/VND.FUJIXEROX.DOCUWORKS.BINDER", "XBD");
	public static final MediaType APPLICATION_VND_FUJIXEROX_DOCUWORKS = create("application/vnd.fujixerox.docuworks",
		"XDW");
	public static final MediaType APPLICATION_VND_FUZZYSHEET = create("application/vnd.fuzzysheet", "fzs");
	public static final MediaType APPLICATION_VND_GENOMATIX_TUXEDO = create("application/vnd.genomatix.tuxedo", "txd");
	public static final MediaType APPLICATION_VND_GEOGEBRA_FILE = create("application/vnd.geogebra.file", "ggb");
	public static final MediaType APPLICATION_VND_GEOGEBRA_TOOL = create("application/vnd.geogebra.tool", "ggt");
	public static final MediaType APPLICATION_VND_GEOMETRY_EXPLORER = create("application/vnd.geometry-explorer",
		"gex", "GRE");
	public static final MediaType APPLICATION_VND_GEONEXT = create("application/vnd.geonext", "gxt");
	public static final MediaType APPLICATION_VND_GEOPLAN = create("application/vnd.geoplan", "g2w");
	public static final MediaType APPLICATION_VND_GEOSPACE = create("application/vnd.geospace", "g3w");
	public static final MediaType APPLICATION_VND_GMX = create("application/vnd.gmx", "gmx");
	public static final MediaType APPLICATION_VND_GOOGLE_EARTH_KML_XML = create("application/vnd.google-earth.kml+xml",
		"KML");
	public static final MediaType APPLICATION_VND_GOOGLE_EARTH_KMZ = create("application/vnd.google-earth.kmz", "kmz");
	public static final MediaType APPLICATION_VND_GRAFEQ = create("application/vnd.grafeq", "gqf", "gqs");
	public static final MediaType APPLICATION_VND_GROOVE_ACCOUNT = create("application/vnd.groove-account", "gac");
	public static final MediaType APPLICATION_VND_GROOVE_HELP = create("application/vnd.groove-help", "ghf");
	public static final MediaType APPLICATION_VND_GROOVE_IDENTITY_MESSAGE = create(
		"APPLICATION/VND.GROOVE-IDENTITY-MESSAGE", "GIM");
	public static final MediaType APPLICATION_VND_GROOVE_INJECTOR = create("application/vnd.groove-injector", "grv");
	public static final MediaType APPLICATION_VND_GROOVE_TOOL_MESSAGE = create("application/vnd.groove-tool-message",
		"GTM");
	public static final MediaType APPLICATION_VND_GROOVE_TOOL_TEMPLATE = create("application/vnd.groove-tool-template",
		"TPL");
	public static final MediaType APPLICATION_VND_GROOVE_VCARD = create("application/vnd.groove-vcard", "vcg");
	public static final MediaType APPLICATION_VND_HAL_XML = create("application/vnd.hal+xml", "hal");
	public static final MediaType APPLICATION_VND_HANDHELD_ENTERTAINMENT_XML = create(
		"APPLICATION/VND.HANDHELD-ENTERTAINMENT+XML", "ZMM");
	public static final MediaType APPLICATION_VND_HBCI = create("application/vnd.hbci", "hbci");
	public static final MediaType APPLICATION_VND_HHE_LESSON_PLAYER = create("application/vnd.hhe.lesson-player", "les");
	public static final MediaType APPLICATION_VND_HP_HPGL = create("application/vnd.hp-hpgl", "hpgl");
	public static final MediaType APPLICATION_VND_HP_HPID = create("application/vnd.hp-hpid", "hpid");
	public static final MediaType APPLICATION_VND_HP_HPS = create("application/vnd.hp-hps", "hps");
	public static final MediaType APPLICATION_VND_HP_JLYT = create("application/vnd.hp-jlyt", "jlt");
	public static final MediaType APPLICATION_VND_HP_PCL = create("application/vnd.hp-pcl", "pcl");
	public static final MediaType APPLICATION_VND_HP_PCLXL = create("application/vnd.hp-pclxl", "pclxl");
	public static final MediaType APPLICATION_VND_HYDROSTATIX_SOF_DATA = create("application/vnd.hydrostatix.sof-data",
		"SFD-HDSTX");
	public static final MediaType APPLICATION_VND_IBM_MINIPAY = create("application/vnd.ibm.minipay", "mpy");
	public static final MediaType APPLICATION_VND_IBM_MODCAP = create("application/vnd.ibm.modcap", "afp", "listafp",
		"LIST3820");
	public static final MediaType APPLICATION_VND_IBM_RIGHTS_MANAGEMENT = create(
		"application/vnd.ibm.rights-management", "IRM");
	public static final MediaType APPLICATION_VND_IBM_SECURE_CONTAINER = create("application/vnd.ibm.secure-container",
		"SC");
	public static final MediaType APPLICATION_VND_ICCPROFILE = create("application/vnd.iccprofile", "icc", "icm");
	public static final MediaType APPLICATION_VND_IGLOADER = create("application/vnd.igloader", "igl");
	public static final MediaType APPLICATION_VND_IMMERVISION_IVP = create("application/vnd.immervision-ivp", "ivp");
	public static final MediaType APPLICATION_VND_IMMERVISION_IVU = create("application/vnd.immervision-ivu", "ivu");
	public static final MediaType APPLICATION_VND_INSORS_IGM = create("application/vnd.insors.igm", "igm");
	public static final MediaType APPLICATION_VND_INTERCON_FORMNET = create("application/vnd.intercon.formnet", "xpw",
		"XPX");
	public static final MediaType APPLICATION_VND_INTERGEO = create("application/vnd.intergeo", "i2g");
	public static final MediaType APPLICATION_VND_INTU_QBO = create("application/vnd.intu.qbo", "qbo");
	public static final MediaType APPLICATION_VND_INTU_QFX = create("application/vnd.intu.qfx", "qfx");
	public static final MediaType APPLICATION_VND_IPUNPLUGGED_RCPROFILE = create(
		"application/vnd.ipunplugged.rcprofile", "RCPROFILE");
	public static final MediaType APPLICATION_VND_IREPOSITORY_PACKAGE_XML = create(
		"APPLICATION/VND.IREPOSITORY.PACKAGE+XML", "IRP");
	public static final MediaType APPLICATION_VND_ISAC_FCS = create("application/vnd.isac.fcs", "fcs");
	public static final MediaType APPLICATION_VND_IS_XPR = create("application/vnd.is-xpr", "xpr");
	public static final MediaType APPLICATION_VND_JAM = create("application/vnd.jam", "jam");
	public static final MediaType APPLICATION_VND_JCP_JAVAME_MIDLET_RMS = create(
		"application/vnd.jcp.javame.midlet-rms", "RMS");
	public static final MediaType APPLICATION_VND_JISP = create("application/vnd.jisp", "jisp");
	public static final MediaType APPLICATION_VND_JOOST_JODA_ARCHIVE = create("application/vnd.joost.joda-archive",
		"joda");
	public static final MediaType APPLICATION_VND_KAHOOTZ = create("application/vnd.kahootz", "ktz", "ktr");
	public static final MediaType APPLICATION_VND_KDE_KARBON = create("application/vnd.kde.karbon", "karbon");
	public static final MediaType APPLICATION_VND_KDE_KCHART = create("application/vnd.kde.kchart", "chrt");
	public static final MediaType APPLICATION_VND_KDE_KFORMULA = create("application/vnd.kde.kformula", "kfo");
	public static final MediaType APPLICATION_VND_KDE_KIVIO = create("application/vnd.kde.kivio", "flw");
	public static final MediaType APPLICATION_VND_KDE_KONTOUR = create("application/vnd.kde.kontour", "kon");
	public static final MediaType APPLICATION_VND_KDE_KPRESENTER = create("application/vnd.kde.kpresenter", "kpr",
		"kpt");
	public static final MediaType APPLICATION_VND_KDE_KSPREAD = create("application/vnd.kde.kspread", "ksp");
	public static final MediaType APPLICATION_VND_KDE_KWORD = create("application/vnd.kde.kword", "kwd", "kwt");
	public static final MediaType APPLICATION_VND_KENAMEAAPP = create("application/vnd.kenameaapp", "htke");
	public static final MediaType APPLICATION_VND_KIDSPIRATION = create("application/vnd.kidspiration", "kia");
	public static final MediaType APPLICATION_VND_KINAR = create("application/vnd.kinar", "kne", "knp");
	public static final MediaType APPLICATION_VND_KOAN = create("application/vnd.koan", "skp", "skd", "skt", "skm");
	public static final MediaType APPLICATION_VND_KODAK_DESCRIPTOR = create("application/vnd.kodak-descriptor", "sse");
	public static final MediaType APPLICATION_VND_LAS_LAS_XML = create("application/vnd.las.las+xml", "lasxml");
	public static final MediaType APPLICATION_VND_LLAMAGRAPHICS_LIFE_BALANCE_DESKTOP = create(
		"APPLICATION/VND.LLAMAGRAPHICS.LIFE-BALANCE.DESKTOP", "LBD");
	public static final MediaType APPLICATION_VND_LLAMAGRAPHICS_LIFE_BALANCE_EXCHANGE_XML = create(
		"APPLICATION/VND.LLAMAGRAPHICS.LIFE-BALANCE.EXCHANGE+XML", "LBE");
	public static final MediaType APPLICATION_VND_LOTUS_1_2_3 = create("application/vnd.lotus-1-2-3", "123");
	public static final MediaType APPLICATION_VND_LOTUS_APPROACH = create("application/vnd.lotus-approach", "apr");
	public static final MediaType APPLICATION_VND_LOTUS_FREELANCE = create("application/vnd.lotus-freelance", "pre");
	public static final MediaType APPLICATION_VND_LOTUS_NOTES = create("application/vnd.lotus-notes", "nsf");
	public static final MediaType APPLICATION_VND_LOTUS_ORGANIZER = create("application/vnd.lotus-organizer", "org");
	public static final MediaType APPLICATION_VND_LOTUS_SCREENCAM = create("application/vnd.lotus-screencam", "scm");
	public static final MediaType APPLICATION_VND_LOTUS_WORDPRO = create("application/vnd.lotus-wordpro", "lwp");
	public static final MediaType APPLICATION_VND_MACPORTS_PORTPKG = create("application/vnd.macports.portpkg",
		"portpkg");
	public static final MediaType APPLICATION_VND_MCD = create("application/vnd.mcd", "mcd");
	public static final MediaType APPLICATION_VND_MEDCALCDATA = create("application/vnd.medcalcdata", "mc1");
	public static final MediaType APPLICATION_VND_MEDIASTATION_CDKEY = create("application/vnd.mediastation.cdkey",
		"CDKEY");
	public static final MediaType APPLICATION_VND_MFER = create("application/vnd.mfer", "mwf");
	public static final MediaType APPLICATION_VND_MFMP = create("application/vnd.mfmp", "mfm");
	public static final MediaType APPLICATION_VND_MICROGRAFX_FLO = create("application/vnd.micrografx.flo", "flo");
	public static final MediaType APPLICATION_VND_MICROGRAFX_IGX = create("application/vnd.micrografx.igx", "igx");
	public static final MediaType APPLICATION_VND_MIF = create("application/vnd.mif", "mif");
	public static final MediaType APPLICATION_VND_MOBIUS_DAF = create("application/vnd.mobius.daf", "daf");
	public static final MediaType APPLICATION_VND_MOBIUS_DIS = create("application/vnd.mobius.dis", "dis");
	public static final MediaType APPLICATION_VND_MOBIUS_MBK = create("application/vnd.mobius.mbk", "mbk");
	public static final MediaType APPLICATION_VND_MOBIUS_MQY = create("application/vnd.mobius.mqy", "mqy");
	public static final MediaType APPLICATION_VND_MOBIUS_MSL = create("application/vnd.mobius.msl", "msl");
	public static final MediaType APPLICATION_VND_MOBIUS_PLC = create("application/vnd.mobius.plc", "plc");
	public static final MediaType APPLICATION_VND_MOBIUS_TXF = create("application/vnd.mobius.txf", "txf");
	public static final MediaType APPLICATION_VND_MOPHUN_APPLICATION = create("application/vnd.mophun.application",
		"mpn");
	public static final MediaType APPLICATION_VND_MOPHUN_CERTIFICATE = create("application/vnd.mophun.certificate",
		"mpc");
	public static final MediaType APPLICATION_VND_MOZILLA_XUL_XML = create("application/vnd.mozilla.xul+xml", "xul");
	public static final MediaType APPLICATION_VND_MS_ARTGALRY = create("application/vnd.ms-artgalry", "cil");
	public static final MediaType APPLICATION_VND_MS_CAB_COMPRESSED = create("application/vnd.ms-cab-compressed", "cab");
	public static final MediaType APPLICATION_VND_MSEQ = create("application/vnd.mseq", "mseq");
	public static final MediaType APPLICATION_VND_MS_EXCEL_ADDIN_MACROENABLED_12 = create(
		"APPLICATION/VND.MS-EXCEL.ADDIN.MACROENABLED.12", "XLAM");
	public static final MediaType APPLICATION_VND_MS_EXCEL_SHEET_BINARY_MACROENABLED_12 = create(
		"APPLICATION/VND.MS-EXCEL.SHEET.BINARY.MACROENABLED.12", "XLSB");
	public static final MediaType APPLICATION_VND_MS_EXCEL_SHEET_MACROENABLED_12 = create(
		"APPLICATION/VND.MS-EXCEL.SHEET.MACROENABLED.12", "XLSM");
	public static final MediaType APPLICATION_VND_MS_EXCEL_TEMPLATE_MACROENABLED_12 = create(
		"APPLICATION/VND.MS-EXCEL.TEMPLATE.MACROENABLED.12", "XLTM");
	public static final MediaType APPLICATION_VND_MS_EXCEL = create("application/vnd.ms-excel", "xls", "xlm", "xla",
		"XLB", "XLC", "XLT", "XLW");
	public static final MediaType APPLICATION_VND_MS_FONTOBJECT = create("application/vnd.ms-fontobject", "eot");
	public static final MediaType APPLICATION_VND_MS_HTMLHELP = create("application/vnd.ms-htmlhelp", "chm");
	public static final MediaType APPLICATION_VND_MS_IMS = create("application/vnd.ms-ims", "ims");
	public static final MediaType APPLICATION_VND_MS_LRM = create("application/vnd.ms-lrm", "lrm");
	public static final MediaType APPLICATION_VND_MS_OFFICETHEME = create("application/vnd.ms-officetheme", "thmx");
	public static final MediaType APPLICATION_VND_MS_PKI_SECCAT = create("application/vnd.ms-pki.seccat", "cat");
	public static final MediaType APPLICATION_VND_MS_PKI_STL = create("application/vnd.ms-pki.stl", "stl");
	public static final MediaType APPLICATION_VND_MS_POWERPOINT_ADDIN_MACROENABLED_12 = create(
		"APPLICATION/VND.MS-POWERPOINT.ADDIN.MACROENABLED.12", "PPAM");
	public static final MediaType APPLICATION_VND_MS_POWERPOINT = create("application/vnd.ms-powerpoint", "ppt", "pps",
		"POT");
	public static final MediaType APPLICATION_VND_MS_POWERPOINT_PRESENTATION_MACROENABLED_12 = create(
		"APPLICATION/VND.MS-POWERPOINT.PRESENTATION.MACROENABLED.12", "PPTM");
	public static final MediaType APPLICATION_VND_MS_POWERPOINT_SLIDE_MACROENABLED_12 = create(
		"APPLICATION/VND.MS-POWERPOINT.SLIDE.MACROENABLED.12", "SLDM");
	public static final MediaType APPLICATION_VND_MS_POWERPOINT_SLIDESHOW_MACROENABLED_12 = create(
		"APPLICATION/VND.MS-POWERPOINT.SLIDESHOW.MACROENABLED.12", "PPSM");
	public static final MediaType APPLICATION_VND_MS_POWERPOINT_TEMPLATE_MACROENABLED_12 = create(
		"APPLICATION/VND.MS-POWERPOINT.TEMPLATE.MACROENABLED.12", "POTM");
	public static final MediaType APPLICATION_VND_MS_PROJECT = create("application/vnd.ms-project", "mpp", "mpt");
	public static final MediaType APPLICATION_VND_MS_WORD_DOCUMENT_MACROENABLED_12 = create(
		"APPLICATION/VND.MS-WORD.DOCUMENT.MACROENABLED.12", "DOCM");
	public static final MediaType APPLICATION_VND_MS_WORD_TEMPLATE_MACROENABLED_12 = create(
		"APPLICATION/VND.MS-WORD.TEMPLATE.MACROENABLED.12", "DOTM");
	public static final MediaType APPLICATION_VND_MS_WORKS = create("application/vnd.ms-works", "wps", "wks", "wcm",
		"wdb");
	public static final MediaType APPLICATION_VND_MS_WPL = create("application/vnd.ms-wpl", "wpl");
	public static final MediaType APPLICATION_VND_MS_XPSDOCUMENT = create("application/vnd.ms-xpsdocument", "xps");
	public static final MediaType APPLICATION_VND_MUSICIAN = create("application/vnd.musician", "mus");
	public static final MediaType APPLICATION_VND_MUVEE_STYLE = create("application/vnd.muvee.style", "msty");
	public static final MediaType APPLICATION_VND_MYNFC = create("application/vnd.mynfc", "taglet");
	public static final MediaType APPLICATION_VND_NEUROLANGUAGE_NLU = create("application/vnd.neurolanguage.nlu", "nlu");
	public static final MediaType APPLICATION_VND_NITF = create("application/vnd.nitf", "ntf", "nitf");
	public static final MediaType APPLICATION_VND_NOBLENET_DIRECTORY = create("application/vnd.noblenet-directory",
		"nnd");
	public static final MediaType APPLICATION_VND_NOBLENET_SEALER = create("application/vnd.noblenet-sealer", "nns");
	public static final MediaType APPLICATION_VND_NOBLENET_WEB = create("application/vnd.noblenet-web", "nnw");
	public static final MediaType APPLICATION_VND_NOKIA_N_GAGE_DATA = create("application/vnd.nokia.n-gage.data",
		"ngdat");
	public static final MediaType APPLICATION_VND_NOKIA_N_GAGE_SYMBIAN_INSTALL = create(
		"APPLICATION/VND.NOKIA.N-GAGE.SYMBIAN.INSTALL", "N-GAGE");
	public static final MediaType APPLICATION_VND_NOKIA_RADIO_PRESET = create("application/vnd.nokia.radio-preset",
		"rpst");
	public static final MediaType APPLICATION_VND_NOKIA_RADIO_PRESETS = create("application/vnd.nokia.radio-presets",
		"RPSS");
	public static final MediaType APPLICATION_VND_NOVADIGM_EDM = create("application/vnd.novadigm.edm", "edm");
	public static final MediaType APPLICATION_VND_NOVADIGM_EDX = create("application/vnd.novadigm.edx", "edx");
	public static final MediaType APPLICATION_VND_NOVADIGM_EXT = create("application/vnd.novadigm.ext", "ext");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_CHART = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.CHART", "ODC");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_CHART_TEMPLATE = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.CHART-TEMPLATE", "OTC");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_DATABASE = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.DATABASE", "ODB");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_FORMULA = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.FORMULA", "ODF");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_FORMULA_TEMPLATE = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.FORMULA-TEMPLATE", "ODFT");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_GRAPHICS = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.GRAPHICS", "ODG");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_GRAPHICS_TEMPLATE = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.GRAPHICS-TEMPLATE", "OTG");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_IMAGE = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.IMAGE", "ODI");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_IMAGE_TEMPLATE = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.IMAGE-TEMPLATE", "OTI");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_PRESENTATION = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.PRESENTATION", "ODP");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_PRESENTATION_TEMPLATE = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.PRESENTATION-TEMPLATE", "OTP");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_SPREADSHEET = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.SPREADSHEET", "ODS");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_SPREADSHEET_TEMPLATE = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.SPREADSHEET-TEMPLATE", "OTS");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_TEXT_MASTER = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.TEXT-MASTER", "ODM");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_TEXT = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.TEXT", "ODT");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_TEXT_TEMPLATE = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.TEXT-TEMPLATE", "OTT");
	public static final MediaType APPLICATION_VND_OASIS_OPENDOCUMENT_TEXT_WEB = create(
		"APPLICATION/VND.OASIS.OPENDOCUMENT.TEXT-WEB", "OTH");
	public static final MediaType APPLICATION_VND_OLPC_SUGAR = create("application/vnd.olpc-sugar", "xo");
	public static final MediaType APPLICATION_VND_OMA_DD2_XML = create("application/vnd.oma.dd2+xml", "dd2");
	public static final MediaType APPLICATION_VND_OPENOFFICEORG_EXTENSION = create(
		"APPLICATION/VND.OPENOFFICEORG.EXTENSION", "OXT");
	public static final MediaType APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_PRESENTATIONML_PRESENTATION = create(
		"APPLICATION/VND.OPENXMLFORMATS-OFFICEDOCUMENT.PRESENTATIONML.PRESENTATION", "PPTX");
	public static final MediaType APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_PRESENTATIONML_SLIDESHOW = create(
		"APPLICATION/VND.OPENXMLFORMATS-OFFICEDOCUMENT.PRESENTATIONML.SLIDESHOW", "PPSX");
	public static final MediaType APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_PRESENTATIONML_SLIDE = create(
		"APPLICATION/VND.OPENXMLFORMATS-OFFICEDOCUMENT.PRESENTATIONML.SLIDE", "SLDX");
	public static final MediaType APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_PRESENTATIONML_TEMPLATE = create(
		"APPLICATION/VND.OPENXMLFORMATS-OFFICEDOCUMENT.PRESENTATIONML.TEMPLATE", "POTX");
	public static final MediaType APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SPREADSHEETML_SHEET = create(
		"APPLICATION/VND.OPENXMLFORMATS-OFFICEDOCUMENT.SPREADSHEETML.SHEET", "XLSX");
	public static final MediaType APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SPREADSHEETML_TEMPLATE = create(
		"APPLICATION/VND.OPENXMLFORMATS-OFFICEDOCUMENT.SPREADSHEETML.TEMPLATE", "XLTX");
	public static final MediaType APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_WORDPROCESSINGML_DOCUMENT = create(
		"APPLICATION/VND.OPENXMLFORMATS-OFFICEDOCUMENT.WORDPROCESSINGML.DOCUMENT", "DOCX");
	public static final MediaType APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_WORDPROCESSINGML_TEMPLATE = create(
		"APPLICATION/VND.OPENXMLFORMATS-OFFICEDOCUMENT.WORDPROCESSINGML.TEMPLATE", "DOTX");
	public static final MediaType APPLICATION_VND_OSGEO_MAPGUIDE_PACKAGE = create(
		"APPLICATION/VND.OSGEO.MAPGUIDE.PACKAGE", "MGP");
	public static final MediaType APPLICATION_VND_OSGI_DP = create("application/vnd.osgi.dp", "dp");
	public static final MediaType APPLICATION_VND_OSGI_SUBSYSTEM = create("application/vnd.osgi.subsystem", "esa");
	public static final MediaType APPLICATION_VND_PALM = create("application/vnd.palm", "pdb", "pqa", "oprc");
	public static final MediaType APPLICATION_VND_PAWAAFILE = create("application/vnd.pawaafile", "paw");
	public static final MediaType APPLICATION_VND_PG_FORMAT = create("application/vnd.pg.format", "str");
	public static final MediaType APPLICATION_VND_PG_OSASLI = create("application/vnd.pg.osasli", "ei6");
	public static final MediaType APPLICATION_VND_PICSEL = create("application/vnd.picsel", "efif");
	public static final MediaType APPLICATION_VND_PMI_WIDGET = create("application/vnd.pmi.widget", "wg");
	public static final MediaType APPLICATION_VND_POCKETLEARN = create("application/vnd.pocketlearn", "plf");
	public static final MediaType APPLICATION_VND_POWERBUILDER6 = create("application/vnd.powerbuilder6", "pbd");
	public static final MediaType APPLICATION_VND_PREVIEWSYSTEMS_BOX = create("application/vnd.previewsystems.box",
		"box");
	public static final MediaType APPLICATION_VND_PROTEUS_MAGAZINE = create("application/vnd.proteus.magazine", "mgz");
	public static final MediaType APPLICATION_VND_PUBLISHARE_DELTA_TREE = create(
		"application/vnd.publishare-delta-tree", "QPS");
	public static final MediaType APPLICATION_VND_PVI_PTID1 = create("application/vnd.pvi.ptid1", "ptid");
	public static final MediaType APPLICATION_VND_QUARK_QUARKXPRESS = create("application/vnd.quark.quarkxpress",
		"qxd", "QXT", "QWD", "QWT", "QXL", "QXB");
	public static final MediaType APPLICATION_VND_REALVNC_BED = create("application/vnd.realvnc.bed", "bed");
	public static final MediaType APPLICATION_VND_RECORDARE_MUSICXML = create("application/vnd.recordare.musicxml",
		"mxl");
	public static final MediaType APPLICATION_VND_RECORDARE_MUSICXML_XML = create(
		"APPLICATION/VND.RECORDARE.MUSICXML+XML", "MUSICXML");
	public static final MediaType APPLICATION_VND_RIG_CRYPTONOTE = create("application/vnd.rig.cryptonote",
		"cryptonote");
	public static final MediaType APPLICATION_VND_RIM_COD = create("application/vnd.rim.cod", "cod");
	public static final MediaType APPLICATION_VND_RN_REALMEDIA = create("application/vnd.rn-realmedia", "rm");
	public static final MediaType APPLICATION_VND_RN_REALMEDIA_VBR = create("application/vnd.rn-realmedia-vbr", "rmvb");
	public static final MediaType APPLICATION_VND_ROUTE66_LINK66_XML = create("application/vnd.route66.link66+xml",
		"LINK66");
	public static final MediaType APPLICATION_VND_SAILINGTRACKER_TRACK = create("application/vnd.sailingtracker.track",
		"ST");
	public static final MediaType APPLICATION_VND_SEEMAIL = create("application/vnd.seemail", "see");
	public static final MediaType APPLICATION_VND_SEMA = create("application/vnd.sema", "sema");
	public static final MediaType APPLICATION_VND_SEMD = create("application/vnd.semd", "semd");
	public static final MediaType APPLICATION_VND_SEMF = create("application/vnd.semf", "semf");
	public static final MediaType APPLICATION_VND_SHANA_INFORMED_FORMDATA = create(
		"APPLICATION/VND.SHANA.INFORMED.FORMDATA", "IFM");
	public static final MediaType APPLICATION_VND_SHANA_INFORMED_FORMTEMPLATE = create(
		"APPLICATION/VND.SHANA.INFORMED.FORMTEMPLATE", "ITP");
	public static final MediaType APPLICATION_VND_SHANA_INFORMED_INTERCHANGE = create(
		"APPLICATION/VND.SHANA.INFORMED.INTERCHANGE", "IIF");
	public static final MediaType APPLICATION_VND_SHANA_INFORMED_PACKAGE = create(
		"APPLICATION/VND.SHANA.INFORMED.PACKAGE", "IPK");
	public static final MediaType APPLICATION_VND_SIMTECH_MINDMAPPER = create("application/vnd.simtech-mindmapper",
		"twd", "TWDS");
	public static final MediaType APPLICATION_VND_SMAF = create("application/vnd.smaf", "mmf");
	public static final MediaType APPLICATION_VND_SMART_TEACHER = create("application/vnd.smart.teacher", "teacher");
	public static final MediaType APPLICATION_VND_SOLENT_SDKM_XML = create("application/vnd.solent.sdkm+xml", "sdkm",
		"SDKD");
	public static final MediaType APPLICATION_VND_SPOTFIRE_DXP = create("application/vnd.spotfire.dxp", "dxp");
	public static final MediaType APPLICATION_VND_SPOTFIRE_SFS = create("application/vnd.spotfire.sfs", "sfs");
	public static final MediaType APPLICATION_VND_STARDIVISION_CALC = create("application/vnd.stardivision.calc", "sdc");
	public static final MediaType APPLICATION_VND_STARDIVISION_CHART = create("application/vnd.stardivision.chart",
		"sds");
	public static final MediaType APPLICATION_VND_STARDIVISION_DRAW = create("application/vnd.stardivision.draw", "sda");
	public static final MediaType APPLICATION_VND_STARDIVISION_IMPRESS = create("application/vnd.stardivision.impress",
		"SDD");
	public static final MediaType APPLICATION_VND_STARDIVISION_MATH = create("application/vnd.stardivision.math",
		"smf", "SDF");
	public static final MediaType APPLICATION_VND_STARDIVISION_WRITER_GLOBAL = create(
		"APPLICATION/VND.STARDIVISION.WRITER-GLOBAL", "SGL");
	public static final MediaType APPLICATION_VND_STARDIVISION_WRITER = create("application/vnd.stardivision.writer",
		"SDW", "VOR");
	public static final MediaType APPLICATION_VND_STEPMANIA_PACKAGE = create("application/vnd.stepmania.package",
		"smzip");
	public static final MediaType APPLICATION_VND_STEPMANIA_STEPCHART = create("application/vnd.stepmania.stepchart",
		"sm");
	public static final MediaType APPLICATION_VND_SUN_XML_CALC = create("application/vnd.sun.xml.calc", "sxc");
	public static final MediaType APPLICATION_VND_SUN_XML_CALC_TEMPLATE = create(
		"application/vnd.sun.xml.calc.template", "STC");
	public static final MediaType APPLICATION_VND_SUN_XML_DRAW = create("application/vnd.sun.xml.draw", "sxd");
	public static final MediaType APPLICATION_VND_SUN_XML_DRAW_TEMPLATE = create(
		"application/vnd.sun.xml.draw.template", "STD");
	public static final MediaType APPLICATION_VND_SUN_XML_IMPRESS = create("application/vnd.sun.xml.impress", "sxi");
	public static final MediaType APPLICATION_VND_SUN_XML_IMPRESS_TEMPLATE = create(
		"APPLICATION/VND.SUN.XML.IMPRESS.TEMPLATE", "STI");
	public static final MediaType APPLICATION_VND_SUN_XML_MATH = create("application/vnd.sun.xml.math", "sxm");
	public static final MediaType APPLICATION_VND_SUN_XML_WRITER_GLOBAL = create(
		"application/vnd.sun.xml.writer.global", "SXG");
	public static final MediaType APPLICATION_VND_SUN_XML_WRITER = create("application/vnd.sun.xml.writer", "sxw");
	public static final MediaType APPLICATION_VND_SUN_XML_WRITER_TEMPLATE = create(
		"APPLICATION/VND.SUN.XML.WRITER.TEMPLATE", "STW");
	public static final MediaType APPLICATION_VND_SUS_CALENDAR = create("application/vnd.sus-calendar", "sus", "susp");
	public static final MediaType APPLICATION_VND_SVD = create("application/vnd.svd", "svd");
	public static final MediaType APPLICATION_VND_SYMBIAN_INSTALL = create("application/vnd.symbian.install", "sis",
		"SISX");
	public static final MediaType APPLICATION_VND_SYNCML_DM_WBXML = create("application/vnd.syncml.dm+wbxml", "bdm");
	public static final MediaType APPLICATION_VND_SYNCML_DM_XML = create("application/vnd.syncml.dm+xml", "xdm");
	public static final MediaType APPLICATION_VND_SYNCML_XML = create("application/vnd.syncml+xml", "xsm");
	public static final MediaType APPLICATION_VND_TAO_INTENT_MODULE_ARCHIVE = create(
		"APPLICATION/VND.TAO.INTENT-MODULE-ARCHIVE", "TAO");
	public static final MediaType APPLICATION_VND_TCPDUMP_PCAP = create("application/vnd.tcpdump.pcap", "pcap", "cap",
		"DMP");
	public static final MediaType APPLICATION_VND_TMOBILE_LIVETV = create("application/vnd.tmobile-livetv", "tmo");
	public static final MediaType APPLICATION_VND_TRID_TPT = create("application/vnd.trid.tpt", "tpt");
	public static final MediaType APPLICATION_VND_TRISCAPE_MXS = create("application/vnd.triscape.mxs", "mxs");
	public static final MediaType APPLICATION_VND_TRUEAPP = create("application/vnd.trueapp", "tra");
	public static final MediaType APPLICATION_VND_UFDL = create("application/vnd.ufdl", "ufd", "ufdl");
	public static final MediaType APPLICATION_VND_UIQ_THEME = create("application/vnd.uiq.theme", "utz");
	public static final MediaType APPLICATION_VND_UMAJIN = create("application/vnd.umajin", "umj");
	public static final MediaType APPLICATION_VND_UNITY = create("application/vnd.unity", "unityweb");
	public static final MediaType APPLICATION_VND_UOML_XML = create("application/vnd.uoml+xml", "uoml");
	public static final MediaType APPLICATION_VND_VCX = create("application/vnd.vcx", "vcx");
	public static final MediaType APPLICATION_VND_VISIONARY = create("application/vnd.visionary", "vis");
	public static final MediaType APPLICATION_VND_VISIO = create("application/vnd.visio", "vsd", "vst", "vss", "vsw");
	public static final MediaType APPLICATION_VND_VSF = create("application/vnd.vsf", "vsf");
	public static final MediaType APPLICATION_VND_WAP_WBXML = create("application/vnd.wap.wbxml", "wbxml");
	public static final MediaType APPLICATION_VND_WAP_WMLC = create("application/vnd.wap.wmlc", "wmlc");
	public static final MediaType APPLICATION_VND_WAP_WMLSCRIPTC = create("application/vnd.wap.wmlscriptc", "wmlsc");
	public static final MediaType APPLICATION_VND_WEBTURBO = create("application/vnd.webturbo", "wtb");
	public static final MediaType APPLICATION_VND_WOLFRAM_PLAYER = create("application/vnd.wolfram.player", "nbp");
	public static final MediaType APPLICATION_VND_WORDPERFECT5_1 = create("application/vnd.wordperfect5.1", "wp5");
	public static final MediaType APPLICATION_VND_WORDPERFECT = create("application/vnd.wordperfect", "wpd");
	public static final MediaType APPLICATION_VND_WQD = create("application/vnd.wqd", "wqd");
	public static final MediaType APPLICATION_VND_WT_STF = create("application/vnd.wt.stf", "stf");
	public static final MediaType APPLICATION_VND_XARA = create("application/vnd.xara", "xar");
	public static final MediaType APPLICATION_VND_XFDL = create("application/vnd.xfdl", "xfdl");
	public static final MediaType APPLICATION_VND_YAMAHA_HV_DIC = create("application/vnd.yamaha.hv-dic", "hvd");
	public static final MediaType APPLICATION_VND_YAMAHA_HV_SCRIPT = create("application/vnd.yamaha.hv-script", "hvs");
	public static final MediaType APPLICATION_VND_YAMAHA_HV_VOICE = create("application/vnd.yamaha.hv-voice", "hvp");
	public static final MediaType APPLICATION_VND_YAMAHA_OPENSCOREFORMAT = create(
		"APPLICATION/VND.YAMAHA.OPENSCOREFORMAT", "OSF");
	public static final MediaType APPLICATION_VND_YAMAHA_OPENSCOREFORMAT_OSFPVG_XML = create(
		"APPLICATION/VND.YAMAHA.OPENSCOREFORMAT.OSFPVG+XML", "OSFPVG");
	public static final MediaType APPLICATION_VND_YAMAHA_SMAF_AUDIO = create("application/vnd.yamaha.smaf-audio", "saf");
	public static final MediaType APPLICATION_VND_YAMAHA_SMAF_PHRASE = create("application/vnd.yamaha.smaf-phrase",
		"spf");
	public static final MediaType APPLICATION_VND_YELLOWRIVER_CUSTOM_MENU = create(
		"APPLICATION/VND.YELLOWRIVER-CUSTOM-MENU", "CMP");
	public static final MediaType APPLICATION_VND_ZUL = create("application/vnd.zul", "zir", "zirz");
	public static final MediaType APPLICATION_VND_ZZAZZ_DECK_XML = create("application/vnd.zzazz.deck+xml", "zaz");
	public static final MediaType APPLICATION_VOICEXML_XML = create("application/voicexml+xml", "vxml");
	public static final MediaType APPLICATION_WIDGET = create("application/widget", "wgt");
	public static final MediaType APPLICATION_WINHLP = create("application/winhlp", "hlp");
	public static final MediaType APPLICATION_WSDL_XML = create("application/wsdl+xml", "wsdl");
	public static final MediaType APPLICATION_WSPOLICY_XML = create("application/wspolicy+xml", "wspolicy");
	public static final MediaType APPLICATION_X_123 = create("application/x-123", "wk");
	public static final MediaType APPLICATION_X_7Z_COMPRESSED = create("application/x-7z-compressed", "7z");
	public static final MediaType APPLICATION_X_ABIWORD = create("application/x-abiword", "abw");
	public static final MediaType APPLICATION_X_ACE_COMPRESSED = create("application/x-ace-compressed", "ace");
	public static final MediaType APPLICATION_XAML_XML = create("application/xaml+xml", "xaml");
	public static final MediaType APPLICATION_X_APPLE_DISKIMAGE = create("application/x-apple-diskimage", "dmg");
	public static final MediaType APPLICATION_X_AUTHORWARE_BIN = create("application/x-authorware-bin", "aab", "x32",
		"U32", "VOX");
	public static final MediaType APPLICATION_X_AUTHORWARE_MAP = create("application/x-authorware-map", "aam");
	public static final MediaType APPLICATION_X_AUTHORWARE_SEG = create("application/x-authorware-seg", "aas");
	public static final MediaType APPLICATION_X_BCPIO = create("application/x-bcpio", "bcpio");
	public static final MediaType APPLICATION_X_BITTORRENT = create("application/x-bittorrent", "torrent");
	public static final MediaType APPLICATION_X_BLORB = create("application/x-blorb", "blb", "blorb");
	public static final MediaType APPLICATION_X_BZIP2 = create("application/x-bzip2", "bz2", "boz");
	public static final MediaType APPLICATION_X_BZIP = create("application/x-bzip", "bz");
	public static final MediaType APPLICATION_X_CAB = create("application/x-cab", "cab");
	public static final MediaType APPLICATION_XCAP_DIFF_XML = create("application/xcap-diff+xml", "xdf");
	public static final MediaType APPLICATION_X_CBR = create("application/x-cbr", "cbr", "cba", "cbt", "cbz", "cb7");
	public static final MediaType APPLICATION_X_CBZ = create("application/x-cbz", "cbz");
	public static final MediaType APPLICATION_X_CDF = create("application/x-cdf", "cdf", "cda");
	public static final MediaType APPLICATION_X_CDLINK = create("application/x-cdlink", "vcd");
	public static final MediaType APPLICATION_X_CFS_COMPRESSED = create("application/x-cfs-compressed", "cfs");
	public static final MediaType APPLICATION_X_CHAT = create("application/x-chat", "chat");
	public static final MediaType APPLICATION_X_CHESS_PGN = create("application/x-chess-pgn", "pgn");
	public static final MediaType APPLICATION_X_COMSOL = create("application/x-comsol", "mph");
	public static final MediaType APPLICATION_X_CONFERENCE = create("application/x-conference", "nsc");
	public static final MediaType APPLICATION_X_CPIO = create("application/x-cpio", "cpio");
	public static final MediaType APPLICATION_X_CSH = create("application/x-csh", "csh");
	public static final MediaType APPLICATION_X_DEBIAN_PACKAGE = create("application/x-debian-package", "deb", "udeb");
	public static final MediaType APPLICATION_X_DGC_COMPRESSED = create("application/x-dgc-compressed", "dgc");
	public static final MediaType APPLICATION_X_DIRECTOR = create("application/x-director", "dir", "dcr", "dxr", "cst",
		"CCT", "CXT", "W3D", "FGD", "SWA");
	public static final MediaType APPLICATION_X_DMS = create("application/x-dms", "dms");
	public static final MediaType APPLICATION_X_DOOM = create("application/x-doom", "wad");
	public static final MediaType APPLICATION_X_DTBNCX_XML = create("application/x-dtbncx+xml", "ncx");
	public static final MediaType APPLICATION_X_DTBOOK_XML = create("application/x-dtbook+xml", "dtb");
	public static final MediaType APPLICATION_X_DTBRESOURCE_XML = create("application/x-dtbresource+xml", "res");
	public static final MediaType APPLICATION_X_DVI = create("application/x-dvi", "dvi");
	public static final MediaType APPLICATION_XENC_XML = create("application/xenc+xml", "xenc");
	public static final MediaType APPLICATION_X_ENVOY = create("application/x-envoy", "evy");
	public static final MediaType APPLICATION_X_EVA = create("application/x-eva", "eva");
	public static final MediaType APPLICATION_X_FONT_BDF = create("application/x-font-bdf", "bdf");
	public static final MediaType APPLICATION_X_FONT_GHOSTSCRIPT = create("application/x-font-ghostscript", "gsf");
	public static final MediaType APPLICATION_X_FONT_LINUX_PSF = create("application/x-font-linux-psf", "psf");
	public static final MediaType APPLICATION_X_FONT_OTF = create("application/x-font-otf", "otf");
	public static final MediaType APPLICATION_X_FONT_PCF = create("application/x-font-pcf", "pcf");
	public static final MediaType APPLICATION_X_FONT = create("application/x-font", "pfa", "pfb", "gsf", "pcf", "pcf.z");
	public static final MediaType APPLICATION_X_FONT_SNF = create("application/x-font-snf", "snf");
	public static final MediaType APPLICATION_X_FONT_TTF = create("application/x-font-ttf", "ttf", "ttc");
	public static final MediaType APPLICATION_X_FONT_TYPE1 = create("application/x-font-type1", "pfa", "pfb", "pfm",
		"afm");
	public static final MediaType APPLICATION_X_FONT_WOFF = create("application/x-font-woff", "woff", "woff2");
	public static final MediaType APPLICATION_X_FREEARC = create("application/x-freearc", "arc");
	public static final MediaType APPLICATION_X_FREEMIND = create("application/x-freemind", "mm");
	public static final MediaType APPLICATION_X_FUTURESPLASH = create("application/x-futuresplash", "spl");
	public static final MediaType APPLICATION_X_GANTTPROJECT = create("application/x-ganttproject", "gan");
	public static final MediaType APPLICATION_X_GCA_COMPRESSED = create("application/x-gca-compressed", "gca");
	public static final MediaType APPLICATION_X_GLULX = create("application/x-glulx", "ulx");
	public static final MediaType APPLICATION_X_GNUMERIC = create("application/x-gnumeric", "gnumeric");
	public static final MediaType APPLICATION_X_GO_SGF = create("application/x-go-sgf", "sgf");
	public static final MediaType APPLICATION_X_GRAMPS_XML = create("application/x-gramps-xml", "gramps");
	public static final MediaType APPLICATION_X_GRAPHING_CALCULATOR = create("application/x-graphing-calculator", "gcf");
	public static final MediaType APPLICATION_X_GTAR_COMPRESSED = create("application/x-gtar-compressed", "tgz", "taz");
	public static final MediaType APPLICATION_X_GTAR = create("application/x-gtar", "gtar");
	public static final MediaType APPLICATION_X_HDF = create("application/x-hdf", "hdf");
	public static final MediaType APPLICATION_XHTML_XML_UTF8 = createUTF8("application/xhtml+xml", "xhtml", "xht");
	public static final MediaType APPLICATION_X_HTTPD_ERUBY = create("application/x-httpd-eruby", "rhtml");
	public static final MediaType APPLICATION_X_HTTPD_PHP3 = create("application/x-httpd-php3", "php3");
	public static final MediaType APPLICATION_X_HTTPD_PHP3_PREPROCESSED = create(
		"application/x-httpd-php3-preprocessed", "PHP3P");
	public static final MediaType APPLICATION_X_HTTPD_PHP4 = create("application/x-httpd-php4", "php4");
	public static final MediaType APPLICATION_X_HTTPD_PHP5 = create("application/x-httpd-php5", "php5");
	public static final MediaType APPLICATION_X_HTTPD_PHP = create("application/x-httpd-php", "phtml", "pht", "php");
	public static final MediaType APPLICATION_X_HTTPD_PHP_SOURCE = create("application/x-httpd-php-source", "phps");
	public static final MediaType APPLICATION_X_ICA = create("application/x-ica", "ica");
	public static final MediaType APPLICATION_X_INFO = create("application/x-info", "info");
	public static final MediaType APPLICATION_X_INSTALL_INSTRUCTIONS = create("application/x-install-instructions",
		"INSTALL");
	public static final MediaType APPLICATION_X_INTERNET_SIGNUP = create("application/x-internet-signup", "ins", "isp");
	public static final MediaType APPLICATION_X_IPHONE = create("application/x-iphone", "iii");
	public static final MediaType APPLICATION_X_ISO9660_IMAGE = create("application/x-iso9660-image", "iso");
	public static final MediaType APPLICATION_X_JAM = create("application/x-jam", "jam");
	public static final MediaType APPLICATION_X_JAVA_JNLP_FILE = create("application/x-java-jnlp-file", "jnlp");
	public static final MediaType APPLICATION_X_JMOL = create("application/x-jmol", "jmz");
	public static final MediaType APPLICATION_X_KCHART = create("application/x-kchart", "chrt");
	public static final MediaType APPLICATION_X_KILLUSTRATOR = create("application/x-killustrator", "kil");
	public static final MediaType APPLICATION_X_KOAN = create("application/x-koan", "skp", "skd", "skt", "skm");
	public static final MediaType APPLICATION_X_KPRESENTER = create("application/x-kpresenter", "kpr", "kpt");
	public static final MediaType APPLICATION_X_KSPREAD = create("application/x-kspread", "ksp");
	public static final MediaType APPLICATION_X_KWORD = create("application/x-kword", "kwd", "kwt");
	public static final MediaType APPLICATION_X_LATEX = create("application/x-latex", "latex");
	public static final MediaType APPLICATION_X_LHA = create("application/x-lha", "lha");
	public static final MediaType APPLICATION_X_LYX = create("application/x-lyx", "lyx");
	public static final MediaType APPLICATION_X_LZH_COMPRESSED = create("application/x-lzh-compressed", "lzh", "lha");
	public static final MediaType APPLICATION_X_LZH = create("application/x-lzh", "lzh");
	public static final MediaType APPLICATION_X_LZX = create("application/x-lzx", "lzx");
	public static final MediaType APPLICATION_X_MAKER = create("application/x-maker", "frm", "maker", "frame", "fm",
		"fb", "BOOK", "FBDOC");
	public static final MediaType APPLICATION_X_MIE = create("application/x-mie", "mie");
	public static final MediaType APPLICATION_X_MIF = create("application/x-mif", "mif");
	public static final MediaType APPLICATION_XML_DTD_UTF8 = createUTF8("application/xml-dtd", "dtd");
	public static final MediaType APPLICATION_XML_UTF8 = createUTF8("application/xml", "xml", "xsl", "xsd");
	public static final MediaType APPLICATION_X_MOBIPOCKET_EBOOK = create("application/x-mobipocket-ebook", "prc",
		"mobi");
	public static final MediaType APPLICATION_X_MPEGURL = create("application/x-mpegurl", "m3u8");
	public static final MediaType APPLICATION_X_MSACCESS = create("application/x-msaccess", "mdb");
	public static final MediaType APPLICATION_X_MS_APPLICATION = create("application/x-ms-application", "application");
	public static final MediaType APPLICATION_X_MSBINDER = create("application/x-msbinder", "obd");
	public static final MediaType APPLICATION_X_MSCARDFILE = create("application/x-mscardfile", "crd");
	public static final MediaType APPLICATION_X_MSCLIP = create("application/x-msclip", "clp");
	public static final MediaType APPLICATION_X_MSDOS_PROGRAM = create("application/x-msdos-program", "com", "exe",
		"bat", "DLL");
	public static final MediaType APPLICATION_X_MSDOWNLOAD = create("application/x-msdownload", "exe", "dll", "com",
		"BAT", "MSI");
	public static final MediaType APPLICATION_X_MSI = create("application/x-msi", "msi");
	public static final MediaType APPLICATION_X_MSMEDIAVIEW = create("application/x-msmediaview", "mvb", "m13", "m14");
	public static final MediaType APPLICATION_X_MSMETAFILE = create("application/x-msmetafile", "wmf", "wmz", "emf",
		"emz");
	public static final MediaType APPLICATION_X_MSMONEY = create("application/x-msmoney", "mny");
	public static final MediaType APPLICATION_X_MSPUBLISHER = create("application/x-mspublisher", "pub");
	public static final MediaType APPLICATION_X_MSSCHEDULE = create("application/x-msschedule", "scd");
	public static final MediaType APPLICATION_X_MS_SHORTCUT = create("application/x-ms-shortcut", "lnk");
	public static final MediaType APPLICATION_X_MSTERMINAL = create("application/x-msterminal", "trm");
	public static final MediaType APPLICATION_X_MS_WMD = create("application/x-ms-wmd", "wmd");
	public static final MediaType APPLICATION_X_MS_WMZ = create("application/x-ms-wmz", "wmz");
	public static final MediaType APPLICATION_X_MSWRITE = create("application/x-mswrite", "wri");
	public static final MediaType APPLICATION_X_MS_XBAP = create("application/x-ms-xbap", "xbap");
	public static final MediaType APPLICATION_X_NETCDF = create("application/x-netcdf", "nc", "cdf");
	public static final MediaType APPLICATION_X_NS_PROXY_AUTOCONFIG = create("application/x-ns-proxy-autoconfig",
		"pac", "DAT");
	public static final MediaType APPLICATION_X_NWC = create("application/x-nwc", "nwc");
	public static final MediaType APPLICATION_X_NZB = create("application/x-nzb", "nzb");
	public static final MediaType APPLICATION_X_OBJECT = create("application/x-object", "o");
	public static final MediaType APPLICATION_XOP_XML = create("application/xop+xml", "xop");
	public static final MediaType APPLICATION_X_OZ_APPLICATION = create("application/x-oz-application", "oza");
	public static final MediaType APPLICATION_X_PKCS12 = create("application/x-pkcs12", "p12", "pfx");
	public static final MediaType APPLICATION_X_PKCS7_CERTIFICATES = create("application/x-pkcs7-certificates", "p7b",
		"SPC");
	public static final MediaType APPLICATION_X_PKCS7_CERTREQRESP = create("application/x-pkcs7-certreqresp", "p7r");
	public static final MediaType APPLICATION_X_PKCS7_CRL = create("application/x-pkcs7-crl", "crl");
	public static final MediaType APPLICATION_XPROC_XML = create("application/xproc+xml", "xpl");
	public static final MediaType APPLICATION_X_PYTHON_CODE = create("application/x-python-code", "pyc", "pyo");
	public static final MediaType APPLICATION_X_QGIS = create("application/x-qgis", "qgs", "shp", "shx");
	public static final MediaType APPLICATION_X_QUICKTIMEPLAYER = create("application/x-quicktimeplayer", "qtl");
	public static final MediaType APPLICATION_X_RAR_COMPRESSED = create("application/x-rar-compressed", "rar");
	public static final MediaType APPLICATION_X_RDP = create("application/x-rdp", "rdp");
	public static final MediaType APPLICATION_X_REDHAT_PACKAGE_MANAGER = create("application/x-redhat-package-manager",
		"RPM");
	public static final MediaType APPLICATION_X_RESEARCH_INFO_SYSTEMS = create("application/x-research-info-systems",
		"RIS");
	public static final MediaType APPLICATION_X_RUBY = create("application/x-ruby", "rb");
	public static final MediaType APPLICATION_X_SCILAB = create("application/x-scilab", "sci", "sce");
	public static final MediaType APPLICATION_X_SHAR = create("application/x-shar", "shar");
	public static final MediaType APPLICATION_X_SHOCKWAVE_FLASH = create("application/x-shockwave-flash", "swf", "swfl");
	public static final MediaType APPLICATION_X_SH_UTF8 = createUTF8("application/x-sh", "sh");
	public static final MediaType APPLICATION_X_SILVERLIGHT_APP = create("application/x-silverlight-app", "xap");
	public static final MediaType APPLICATION_X_SILVERLIGHT = create("application/x-silverlight", "scr");
	public static final MediaType APPLICATION_XSLT_XML_UTF8 = createUTF8("application/xslt+xml", "xslt");
	public static final MediaType APPLICATION_XSPF_XML_UTF8 = createUTF8("application/xspf+xml", "xspf");
	public static final MediaType APPLICATION_X_SQL_UTF8 = createUTF8("application/x-sql", "sql");
	public static final MediaType APPLICATION_X_STUFFIT = create("application/x-stuffit", "sit", "sitx");
	public static final MediaType APPLICATION_X_STUFFITX = create("application/x-stuffitx", "sitx");
	public static final MediaType APPLICATION_X_SUBRIP = create("application/x-subrip", "srt");
	public static final MediaType APPLICATION_X_SV4CPIO = create("application/x-sv4cpio", "sv4cpio");
	public static final MediaType APPLICATION_X_SV4CRC = create("application/x-sv4crc", "sv4crc");
	public static final MediaType APPLICATION_X_T3VM_IMAGE = create("application/x-t3vm-image", "t3");
	public static final MediaType APPLICATION_X_TADS = create("application/x-tads", "gam");
	public static final MediaType APPLICATION_X_TAR = create("application/x-tar", "tar");
	public static final MediaType APPLICATION_X_TCL = create("application/x-tcl", "tcl");
	public static final MediaType APPLICATION_X_TEX_GF = create("application/x-tex-gf", "gf");
	public static final MediaType APPLICATION_X_TEXINFO = create("application/x-texinfo", "texinfo", "texi");
	public static final MediaType APPLICATION_X_TEX_PK = create("application/x-tex-pk", "pk");
	public static final MediaType APPLICATION_X_TEX = create("application/x-tex", "tex");
	public static final MediaType APPLICATION_X_TEX_TFM = create("application/x-tex-tfm", "tfm");
	public static final MediaType APPLICATION_X_TGIF = create("application/x-tgif", "obj");
	public static final MediaType APPLICATION_X_TRASH = create("application/x-trash", "~", "%", "bak", "old", "sik");
	public static final MediaType APPLICATION_X_TROFF_MAN = create("application/x-troff-man", "man");
	public static final MediaType APPLICATION_X_TROFF_ME = create("application/x-troff-me", "me");
	public static final MediaType APPLICATION_X_TROFF_MS = create("application/x-troff-ms", "ms");
	public static final MediaType APPLICATION_X_TROFF = create("application/x-troff", "t", "tr", "roff");
	public static final MediaType APPLICATION_X_USTAR = create("application/x-ustar", "ustar");
	public static final MediaType APPLICATION_XV_XML = create("application/xv+xml", "mxml", "xhvml", "xvml", "xvm");
	public static final MediaType APPLICATION_X_WAIS_SOURCE = create("application/x-wais-source", "src");
	public static final MediaType APPLICATION_X_WINGZ = create("application/x-wingz", "wz");
	public static final MediaType APPLICATION_X_X509_CA_CERT = create("application/x-x509-ca-cert", "der", "crt");
	public static final MediaType APPLICATION_X_XCF = create("application/x-xcf", "xcf");
	public static final MediaType APPLICATION_X_XFIG = create("application/x-xfig", "fig");
	public static final MediaType APPLICATION_X_XLIFF_XML = create("application/x-xliff+xml", "xlf");
	public static final MediaType APPLICATION_X_XPINSTALL = create("application/x-xpinstall", "xpi");
	public static final MediaType APPLICATION_X_XZ = create("application/x-xz", "xz");
	public static final MediaType APPLICATION_X_ZMACHINE = create("application/x-zmachine", "z1", "z2", "z3", "z4",
		"z5", "Z6", "Z7", "Z8");
	public static final MediaType APPLICATION_YANG = create("application/yang", "yang");
	public static final MediaType APPLICATION_YIN_XML = create("application/yin+xml", "yin");
	public static final MediaType APPLICATION_ZIP = create("application/zip", "zip");
	public static final MediaType AUDIO_ADPCM = create("audio/adpcm", "adp");
	public static final MediaType AUDIO_AMR = create("audio/amr", "amr");
	public static final MediaType AUDIO_AMR_WB = create("audio/amr-wb", "awb");
	public static final MediaType AUDIO_ANNODEX = create("audio/annodex", "axa");
	public static final MediaType AUDIO_BASIC = create("audio/basic", "au", "snd");
	public static final MediaType AUDIO_CSOUND = create("audio/csound", "csd", "orc", "sco");
	public static final MediaType AUDIO_FLAC = create("audio/flac", "flac");
	public static final MediaType AUDIO_MIDI = create("audio/midi", "mid", "midi", "kar", "rmi");
	public static final MediaType AUDIO_MP4 = create("audio/mp4", "mp4a");
	public static final MediaType AUDIO_MPEG = create("audio/mpeg", "mpga", "mpega", "mp2", "mp2a", "mp3", "m2a",
		"m3a", "MP3", "M4A");
	public static final MediaType AUDIO_MPEGURL = create("audio/mpegurl", "m3u");
	public static final MediaType AUDIO_OGG = create("audio/ogg", "oga", "ogg", "spx");
	public static final MediaType AUDIO_PRS_SID = create("audio/prs.sid", "sid");
	public static final MediaType AUDIO_S3M = create("audio/s3m", "s3m");
	public static final MediaType AUDIO_SILK = create("audio/silk", "sil");
	public static final MediaType AUDIO_VND_DECE_AUDIO = create("audio/vnd.dece.audio", "uva", "uvva");
	public static final MediaType AUDIO_VND_DIGITAL_WINDS = create("audio/vnd.digital-winds", "eol");
	public static final MediaType AUDIO_VND_DRA = create("audio/vnd.dra", "dra");
	public static final MediaType AUDIO_VND_DTS = create("audio/vnd.dts", "dts");
	public static final MediaType AUDIO_VND_DTS_HD = create("audio/vnd.dts.hd", "dtshd");
	public static final MediaType AUDIO_VND_LUCENT_VOICE = create("audio/vnd.lucent.voice", "lvp");
	public static final MediaType AUDIO_VND_MS_PLAYREADY_MEDIA_PYA = create("audio/vnd.ms-playready.media.pya", "pya");
	public static final MediaType AUDIO_VND_NUERA_ECELP4800 = create("audio/vnd.nuera.ecelp4800", "ecelp4800");
	public static final MediaType AUDIO_VND_NUERA_ECELP7470 = create("audio/vnd.nuera.ecelp7470", "ecelp7470");
	public static final MediaType AUDIO_VND_NUERA_ECELP9600 = create("audio/vnd.nuera.ecelp9600", "ecelp9600");
	public static final MediaType AUDIO_VND_RIP = create("audio/vnd.rip", "rip");
	public static final MediaType AUDIO_WEBM = create("audio/webm", "weba");
	public static final MediaType AUDIO_X_AAC = create("audio/x-aac", "aac");
	public static final MediaType AUDIO_X_AIFF = create("audio/x-aiff", "aif", "aiff", "aifc");
	public static final MediaType AUDIO_X_CAF = create("audio/x-caf", "caf");
	public static final MediaType AUDIO_X_FLAC = create("audio/x-flac", "flac");
	public static final MediaType AUDIO_X_GSM = create("audio/x-gsm", "gsm");
	public static final MediaType AUDIO_X_MATROSKA = create("audio/x-matroska", "mka");
	public static final MediaType AUDIO_X_MPEGURL = create("audio/x-mpegurl", "m3u");
	public static final MediaType AUDIO_X_MS_WAX = create("audio/x-ms-wax", "wax");
	public static final MediaType AUDIO_X_MS_WMA = create("audio/x-ms-wma", "wma");
	public static final MediaType AUDIO_XM = create("audio/xm", "xm");
	public static final MediaType AUDIO_X_PN_REALAUDIO_PLUGIN = create("audio/x-pn-realaudio-plugin", "rmp");
	public static final MediaType AUDIO_X_PN_REALAUDIO = create("audio/x-pn-realaudio", "ra", "rm", "ram");
	public static final MediaType AUDIO_X_REALAUDIO = create("audio/x-realaudio", "ra");
	public static final MediaType AUDIO_X_SCPLS = create("audio/x-scpls", "pls");
	public static final MediaType AUDIO_X_SD2 = create("audio/x-sd2", "sd2");
	public static final MediaType AUDIO_X_WAV = create("audio/x-wav", "wav");
	public static final MediaType CHEMICAL_X_ALCHEMY = create("chemical/x-alchemy", "alc");
	public static final MediaType CHEMICAL_X_CACHE = create("chemical/x-cache", "cac", "cache");
	public static final MediaType CHEMICAL_X_CACHE_CSF = create("chemical/x-cache-csf", "csf");
	public static final MediaType CHEMICAL_X_CACTVS_BINARY = create("chemical/x-cactvs-binary", "cbin", "cascii",
		"ctab");
	public static final MediaType CHEMICAL_X_CDX = create("chemical/x-cdx", "cdx");
	public static final MediaType CHEMICAL_X_CERIUS = create("chemical/x-cerius", "cer");
	public static final MediaType CHEMICAL_X_CHEM3D = create("chemical/x-chem3d", "c3d");
	public static final MediaType CHEMICAL_X_CHEMDRAW = create("chemical/x-chemdraw", "chm");
	public static final MediaType CHEMICAL_X_CIF = create("chemical/x-cif", "cif");
	public static final MediaType CHEMICAL_X_CMDF = create("chemical/x-cmdf", "cmdf");
	public static final MediaType CHEMICAL_X_CML = create("chemical/x-cml", "cml");
	public static final MediaType CHEMICAL_X_COMPASS = create("chemical/x-compass", "cpa");
	public static final MediaType CHEMICAL_X_CROSSFIRE = create("chemical/x-crossfire", "bsd");
	public static final MediaType CHEMICAL_X_CSML = create("chemical/x-csml", "csml", "csm");
	public static final MediaType CHEMICAL_X_CTX = create("chemical/x-ctx", "ctx");
	public static final MediaType CHEMICAL_X_CXF = create("chemical/x-cxf", "cxf", "cef");
	public static final MediaType CHEMICAL_X_DAYLIGHT_SMILES = create("chemical/x-daylight-smiles", "smi");
	public static final MediaType CHEMICAL_X_EMBL_DL_NUCLEOTIDE = create("chemical/x-embl-dl-nucleotide", "emb", "embl");
	public static final MediaType CHEMICAL_X_GALACTIC_SPC = create("chemical/x-galactic-spc", "spc");
	public static final MediaType CHEMICAL_X_GAMESS_INPUT = create("chemical/x-gamess-input", "inp", "gam", "gamin");
	public static final MediaType CHEMICAL_X_GAUSSIAN_CHECKPOINT = create("chemical/x-gaussian-checkpoint", "fch",
		"fchk");
	public static final MediaType CHEMICAL_X_GAUSSIAN_CUBE = create("chemical/x-gaussian-cube", "cub");
	public static final MediaType CHEMICAL_X_GAUSSIAN_INPUT = create("chemical/x-gaussian-input", "gau", "gjc", "gjf");
	public static final MediaType CHEMICAL_X_GAUSSIAN_LOG = create("chemical/x-gaussian-log", "gal");
	public static final MediaType CHEMICAL_X_GCG8_SEQUENCE = create("chemical/x-gcg8-sequence", "gcg");
	public static final MediaType CHEMICAL_X_GENBANK = create("chemical/x-genbank", "gen");
	public static final MediaType CHEMICAL_X_HIN = create("chemical/x-hin", "hin");
	public static final MediaType CHEMICAL_X_ISOSTAR = create("chemical/x-isostar", "istr", "ist");
	public static final MediaType CHEMICAL_X_JCAMP_DX = create("chemical/x-jcamp-dx", "jdx", "dx");
	public static final MediaType CHEMICAL_X_KINEMAGE = create("chemical/x-kinemage", "kin");
	public static final MediaType CHEMICAL_X_MACMOLECULE = create("chemical/x-macmolecule", "mcm");
	public static final MediaType CHEMICAL_X_MACROMODEL_INPUT = create("chemical/x-macromodel-input", "mmd", "mmod");
	public static final MediaType CHEMICAL_X_MDL_MOLFILE = create("chemical/x-mdl-molfile", "mol");
	public static final MediaType CHEMICAL_X_MDL_RDFILE = create("chemical/x-mdl-rdfile", "rd");
	public static final MediaType CHEMICAL_X_MDL_RXNFILE = create("chemical/x-mdl-rxnfile", "rxn");
	public static final MediaType CHEMICAL_X_MDL_SDFILE = create("chemical/x-mdl-sdfile", "sd", "sdf");
	public static final MediaType CHEMICAL_X_MDL_TGF = create("chemical/x-mdl-tgf", "tgf");
	public static final MediaType CHEMICAL_X_MIF = create("chemical/x-mif", "mif");
	public static final MediaType CHEMICAL_X_MMCIF = create("chemical/x-mmcif", "mcif");
	public static final MediaType CHEMICAL_X_MOL2 = create("chemical/x-mol2", "mol2");
	public static final MediaType CHEMICAL_X_MOLCONN_Z = create("chemical/x-molconn-z", "b");
	public static final MediaType CHEMICAL_X_MOPAC_GRAPH = create("chemical/x-mopac-graph", "gpt");
	public static final MediaType CHEMICAL_X_MOPAC_INPUT = create("chemical/x-mopac-input", "mop", "mopcrt", "mpc",
		"zmt");
	public static final MediaType CHEMICAL_X_MOPAC_OUT = create("chemical/x-mopac-out", "moo");
	public static final MediaType CHEMICAL_X_MOPAC_VIB = create("chemical/x-mopac-vib", "mvb");
	public static final MediaType CHEMICAL_X_NCBI_ASN1_ASCII = create("chemical/x-ncbi-asn1-ascii", "prt", "ent");
	public static final MediaType CHEMICAL_X_NCBI_ASN1 = create("chemical/x-ncbi-asn1", "asn");
	public static final MediaType CHEMICAL_X_NCBI_ASN1_BINARY = create("chemical/x-ncbi-asn1-binary", "val", "aso");
	public static final MediaType CHEMICAL_X_NCBI_ASN1_SPEC = create("chemical/x-ncbi-asn1-spec", "asn");
	public static final MediaType CHEMICAL_X_PDB = create("chemical/x-pdb", "pdb", "ent");
	public static final MediaType CHEMICAL_X_ROSDAL = create("chemical/x-rosdal", "ros");
	public static final MediaType CHEMICAL_X_SWISSPROT = create("chemical/x-swissprot", "sw");
	public static final MediaType CHEMICAL_X_VAMAS_ISO14976 = create("chemical/x-vamas-iso14976", "vms");
	public static final MediaType CHEMICAL_X_VMD = create("chemical/x-vmd", "vmd");
	public static final MediaType CHEMICAL_X_XTEL = create("chemical/x-xtel", "xtel");
	public static final MediaType CHEMICAL_X_XYZ = create("chemical/x-xyz", "xyz");
	public static final MediaType IMAGE_BMP = create("image/bmp", "bmp");
	public static final MediaType IMAGE_CGM = create("image/cgm", "cgm");
	public static final MediaType IMAGE_G3FAX = create("image/g3fax", "g3");
	public static final MediaType IMAGE_GIF = create("image/gif", "gif");
	public static final MediaType IMAGE_IEF = create("image/ief", "ief");
	public static final MediaType IMAGE_JPEG = create("image/jpeg", "jpeg", "jpg", "jpe");
	public static final MediaType IMAGE_KTX = create("image/ktx", "ktx");
	public static final MediaType IMAGE_PCX = create("image/pcx", "pcx");
	public static final MediaType IMAGE_PNG = create("image/png", "png");
	public static final MediaType IMAGE_PRS_BTIF = create("image/prs.btif", "btif");
	public static final MediaType IMAGE_SGI = create("image/sgi", "sgi");
	public static final MediaType IMAGE_SVG_XML = createUTF8("image/svg+xml", "svg", "svgz");
	public static final MediaType IMAGE_TIFF = create("image/tiff", "tiff", "tif");
	public static final MediaType IMAGE_VND_ADOBE_PHOTOSHOP = create("image/vnd.adobe.photoshop", "psd");
	public static final MediaType IMAGE_VND_DECE_GRAPHIC = create("image/vnd.dece.graphic", "uvi", "uvvi", "uvg",
		"uvvg");
	public static final MediaType IMAGE_VND_DJVU = create("image/vnd.djvu", "djvu", "djv");
	public static final MediaType IMAGE_VND_DVB_SUBTITLE = create("image/vnd.dvb.subtitle", "sub");
	public static final MediaType IMAGE_VND_DWG = create("image/vnd.dwg", "dwg");
	public static final MediaType IMAGE_VND_DXF = create("image/vnd.dxf", "dxf");
	public static final MediaType IMAGE_VND_FASTBIDSHEET = create("image/vnd.fastbidsheet", "fbs");
	public static final MediaType IMAGE_VND_FPX = create("image/vnd.fpx", "fpx");
	public static final MediaType IMAGE_VND_FST = create("image/vnd.fst", "fst");
	public static final MediaType IMAGE_VND_FUJIXEROX_EDMICS_MMR = create("image/vnd.fujixerox.edmics-mmr", "mmr");
	public static final MediaType IMAGE_VND_FUJIXEROX_EDMICS_RLC = create("image/vnd.fujixerox.edmics-rlc", "rlc");
	public static final MediaType IMAGE_VND_MS_MODI = create("image/vnd.ms-modi", "mdi");
	public static final MediaType IMAGE_VND_MS_PHOTO = create("image/vnd.ms-photo", "wdp");
	public static final MediaType IMAGE_VND_NET_FPX = create("image/vnd.net-fpx", "npx");
	public static final MediaType IMAGE_VND_WAP_WBMP = create("image/vnd.wap.wbmp", "wbmp");
	public static final MediaType IMAGE_VND_XIFF = create("image/vnd.xiff", "xif");
	public static final MediaType IMAGE_WEBP = create("image/webp", "webp");
	public static final MediaType IMAGE_X_3DS = create("image/x-3ds", "3ds");
	public static final MediaType IMAGE_X_CANON_CR2 = create("image/x-canon-cr2", "cr2");
	public static final MediaType IMAGE_X_CANON_CRW = create("image/x-canon-crw", "crw");
	public static final MediaType IMAGE_X_CMU_RASTER = create("image/x-cmu-raster", "ras");
	public static final MediaType IMAGE_X_CMX = create("image/x-cmx", "cmx");
	public static final MediaType IMAGE_X_CORELDRAW = create("image/x-coreldraw", "cdr");
	public static final MediaType IMAGE_X_CORELDRAWPATTERN = create("image/x-coreldrawpattern", "pat");
	public static final MediaType IMAGE_X_CORELDRAWTEMPLATE = create("image/x-coreldrawtemplate", "cdt");
	public static final MediaType IMAGE_X_CORELPHOTOPAINT = create("image/x-corelphotopaint", "cpt");
	public static final MediaType IMAGE_X_EPSON_ERF = create("image/x-epson-erf", "erf");
	public static final MediaType IMAGE_X_FREEHAND = create("image/x-freehand", "fh", "fhc", "fh4", "fh5", "fh7");
	public static final MediaType IMAGE_X_ICON = create("image/x-icon", "ico");
	public static final MediaType IMAGE_X_JG = create("image/x-jg", "art");
	public static final MediaType IMAGE_X_JNG = create("image/x-jng", "jng");
	public static final MediaType IMAGE_X_MRSID_IMAGE = create("image/x-mrsid-image", "sid");
	public static final MediaType IMAGE_X_NIKON_NEF = create("image/x-nikon-nef", "nef");
	public static final MediaType IMAGE_X_OLYMPUS_ORF = create("image/x-olympus-orf", "orf");
	public static final MediaType IMAGE_X_PCX = create("image/x-pcx", "pcx");
	public static final MediaType IMAGE_X_PHOTOSHOP = create("image/x-photoshop", "psd");
	public static final MediaType IMAGE_X_PICT = create("image/x-pict", "pic", "pct");
	public static final MediaType IMAGE_X_PORTABLE_ANYMAP = create("image/x-portable-anymap", "pnm");
	public static final MediaType IMAGE_X_PORTABLE_BITMAP = create("image/x-portable-bitmap", "pbm");
	public static final MediaType IMAGE_X_PORTABLE_GRAYMAP = create("image/x-portable-graymap", "pgm");
	public static final MediaType IMAGE_X_PORTABLE_PIXMAP = create("image/x-portable-pixmap", "ppm");
	public static final MediaType IMAGE_X_RGB = create("image/x-rgb", "rgb");
	public static final MediaType IMAGE_X_TGA = create("image/x-tga", "tga");
	public static final MediaType IMAGE_X_XBITMAP = create("image/x-xbitmap", "xbm");
	public static final MediaType IMAGE_X_XPIXMAP = create("image/x-xpixmap", "xpm");
	public static final MediaType IMAGE_X_XWINDOWDUMP = create("image/x-xwindowdump", "xwd");
	public static final MediaType MESSAGE_RFC822 = create("message/rfc822", "eml", "mime");
	public static final MediaType MODEL_IGES = create("model/iges", "igs", "iges");
	public static final MediaType MODEL_MESH = create("model/mesh", "msh", "mesh", "silo");
	public static final MediaType MODEL_VND_COLLADA_XML = create("model/vnd.collada+xml", "dae");
	public static final MediaType MODEL_VND_DWF = create("model/vnd.dwf", "dwf");
	public static final MediaType MODEL_VND_GDL = create("model/vnd.gdl", "gdl");
	public static final MediaType MODEL_VND_GTW = create("model/vnd.gtw", "gtw");
	public static final MediaType MODEL_VND_MTS = create("model/vnd.mts", "mts");
	public static final MediaType MODEL_VND_VTU = create("model/vnd.vtu", "vtu");
	public static final MediaType MODEL_VRML = create("model/vrml", "wrl", "vrml");
	public static final MediaType MODEL_X3D_BINARY = create("model/x3d+binary", "x3db", "x3dbz");
	public static final MediaType MODEL_X3D_VRML = create("model/x3d+vrml", "x3dv", "x3dvz");
	public static final MediaType MODEL_X3D_XML = create("model/x3d+xml", "x3d", "x3dz");
	public static final MediaType TEXT_CACHE_MANIFEST = create("text/cache-manifest", "appcache", "manifest");
	public static final MediaType TEXT_CALENDAR = create("text/calendar", "ics", "icz", "ifb");
	public static final MediaType TEXT_CSS_UTF8 = createUTF8("text/css", "css");
	public static final MediaType TEXT_CSV_UTF8 = createUTF8("text/csv", "csv");
	public static final MediaType TEXT_H323 = create("text/h323", "323");
	public static final MediaType TEXT_HTML_UTF8 = createUTF8("text/html", "html", "htm", "shtml");
	public static final MediaType TEXT_IULS = create("text/iuls", "uls");
	public static final MediaType TEXT_MATHML = create("text/mathml", "mml");
	public static final MediaType TEXT_N3 = create("text/n3", "n3");
	public static final MediaType TEXT_PLAIN = create("text/plain");
	public static final MediaType TEXT_PLAIN_UTF8 = createUTF8("text/plain", "asc", "txt", "text", "conf", "def",
		"pot", "brf", "LIST", "LOG", "IN");
	public static final MediaType TEXT_PRS_LINES_TAG = create("text/prs.lines.tag", "dsc");
	public static final MediaType TEXT_RICHTEXT = create("text/richtext", "rtx");
	public static final MediaType TEXT_SCRIPTLET = create("text/scriptlet", "sct", "wsc");
	public static final MediaType TEXT_SGML = create("text/sgml", "sgml", "sgm");
	public static final MediaType TEXT_TAB_SEPARATED_VALUES_UTF8 = createUTF8("text/tab-separated-values", "tsv");
	public static final MediaType TEXT_TEXMACS = create("text/texmacs", "tm");
	public static final MediaType TEXT_TROFF = create("text/troff", "t", "tr", "roff", "man", "me", "ms");
	public static final MediaType TEXT_TURTLE = create("text/turtle", "ttl");
	public static final MediaType TEXT_URI_LIST = create("text/uri-list", "uri", "uris", "urls");
	public static final MediaType TEXT_VCARD = create("text/vcard", "vcard");
	public static final MediaType TEXT_VND_CURL = create("text/vnd.curl", "curl");
	public static final MediaType TEXT_VND_CURL_DCURL = create("text/vnd.curl.dcurl", "dcurl");
	public static final MediaType TEXT_VND_CURL_MCURL = create("text/vnd.curl.mcurl", "mcurl");
	public static final MediaType TEXT_VND_CURL_SCURL = create("text/vnd.curl.scurl", "scurl");
	public static final MediaType TEXT_VND_DVB_SUBTITLE = create("text/vnd.dvb.subtitle", "sub");
	public static final MediaType TEXT_VND_FLY = create("text/vnd.fly", "fly");
	public static final MediaType TEXT_VND_FMI_FLEXSTOR = create("text/vnd.fmi.flexstor", "flx");
	public static final MediaType TEXT_VND_GRAPHVIZ = create("text/vnd.graphviz", "gv");
	public static final MediaType TEXT_VND_IN3D_3DML = create("text/vnd.in3d.3dml", "3dml");
	public static final MediaType TEXT_VND_IN3D_SPOT = create("text/vnd.in3d.spot", "spot");
	public static final MediaType TEXT_VND_SUN_J2ME_APP_DESCRIPTOR = create("text/vnd.sun.j2me.app-descriptor", "jad");
	public static final MediaType TEXT_VND_WAP_WMLSCRIPT = create("text/vnd.wap.wmlscript", "wmls");
	public static final MediaType TEXT_VND_WAP_WML = create("text/vnd.wap.wml", "wml");
	public static final MediaType TEXT_X_ASM_UTF8 = createUTF8("text/x-asm", "s", "asm");
	public static final MediaType TEXT_X_BIBTEX_UTF8 = createUTF8("text/x-bibtex", "bib");
	public static final MediaType TEXT_X_BOO_UTF8 = createUTF8("text/x-boo", "boo");
	public static final MediaType TEXT_X_C_UTF8 = createUTF8("text/x-c", "c", "cc", "cxx", "cpp", "h", "hh", "dic");
	public static final MediaType TEXT_X_CHDR_UTF8 = createUTF8("text/x-chdr", "h");
	public static final MediaType TEXT_X_C__HDR_UTF8 = createUTF8("text/x-c++hdr", "h++", "hpp", "hxx", "hh");
	public static final MediaType TEXT_X_COMPONENT_UTF8 = createUTF8("text/x-component", "htc");
	public static final MediaType TEXT_X_CSH_UTF8 = createUTF8("text/x-csh", "csh");
	public static final MediaType TEXT_X_CSRC_UTF8 = createUTF8("text/x-csrc", "c");
	public static final MediaType TEXT_X_C__SRC_UTF8 = createUTF8("text/x-c++src", "c++", "cpp", "cxx", "cc");
	public static final MediaType TEXT_X_DIFF_UTF8 = createUTF8("text/x-diff", "diff", "patch");
	public static final MediaType TEXT_X_DSRC_UTF8 = createUTF8("text/x-dsrc", "d");
	public static final MediaType TEXT_X_FORTRAN_UTF8 = createUTF8("text/x-fortran", "f", "for", "f77", "f90");
	public static final MediaType TEXT_X_HASKELL_UTF8 = createUTF8("text/x-haskell", "hs");
	public static final MediaType TEXT_X_JAVA_UTF8 = createUTF8("text/x-java", "java");
	public static final MediaType TEXT_X_JAVA_SOURCE_UTF8 = createUTF8("text/x-java-source", "java");
	public static final MediaType TEXT_X_LITERATE_HASKELL_UTF8 = createUTF8("text/x-literate-haskell", "lhs");
	public static final MediaType TEXT_X_MOC_UTF8 = createUTF8("text/x-moc", "moc");
	public static final MediaType TEXT_X_NFO_UTF8 = createUTF8("text/x-nfo", "nfo");
	public static final MediaType TEXT_X_OPML_UTF8 = createUTF8("text/x-opml", "opml");
	public static final MediaType TEXT_X_PASCAL_UTF8 = createUTF8("text/x-pascal", "p", "pas");
	public static final MediaType TEXT_X_PCS_GCD_UTF8 = createUTF8("text/x-pcs-gcd", "gcd");
	public static final MediaType TEXT_X_PERL_UTF8 = createUTF8("text/x-perl", "pl", "pm");
	public static final MediaType TEXT_X_PYTHON_UTF8 = createUTF8("text/x-python", "py");
	public static final MediaType TEXT_X_SCALA_UTF8 = createUTF8("text/x-scala", "scala");
	public static final MediaType TEXT_X_SETEXT_UTF8 = createUTF8("text/x-setext", "etx");
	public static final MediaType TEXT_X_SFV_UTF8 = createUTF8("text/x-sfv", "sfv");
	public static final MediaType TEXT_X_SH_UTF8 = createUTF8("text/x-sh", "sh");
	public static final MediaType TEXT_X_TCL_UTF8 = createUTF8("text/x-tcl", "tcl", "tk");
	public static final MediaType TEXT_X_TEX_UTF8 = createUTF8("text/x-tex", "tex", "ltx", "sty", "cls");
	public static final MediaType TEXT_X_UUENCODE_UTF8 = createUTF8("text/x-uuencode", "uu");
	public static final MediaType TEXT_X_VCALENDAR_UTF8 = createUTF8("text/x-vcalendar", "vcs");
	public static final MediaType TEXT_X_VCARD_UTF8 = createUTF8("text/x-vcard", "vcf");
	public static final MediaType VIDEO_3GPP2 = create("video/3gpp2", "3g2");
	public static final MediaType VIDEO_3GPP = create("video/3gpp", "3gp");
	public static final MediaType VIDEO_ANNODEX = create("video/annodex", "axv");
	public static final MediaType VIDEO_DL = create("video/dl", "dl");
	public static final MediaType VIDEO_DV = create("video/dv", "dif", "dv");
	public static final MediaType VIDEO_FLI = create("video/fli", "fli");
	public static final MediaType VIDEO_GL = create("video/gl", "gl");
	public static final MediaType VIDEO_H261 = create("video/h261", "h261");
	public static final MediaType VIDEO_H263 = create("video/h263", "h263");
	public static final MediaType VIDEO_H264 = create("video/h264", "h264");
	public static final MediaType VIDEO_JPEG = create("video/jpeg", "jpgv");
	public static final MediaType VIDEO_JPM = create("video/jpm", "jpm", "jpgm");
	public static final MediaType VIDEO_MJ2 = create("video/mj2", "mj2", "mjp2");
	public static final MediaType VIDEO_MP2T = create("video/mp2t", "ts");
	public static final MediaType VIDEO_MP4 = create("video/mp4", "mp4", "mp4v", "mpg4");
	public static final MediaType VIDEO_MPEG = create("video/mpeg", "mpeg", "mpg", "mpe", "m1v", "m2v");
	public static final MediaType VIDEO_OGG = create("video/ogg", "ogv");
	public static final MediaType VIDEO_QUICKTIME = create("video/quicktime", "qt", "mov");
	public static final MediaType VIDEO_VND_DECE_HD = create("video/vnd.dece.hd", "uvh", "uvvh");
	public static final MediaType VIDEO_VND_DECE_MOBILE = create("video/vnd.dece.mobile", "uvm", "uvvm");
	public static final MediaType VIDEO_VND_DECE_PD = create("video/vnd.dece.pd", "uvp", "uvvp");
	public static final MediaType VIDEO_VND_DECE_SD = create("video/vnd.dece.sd", "uvs", "uvvs");
	public static final MediaType VIDEO_VND_DECE_VIDEO = create("video/vnd.dece.video", "uvv", "uvvv");
	public static final MediaType VIDEO_VND_DVB_FILE = create("video/vnd.dvb.file", "dvb");
	public static final MediaType VIDEO_VND_FVT = create("video/vnd.fvt", "fvt");
	public static final MediaType VIDEO_VND_MPEGURL = create("video/vnd.mpegurl", "mxu", "m4u");
	public static final MediaType VIDEO_VND_MS_PLAYREADY_MEDIA_PYV = create("video/vnd.ms-playready.media.pyv", "pyv");
	public static final MediaType VIDEO_VND_UVVU_MP4 = create("video/vnd.uvvu.mp4", "uvu", "uvvu");
	public static final MediaType VIDEO_VND_VIVO = create("video/vnd.vivo", "viv");
	public static final MediaType VIDEO_WEBM = create("video/webm", "webm");
	public static final MediaType VIDEO_X_F4V = create("video/x-f4v", "f4v");
	public static final MediaType VIDEO_X_FLI = create("video/x-fli", "fli");
	public static final MediaType VIDEO_X_FLV = create("video/x-flv", "flv");
	public static final MediaType VIDEO_X_LA_ASF = create("video/x-la-asf", "lsf", "lsx");
	public static final MediaType VIDEO_X_M4V = create("video/x-m4v", "m4v");
	public static final MediaType VIDEO_X_MATROSKA = create("video/x-matroska", "mpv", "mkv", "mk3d", "mks");
	public static final MediaType VIDEO_X_MNG = create("video/x-mng", "mng");
	public static final MediaType VIDEO_X_MS_ASF = create("video/x-ms-asf", "asf", "asx");
	public static final MediaType VIDEO_X_MSVIDEO = create("video/x-msvideo", "avi");
	public static final MediaType VIDEO_X_MS_VOB = create("video/x-ms-vob", "vob");
	public static final MediaType VIDEO_X_MS_WMV = create("video/x-ms-wmv", "wmv");
	public static final MediaType VIDEO_X_MS_WM = create("video/x-ms-wm", "wm");
	public static final MediaType VIDEO_X_MS_WMX = create("video/x-ms-wmx", "wmx");
	public static final MediaType VIDEO_X_MS_WVX = create("video/x-ms-wvx", "wvx");
	public static final MediaType VIDEO_X_SGI_MOVIE = create("video/x-sgi-movie", "movie");
	public static final MediaType VIDEO_X_SMV = create("video/x-smv", "smv");
	public static final MediaType X_CONFERENCE_X_COOLTALK = create("x-conference/x-cooltalk", "ice");
	public static final MediaType X_EPOC_X_SISX_APP = create("x-epoc/x-sisx-app", "sisx");
	public static final MediaType X_WORLD_X_VRML = create("x-world/x-vrml", "vrm", "vrml", "wrl");

	/*******************************************************/

	public static final MediaType HTML_UTF_8 = TEXT_HTML_UTF8;
	public static final MediaType CSS_UTF_8 = TEXT_CSS_UTF8;
	public static final MediaType CSV_UTF_8 = TEXT_CSV_UTF8;
	public static final MediaType PLAIN_TEXT_UTF_8 = TEXT_PLAIN_UTF8;

	public static final MediaType XHTML_XML_UTF8 = APPLICATION_XHTML_XML_UTF8;
	public static final MediaType JAVASCRIPT_UTF8 = APPLICATION_JAVASCRIPT_UTF8;
	public static final MediaType JSON = APPLICATION_JSON;
	public static final MediaType XML_UTF_8 = APPLICATION_XML_UTF8;

	public static final MediaType BINARY = APPLICATION_OCTET_STREAM;
	public static final MediaType ZIP = APPLICATION_ZIP;
	public static final MediaType PDF = APPLICATION_PDF;
	public static final MediaType SWF = APPLICATION_X_SHOCKWAVE_FLASH;

	public static final MediaType JPEG = IMAGE_JPEG;
	public static final MediaType PNG = IMAGE_PNG;
	public static final MediaType BMP = IMAGE_BMP;
	public static final MediaType GIF = IMAGE_GIF;
	public static final MediaType SVG = IMAGE_SVG_XML;

	public static final MediaType DEFAULT = MediaType.BINARY;

	/*******************************************************/

	public static synchronized MediaType create(String type, String... fileExtensisons) {
		return create(type, NO_ATTR, fileExtensisons);
	}

	public static synchronized MediaType create(String type, String[] attributes, String... fileExtensisons) {
		MediaType mt = new MediaType(type, attributes);

		for (String ext : fileExtensisons) {
			FILE_EXTENSISONS.put(ext, mt);
		}

		return mt;
	}

	public static synchronized MediaType createUTF8(String type, String... fileExtensisons) {
		return create(type, UTF8_ATTR, fileExtensisons);
	}

	public static synchronized MediaType getByFileExtension(String fileExtension) {
		return FILE_EXTENSISONS.get(fileExtension);
	}

	public static synchronized MediaType getByFileName(String filename) {
		int dotPos = filename.lastIndexOf('.');
		if (dotPos >= 0) {
			String ext = filename.substring(dotPos + 1);
			return getByFileExtension(ext);
		} else {
			return MediaType.DEFAULT;
		}
	}

	public static MediaType of(String contentType) {
		return new MediaType(contentType);
	}

	private final byte[] bytes;

	private final byte[] httpHeaderBytes;

	private MediaType(String contentType) {
		this.bytes = contentType.getBytes();
		this.httpHeaderBytes = ("Content-Type: " + new String(bytes) + "\r\n").getBytes();
	}

	private MediaType(String name, String[] attributes) {
		this.bytes = join(name, attributes).getBytes();
		this.httpHeaderBytes = ("Content-Type: " + new String(bytes) + "\r\n").getBytes();
	}

	private String join(String name, String[] attributes) {
		String attrs = U.join("; ", (Object[]) attributes);

		return attrs.isEmpty() ? name : name + "; " + attrs;
	}

	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public String toString() {
		return U.frmt("MediaType[%s]", new String(bytes));
	}

	public byte[] asHttpHeader() {
		return httpHeaderBytes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bytes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MediaType other = (MediaType) obj;
		if (!Arrays.equals(bytes, other.bytes))
			return false;
		return true;
	}

	public String info() {
		if (this == HTML_UTF_8) {
			return "html";
		}

		if (this == JSON) {
			return "json";
		}

		if (this == PLAIN_TEXT_UTF_8) {
			return "plain";
		}

		if (this == BINARY) {
			return "binary";
		}

		return toString();
	}

}
