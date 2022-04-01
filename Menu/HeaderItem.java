package Menu;

import java.util.Arrays;
import java.util.function.Supplier;

import Utils.RGB;

public class HeaderItem {
	String text;
	// RGB background;
	RGB foreground;
	Supplier<?>[] textArgs;

	public HeaderItem(RGB foreground, String text, Supplier<?>... textArgs) {
		this.text = text;
		this.foreground = foreground;
		this.textArgs = textArgs;
	}

	public Object[] evaluateArgs() {
		return Arrays.stream(this.textArgs).map(x -> x.get()).toArray();
	}
}
