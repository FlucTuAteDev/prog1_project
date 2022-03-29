package Menu;

import Utils.RGB;

public class HeaderItem {
	String text;
	// RGB background;
	RGB foreground;
	Object[] textArgs;

	public HeaderItem(RGB foreground, String text, Object... textArgs) {
		this.text = text;
		this.foreground = foreground;
		this.textArgs = textArgs;
	}
}
