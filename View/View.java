package View;

import Base.Console;
import Base.Console.Alignment;
import Base.Console.MoveDirection;

public class View {
	public final int top;
	public final int left;
	public final int width;
	public final int height;

	private final String eraser;

	public View(int top, int left, int width, int height) {
		this.top = top;
		this.left = left;
		this.width = width;
		this.height = height;

		this.eraser = " ".repeat(width);
	}

	public void setCursorPosItion(int row, int col) {
		Console.setCursorPosition(row + top, col + left);
	}
	
	public void home() {
		Console.setCursorPosition(top, left);
	}

	public void clear() {
		for (int i = 0; i < height; i++) {
			Console.setCursorPosition(top + i, left);
			Console.print(eraser);
		}

		home();
	}

	public void println(String format, Object... args) {
		Console.print(format, args);
		Console.setCursorCol(left);
		Console.moveCursor(MoveDirection.DOWN, 1);
	}

	public void printlnAligned(Alignment alignment, int width, String format, Object... args) {
		Console.printAligned(alignment, width, format, args);
		Console.setCursorCol(left);
		Console.moveCursor(MoveDirection.DOWN, 1);
	}

	public void printlnAligned(Alignment alignment, String format, Object... args) {
		Console.printAligned(alignment, this.width, format, args);
		Console.setCursorCol(left);
		Console.moveCursor(MoveDirection.DOWN, 1);
	}
}
