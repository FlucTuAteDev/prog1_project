package Base;

import java.util.function.Consumer;

import Utils.RGB;

public class Console {
	public class ANSI {
		static final String ESC = "\u001b";
		// static final String DEL = "\u007f";
		static final String HOME = ESC + "[H";
		static final String ERASE_SCREEN = ESC + "[2J";
		static final String RESET_COLORS = ESC + "[0m";
		static final String CURSOR_SAVE = ESC + " 7";
		static final String CURSOR_RESTORE = ESC + " 8";
		static final String CURSOR_REQUEST = ESC + "[6n";
		static final String ERASE_LINE = ESC + "[2K";
		static final String TEXT_BOLD = ESC + "[1m";
		static final String TEXT_NORMAL = ESC + "[0m";
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
		switch (alignment) {
			case LEFT:
				printFn.accept(s);
				break;
			case CENTER: {
				String out = String.format(
					"%-" + width + "s",
					String.format(
							"%" + (s.length() + (width - s.length()) / 2) + "s",
							s));
				printFn.accept(out);
			}
				break;
			case RIGHT: {
				String out = String.format("%-" + (width - s.length()) + "s", s);
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

	public static int alignCenter(int width, String text) {
		return (int) Math.floor(width / 2.0 - text.length() / 2.0);
	}

	public static String centerString(int width, String s) {
		return String.format(
				"%-" + width + "s",
				String.format(
						"%" + (s.length() + (width - s.length()) / 2) + "s",
						s));
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
			print(ANSI.ESC + "[38;2;" + color.r + ";" + color.g + ";" + color.b + "m");
	}

	public static void setBackground(RGB color) {
		if (color == null)
			resetStyles();
		else
			print(ANSI.ESC + "[48;2;" + color.r + ";" + color.g + ";" + color.b + "m");
	}

	public static void setBold() {
		print(ANSI.TEXT_BOLD);
	}

	public static void setNormal() {
		print(ANSI.TEXT_NORMAL);
	}
}