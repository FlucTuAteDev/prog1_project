package Menu.Items;

import java.util.Arrays;
import java.util.function.Supplier;

import View.Colors.Colors;
import View.Colors.RGB;

public class HeaderItem {
	public final String text;
	public final RGB background;
	public final RGB foreground;
	public final Supplier<?>[] textArgs;

	public HeaderItem(RGB foreground, RGB background, String text, Supplier<?>... textArgs) {
		this.foreground = foreground;
		this.background = background;
		this.text = text;
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
