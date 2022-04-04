package Menu.Items;
import java.util.Arrays;
import java.util.function.Supplier;

import Utils.Colors;
import Utils.RGB;

public class MenuItem {
	public final RGB foreground;
	public final RGB background;
	public final Runnable action;
	public final boolean redrawSelf;
	public final String text;
	public final Supplier<?>[] textArgs;

	public MenuItem(RGB foreground, RGB background, Runnable action, boolean redrawSelf, String text, Supplier<?>... textArgs) {
		this.text = text;
		this.background = background;
		this.foreground = foreground;
		this.redrawSelf = redrawSelf;
		this.action = action;
		this.textArgs = textArgs;
	}

	public MenuItem(RGB foreground, Runnable action, boolean redrawSelf, String text, Supplier<?>... textArgs) {
		this(foreground, Colors.DEFAULT_BG, action, redrawSelf, text, textArgs);
	}

	public MenuItem(Runnable action, boolean redrawSelf, String text, Supplier<?>... textArgs) {
		this(Colors.DEFAULT_FG, Colors.DEFAULT_BG, action, redrawSelf, text, textArgs);
	}

	public Object[] evaluateArgs() {
		return Arrays.stream(this.textArgs).map(x -> x.get()).toArray();
	}
}
