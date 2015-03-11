package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.Text;
import org.rapidoid.reactive.num.AbstractNum;

public class LastIndexText extends AbstractNum implements Num {

	private final Text t1;
	private final Text t2;

	public LastIndexText(Text t1, Text t2) {
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public int get() {
		return t1.get().lastIndexOf(t2.get());
	}

}
