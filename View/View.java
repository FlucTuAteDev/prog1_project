package View;

import Base.Console;

public class View {
	public final int row;
	public final int col;
	public final int width;
	public final int height;

	private final String eraser;

	public View(int row, int col, int width, int height) {
		this.row = row;
		this.col = col;
		this.width = width;
		this.height = height;

		this.eraser = " ".repeat(width);
	}
	
	public void home() {
		Console.setCursorPosition(row, col);
	}

	public void clear() {
		for (int i = 0; i < height; i++) {
			Console.setCursorPosition(row + i, col);
			Console.print(eraser);
		}

		home();
	}
}
