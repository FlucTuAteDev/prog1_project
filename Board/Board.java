package Board;

import java.util.Scanner;

import Base.Console;
import Base.Console.MoveDirection;
import Hero.Hero;
import Units.Archer;
import Units.Farmer;
import Units.Griff;
import Units.Unit;
import Utils.Colors;
import Utils.Maths;
import Utils.RGB;

public class Board {
	private static final int BOARD_COLS = 12;
	private static final int BOARD_ROWS = 10;
	private static final int CELL_ROWS = 2;
	private static final int CELL_COLS = CELL_ROWS * 2;
	private static final int BOARD_WIDTH = CELL_COLS * BOARD_COLS;
	private static final int BOARD_HEIGHT = CELL_ROWS * BOARD_ROWS;

	private final RGB light = Colors.WHITE;
	private final RGB textColor = Colors.RED;
	
	private final Hero user;
	private final Hero ai;

	private Unit[][] board = new Unit[BOARD_ROWS][BOARD_COLS];

	public Board(Hero user) {
		this.user = user;
		this.ai = new Hero(); // TODO: Random generation
	}

	public void drawBoard() {
		Console.clearScreen();
		for (int i = 0; i < BOARD_ROWS; i++) {
			for (int j = 0; j < CELL_ROWS; j++) { // Each row has to be the same color
				for (int k = 0; k < BOARD_COLS; k++) {
					Console.setBackground((k + i) % 2 == 0 ? Colors.WHITE : null);
					if (board[i][k] == null)
						Console.print(" ".repeat(CELL_COLS));
					else
						this.drawUnit(board[i][k], i, k);
				}
				Console.println("");
			}
			// Console.println("");
		}
		Console.resetColors();
	}

	public void drawLabels() {
		Console.resetColors();
		// ROWS
		char rowID = 'a';
		for (int i = 1; i <= BOARD_HEIGHT; i += CELL_ROWS) {
			Console.setCursorPosition(i, BOARD_WIDTH + 1);
			Console.print(rowID);
			rowID++;
		}

		// COLS
		int colID = 1;
		for (int i = 1; i < BOARD_WIDTH; i += CELL_COLS) {
			Console.setCursorPosition(BOARD_HEIGHT + 1, i);
			Console.print(String.format("%2d", colID));
			colID++;
		}
	}

	private void drawUnit(Unit unit, int row, int col) {
		if (!Maths.inRange(row, 0, BOARD_ROWS) || !Maths.inRange(col, 0, BOARD_COLS)) return;

		RGB color = (row + col) % 2 == 0 ? light : null;
		int screenRow = row * CELL_ROWS + 1;
		int screenCol = col * CELL_COLS + 1;
		Console.setCursorPosition(screenRow, screenCol);
		Console.setBackground(color);
		Console.setForeground(textColor);

		Console.setCursorCol(screenCol + Console.alignCenter(CELL_COLS, unit.icon));
		Console.print(unit.icon); 
		Console.moveCursor(MoveDirection.DOWN, 1);
		Console.setCursorCol(screenCol + Console.alignCenter(CELL_COLS, unit.getCount().toString()));
		Console.print(unit.getCount());
		// Console.print();

		Console.resetColors();
	}

	private void placeUnits() {
		Console.setCursorPosition(BOARD_HEIGHT + 3, 0);
		
	}

	private void setUnit(Unit unit, int row, int col) {
		if (!Maths.inRange(row, 0, BOARD_ROWS) || !Maths.inRange(col, 0, BOARD_COLS)) return;

		this.board[row][col] = unit;
	}
}
