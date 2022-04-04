package Menu;

import java.util.Arrays;
import java.util.function.Supplier;

import Utils.Colors;
import Utils.RGB;

public class HeaderItem {
	String text;
	RGB background;
	RGB foreground;
	Supplier<?>[] textArgs;

	public HeaderItem(RGB foreground, RGB background, String text, Supplier<?>... textArgs) {
		this.text = text;
		this.foreground = foreground;
		this.background = background;
		this.textArgs = textArgs;
	}

	public HeaderItem(RGB foreground, String text, Supplier<?>... textArgs) {
		this(foreground, Colors.DEFAULT_BG, text, textArgs);
	}

	public HeaderItem(String text, Supplier<?>... textArgs) {
		this(Colors.DEFAULT_FG, Colors.DEFAULT_BG, text, textArgs);
	}

	public Object[] evaluateArgs() {
		return Arrays.stream(this.textArgs).map(x -> x.get()).toArray();
	}
}
