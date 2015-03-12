package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Bool;
import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.Text;

public abstract class AbstractText implements Text {

	@Override
	public Text plus(Text v) {
		return new PlusText(this, v);
	}

	@Override
	public Text remove(Text v) {
		return new RemoveText(this, v);
	}

	@Override
	public Text replace(Text v1, Text v2) {
		return new ReplaceText(this, v1, v2);
	}

	@Override
	public Text upper() {
		return new UpperText(this);
	}

	@Override
	public Text lower() {
		return new LowerText(this);
	}

	@Override
	public Text trim() {
		return new TrimText(this);
	}

	@Override
	public Text substring(Num beginIndex, Num endIndex) {
		return new SubstringText(this, beginIndex, endIndex);
	}

	@Override
	public Num length() {
		return new LengthText(this);
	}

	@Override
	public Num indexOf(Text v) {
		return new IndexText(this, v);
	}

	@Override
	public Text mid(Num beginIndex, Num endIndex) {
		return new MidText(this, beginIndex, endIndex);
	}

	@Override
	public Bool contains(Text v) {
		return new ContainsText(this, v);
	}

	@Override
	public Bool endsWith(Text v) {
		return new EndsText(this, v);
	}

	@Override
	public Bool startsWith(Text v) {
		return new StartsText(this, v);
	}

	@Override
	public Bool isEmpty() {
		return new EmptyText(this);
	}

	@Override
	public Bool eq(Text v) {
		return new EqText(this, v);
	}

	@Override
	public Num lastIndexOf(Text v) {
		return new LastIndexText(this, v);
	}

	@Override
	public Num parseInt() {
		return new ParseIntText(this);
	}

}
