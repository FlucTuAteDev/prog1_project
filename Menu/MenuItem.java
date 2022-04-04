package Menu;
import java.util.Arrays;
import java.util.function.Supplier;

import Utils.Colors;
import Utils.RGB;

public class MenuItem {
	String text;
	RGB background;
	RGB foreground;
	Runnable action;
	Supplier<?>[] textArgs;
	boolean redrawSelf;

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

	public MenuItem setRedraw(boolean value) {
		this.redrawSelf = value;
		return this;
	}
}
