package View.Colors;

import Base.Console;
import Base.Console.ANSI;

/**
 * Defines the used colors in the game and useful functions for handling colors
 */
public final class Colors {
	public static final RGB WHITE = new RGB(255, 255, 255);
	public static final RGB BLACK = new RGB(0, 0, 0);
	public static final RGB DARK_GRAY = new RGB(50, 50, 50);
	public static final RGB GRAY = new RGB(100, 100, 100);
	public static final RGB LIGHT_GRAY = new RGB(150, 150, 150);
	public static final RGB RED = new RGB(255, 0, 0);
	public static final RGB GREEN = new RGB(0, 200, 0);
	public static final RGB DARK_GREEN = new RGB(0, 80, 0);
	public static final RGB BLUE = new RGB(0, 200, 200);
	public static final RGB DARK_BLUE = new RGB(0, 100, 200);
	public static final RGB YELLOW = new RGB(255, 255, 0);
	
	public static final RGB DEFAULT_BG = new RGB(12, 12, 12);
	public static final RGB DEFAULT_FG = new RGB(204, 204, 204);

	public static final RGB DAMAGE = new RGB(150, 10, 10);
	public static final RGB HEAL = new RGB(50, 200, 50);

	public static RGB textFromBg(RGB bg) {
		double L = ((bg.r * 0.299) + (bg.g * 0.587) + (bg.b * 0.114)); // HUH?
		return (L > 186) ? BLACK : WHITE;
	}

	public static RGB brighten(RGB color, double fraction) {
        int red = (int) Math.round(Math.min(255, color.r + 255 * fraction));
        int green = (int) Math.round(Math.min(255, color.g + 255 * fraction));
        int blue = (int) Math.round(Math.min(255, color.b + 255 * fraction));

        return new RGB(red, green, blue);
    }

	public static String wrapWithColor(String str, RGB background, RGB foreground) {
		return String.format("%s%s%s%s", 
			Console.getBackground(background),
			Console.getForeground(foreground), 
			str, 
			ANSI.RESET_COLORS);
	}
	public static String wrapWithColor(String str, RGB foreground) {
		return wrapWithColor(str, null, foreground);
	}
	public static void setBgWithFg(RGB background) {
		Console.setBackground(background);
		Console.setForeground(textFromBg(background));
	}
}
