package Menu;
import Utils.RGB;

public class MenuItem {
	String text;
	// RGB background;
	RGB foreground;
	Runnable action;
	Object[] textArgs;
	boolean redrawSelf;

	public MenuItem(String text, RGB foreground, boolean redrawSelf, Runnable action, Object... textArgs) {
		this.text = text;
		// this.background = background;
		this.foreground = foreground;
		this.redrawSelf = redrawSelf;
		this.action = action;
		this.textArgs = textArgs;
	}

	public MenuItem setRedraw(boolean value) {
		this.redrawSelf = value;
		return this;
	}
}
