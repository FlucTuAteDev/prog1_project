package Base;

import java.util.function.Consumer;

import View.Colors.RGB;

public class Console {
	public class ANSI {
		public static final String ESC = "\u001b";
		// static final String DEL = "\u007f";
		public static final String HOME = ESC + "[H";
		public static final String ERASE_SCREEN = ESC + "[2J";
		public static final String RESET_COLORS = ESC + "[0m";
		public static final String CURSOR_SAVE = ESC + " 7";
		public static final String CURSOR_RESTORE = ESC + " 8";
		public static final String CURSOR_REQUEST = ESC + "[6n";
		public static final String ERASE_LINE = ESC + "[2K";
		public static final String TEXT_BOLD = ESC + "[1m";
		public static final String TEXT_NORMAL = ESC + "[0m";
	}

	public static final int WIDTH = 120;
	public static final int HEIGHT = 30;

	// public static void print(Object o) {
	// System.out.print(o);
	// }
	public static enum Alignment {
		LEFT, CENTER, RIGHT
	};

	private static void printBase(Alignment alignment, int width, Consumer<String> printFn, String format, Object... args) {
		String s = String.format(format, args);
		String stripped = s.replaceAll("\u001B\\[[\\d;]*[^\\d;]", ""); // Strips ansi sequences
		int spaceLen = width - stripped.length();
		String pad = spaceLen % 2 == 0 ? "" : " ";
		switch (alignment) {
			case LEFT:
				printFn.accept(s);
				break;
			case CENTER: {
				String spaces = " ".repeat(spaceLen / 2);
				String out = String.format("%s%s%s" + pad, spaces, s, spaces);
				printFn.accept(out);
			}
				break;
			case RIGHT: {
				String spaces = " ".repeat((width - stripped.length()));
				String out = String.format("%s%s", spaces, s);
				printFn.accept(out);
			}
				break;
			default:
				break;
		}
	}

	public static void print(String format, Object... args) {
		printBase(Alignment.LEFT, 0, System.out::print, format, args);
	}

	public static void printAligned(Alignment alignment, int width, String format, Object... args) {
		printBase(alignment, width, System.out::print, format, args);
	}

	public static void println(String format, Object... args) {
		printBase(Alignment.LEFT, 0, System.out::println, format, args);
	}

	public static void printlnAligned(Alignment alignment, int width, String format, Object... args) {
		printBase(alignment, width, System.out::println, format, args);
	}

	/**
	 * Cursor control
	 */

	public static enum MoveDirection {
		UP("A"),
		DOWN("B"),
		RIGHT("C"),
		LEFT("D");

		private String dir;

		MoveDirection(String dir) {
			this.dir = dir;
		}

		public String getChar() {
			return dir;
		}
	}

	public static void setCursorCol(int col) {
		print(ANSI.ESC + "[" + col + "G");
	}

	public static void setCursorPosition(int row, int col) {
		print(ANSI.ESC + "[" + row + ";" + col + "H");
	}

	public static void moveCursor(MoveDirection direction, int amount) {
		if (amount < 1)
			return;
		print(ANSI.ESC + "[" + amount + direction.getChar());
	}

	public static void clearScreen() {
		try {
			if (System.getProperty("os.name").contains("Windows"))
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			else
				new ProcessBuilder("sh", "-c", "clear").inheritIO().start().waitFor();
		} catch (Exception e) {
			Console.println("%s", e);
		}
	}

	public static void clearLine() {
		print(ANSI.ERASE_LINE + "\r");
	}

	public static void clearBelow() {
		Console.print(ANSI.ESC + "[0J");
	}

	public static void clearLine(boolean cursorReturn) {
		print(ANSI.ERASE_LINE + (cursorReturn ? "\r" : ""));
	}

	/**
	 * Color manipulation
	 */
	public static void resetStyles() {
		print(ANSI.RESET_COLORS);
	}

	public static void setForeground(RGB color) {
		if (color == null)
			resetStyles();
		else
			print(getForeground(color));
	}

	public static void setBackground(RGB color) {
		if (color == null)
			resetStyles();
		else
			print(getBackground(color));
	}

	public static String getForeground(RGB color) {
		if (color == null) return "";
		return String.format("%s[38;2;%d;%d;%dm", ANSI.ESC, color.r, color.g, color.b);
	}

	public static String getBackground(RGB color) {
		if (color == null) return "";
		return String.format("%s[48;2;%d;%d;%dm", ANSI.ESC, color.r, color.g, color.b);
	}

	public static void setBold() {
		print(ANSI.TEXT_BOLD);
	}

	public static void setNormal() {
		print(ANSI.TEXT_NORMAL);
	}
}