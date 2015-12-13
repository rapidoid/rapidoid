package org.rapidoid.http.fast;

import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.wrap.BoolWrap;

public class ReqData {

	final Range rUri = new Range();
	final Range rVerb = new Range();
	final Range rPath = new Range();
	final Range rQuery = new Range();
	final Range rProtocol = new Range();

	final Ranges headers = new Ranges(50);

	final KeyValueRanges params = new KeyValueRanges(50);
	final KeyValueRanges headersKV = new KeyValueRanges(50);

	final KeyValueRanges cookies = new KeyValueRanges(50);

	final KeyValueRanges posted = new KeyValueRanges(50);
	final KeyValueRanges files = new KeyValueRanges(50);

	final Range rBody = new Range();

	final BoolWrap isGet = new BoolWrap();
	final BoolWrap isKeepAlive = new BoolWrap();

}
