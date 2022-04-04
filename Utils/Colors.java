package Utils;

public final class Colors {
	public static final RGB WHITE = new RGB(255, 255, 255);
	public static final RGB BLACK = new RGB(0, 0, 0);
	public static final RGB DARK_GRAY = new RGB(50, 50, 50);
	public static final RGB GRAY = new RGB(100, 100, 100);
	public static final RGB LIGHT_GRAY = new RGB(150, 150, 150);
	public static final RGB RED = new RGB(255, 0, 0);
	public static final RGB GREEN = new RGB(0, 200, 0);
	public static final RGB DARK_GREEN = new RGB(0, 100, 0);
	public static final RGB BLUE = new RGB(0, 200, 200);
	public static final RGB DARK_BLUE = new RGB(0, 150, 200);
	public static final RGB YELLOW = new RGB(255, 255, 0);

	public static final RGB DEFAULT_BG = new RGB(12, 12, 12);
	public static final RGB DEFAULT_FG = new RGB(204, 204, 204);

	public static RGB textFromBg(RGB bg) {
		double L = ((bg.r * 0.299) + (bg.g * 0.587) + (bg.b * 0.114)); // HUH?
		return (L > 186) ? BLACK : WHITE;
	}
}
