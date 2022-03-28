package Base;

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

	public static void print(Object o) {
		System.out.print(o);
	}

	public static void println(Object o) {
		System.out.println(o);
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
		print(ANSI.ESC + "[" + amount + direction.getChar());
	}

	public static void clearScreen() {
		try {
			if (System.getProperty("os.name").contains("Windows"))
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			else
				new ProcessBuilder("sh", "-c", "clear").inheritIO().start().waitFor();
		} catch (Exception e) {
			Console.println(e);
		}
		// print(ANSI.ERASE_SCREEN);
		// print(ANSI.HOME);
	}

	public static void clearLine() {
		print(ANSI.ERASE_LINE + "\r");
	}

	public static void clearLine(boolean cursorReturn) {
		print(ANSI.ERASE_LINE + (cursorReturn ? "\r" : ""));
	}

	/**
	 * Color manipulation
	 */
	public static void resetColors() {
		print(ANSI.RESET_COLORS);
	}

	public static void setForeground(RGB color) {
		print(ANSI.ESC + "[38;2;" + color.r + ";" + color.g + ";" + color.b + "m");
	}

	public static void setBackground(RGB color) {
		print(ANSI.ESC + "[48;2;" + color.r + ";" + color.g + ";" + color.b + "m");
	}

	public static void setBold() {
		print(ANSI.TEXT_BOLD);
	}

	public static void setNormal() {
		print(ANSI.TEXT_NORMAL);
	}
}