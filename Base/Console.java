package Base;

import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import Board.Board;
import Board.Tile;
import Utils.Maths;
import Utils.Position;
import Utils.Functions.ConverterFunction;
import View.View;
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
	private static Position savedPosition = new Position(0, 0);
	private static Position currentPosition = new Position(0, 0);

	public static enum Alignment {
		LEFT, CENTER, RIGHT
	};

	private static void printBase(Alignment alignment, int width, Consumer<String> printFn, String format,
			Object... args) {
		String str = String.format(format, args);
		String stripped = stripAnsi(str);

		String out = "";
		switch (alignment) {
			case LEFT:
				out = str;
				printFn.accept(str);
				break;
			case CENTER: {
				int spaceLen = width - (int)stripped.length();
				String spaces = " ".repeat(spaceLen / 2);
				String pad = spaceLen % 2 == 0 ? "" : " ";

				out = spaces + str + spaces + pad;
				// out = String.format("%" + width + "." + width + "s", out);
				printFn.accept(out);
			}
				break;
			case RIGHT: {
				String spaces = " ".repeat((width - stripped.length()));
				out = String.format("%s%s", spaces, str);
				printFn.accept(out);
			}
				break;
		}

		currentPosition.col += out.length();
	}

	public static void print(String format, Object... args) {
		printBase(Alignment.LEFT, 0, System.out::print, format, args);
	}

	public static void printAligned(Alignment alignment, int width, String format, Object... args) {
		printBase(alignment, width, System.out::print, format, args);
	}

	public static void println(String format, Object... args) {
		printlnAligned(Alignment.LEFT, 0, format, args);
	}

	public static void printlnAligned(Alignment alignment, int width, String format, Object... args) {
		printBase(alignment, width, System.out::println, format, args);
		currentPosition.row += 1;
		currentPosition.col = 0;
	}

	/**
	 * Cursor control
	 */

	public static enum MoveDirection {
		UP("A", -1, 0),
		DOWN("B", 1, 0),
		RIGHT("C", 0, 1),
		LEFT("D", 0, -1);

		private String ansiDir;
		private Position dir;

		MoveDirection(String ansiDir, int row, int col) {
			this.ansiDir = ansiDir;
			this.dir = new Position(row, col);
		}

		public String getChar() {
			return ansiDir;
		}

		public Position getDirection() {
			return dir;
		}
	}

	public static void setCursorCol(int col) {
		print(ANSI.ESC + "[" + col + "G");
		currentPosition.col = col;
	}

	public static void setCursorPosition(int row, int col) {
		print(ANSI.ESC + "[" + row + ";" + col + "H");
		currentPosition.row = row;
		currentPosition.col = col;
	}

	public static void setCursorPosition(Position pos) {
		setCursorPosition(pos.row, pos.col);
	}

	public static void moveCursor(MoveDirection direction, int amount) {
		if (amount < 1)
			return;
		print(ANSI.ESC + "[" + amount + direction.getChar());

		// curr += dir * amt. Operator overloading... 🙄
		currentPosition.add(Position.multiply(direction.getDirection(), amount));
	}

	public static void saveCursor() {
		savedPosition.set(currentPosition);
	}

	public static void restoreCursor() {
		Console.setCursorPosition(savedPosition);
	}

	/**
	 * Clearing
	 */

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

	public static void setBold() {
		print(ANSI.TEXT_BOLD);
	}

	public static void setNormal() {
		print(ANSI.TEXT_NORMAL);
	}

	/**
	 * Input
	 */

	private static Scanner sc = new Scanner(System.in);

	/**
	 * Snans a value from the standard input and converts it to T
	 * 
	 * @param text      Text to place before aksing for input
	 * @param converter Converts the read string to T
	 * @param filters   Function that gets the read input as T. Returns null if
	 *                  the value is okay and returns an error string if it should
	 *                  be filtered
	 * @return The scanned value converted to T
	 */
	@SafeVarargs
	public static <T> T scanAndConvert(String text, View view, ConverterFunction<String, T> converter, Function<T, String>... filters) {
		T res = null;

		view.clear();
		Console.saveCursor();
		while (true) {
			Console.print("%s: ", text);
			try {
				String current = sc.nextLine();
				currentPosition.row += 1; // After pressing [Enter] the cursor will be on the next line
				currentPosition.col = 0;

				res = converter.apply(current);
				for (var filter : filters) {
					String error = filter.apply(res);
					if (error != null)
						throw new Exception(error);
				}
				break;
			} catch (Exception e) {
				Game.logError(e.getMessage());
				Console.restoreCursor();

				view.clear();
			}
		}
		Game.clearError();

		return res;
	}

	@SafeVarargs
	public static <T> T scanAndConvert(String text, ConverterFunction<String, T> converter, Function<T, String>... filters) {
			return scanAndConvert(text, Game.inputView, converter, filters);
	}

	@SafeVarargs
	public static Tile scanTile(int rMin, int rMax, int cMin, int cMax, Function<Tile, String>... filters) {
		// User input can range from min + 1 to max (1 based index)
		// Position is returned from min to max - 1 (0 based index)
		char firstCol = (char) ('a' + cMin);
		char lastCol = (char) ('a' + cMax - 1);

		return scanAndConvert(
				String.format("Cella [%c%d - %c%d]", firstCol, rMin + 1, lastCol, rMax),
				(String in) -> {
					char col = 'a';
					int row = 0;
					try {
						col = in.charAt(0);
						row = Integer.parseInt(in.substring(1));

						if (!Character.isLetter(col))
							throw new Exception();
					} catch (Exception e) {
						throw new Exception("A bemenet nem egy cella!");
					}

					if (col < firstCol || col > lastCol || row < rMin + 1 || row > rMax)
						throw new Exception("A cella a határokon kívül esik!");

					Tile tile = Game.board.getTile(row - 1, col - 'a');
					return tile;
				}, filters);
	}

	@SafeVarargs
	public static Tile scanTile(Function<Tile, String>... filters) {
		return scanTile(0, Board.ROWS, 0, Board.COLS, filters);
	}

	public static int scanInt(String text, int min, int max) {
		return scanAndConvert(String.format("%s [%d - %d]", text, min, max),
				(String in) -> {
					int parsed = 0;
					try {
						parsed = Integer.parseInt(in);
						return parsed;
					} catch (NumberFormatException e) {
						throw new Exception("A bemenet nem egy szám!");
					}
				},
				x -> !Maths.inRange(x, min, max + 1) ? "A szám a határokon kívül esik" : null);
	}

	/**
	 * Strings
	 */

	public static String getForeground(RGB color) {
		if (color == null)
			return "";
		return String.format("%s[38;2;%d;%d;%dm", ANSI.ESC, color.r, color.g, color.b);
	}

	public static String getBackground(RGB color) {
		if (color == null)
			return "";
		return String.format("%s[48;2;%d;%d;%dm", ANSI.ESC, color.r, color.g, color.b);
	}

	public static String stripAnsi(String str) {
		return str.replaceAll("\u001B\\[[\\d;]*[^\\d;]", "");
	}
}