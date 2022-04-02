package Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import Base.Console;
import Hero.Hero;
import Units.*;
import Utils.*;

public class Board {
	public static final int BOARD_COLS = 12;
	public static final int BOARD_ROWS = 10;
	public static final int CELL_ROWS = 2;
	public static final int CELL_COLS = CELL_ROWS * 2;
	public static final int BOARD_WIDTH = CELL_COLS * BOARD_COLS;
	public static final int BOARD_HEIGHT = CELL_ROWS * BOARD_ROWS;
	public static final int BOARD_OFFSET = Console.WIDTH / 2 - BOARD_WIDTH / 2;

	private final RGB lightBg = Colors.WHITE;
	private final RGB darkBg = Colors.BLACK;

	private final RGB textOnLight = Colors.BLACK;
	private final RGB textOnDark = Colors.WHITE;

	private final Hero player;
	private final Hero ai;

	private Unit[][] board = new Unit[BOARD_ROWS][BOARD_COLS];

	public Board(Hero player, Hero ai) {
		this.player = player;
		this.ai = ai;
	}

	private void setCursorTo(int row, int col) {
		setCursorTo(row, col, 0, 0);
	}

	private void setCursorTo(int row, int col, int rOffset, int cOffset) {
		int screenRow = row * CELL_ROWS + 1 + rOffset;
		int screenCol = col * CELL_COLS + 1 + cOffset + BOARD_OFFSET;

		Console.setBackground(isLight(row, col) ? lightBg : darkBg);
		Console.setForeground(isLight(row, col) ? textOnLight : textOnDark);

		Console.setCursorPosition(screenRow, screenCol);
	}

	private boolean isLight(int row, int col) {
		return (row + col) % 2 == 0;
	}

	public void drawBoard() {
		Console.clearScreen();
		for (int i = 0; i < BOARD_ROWS; i++) {
			for (int j = 0; j < BOARD_COLS; j++) {
				clearCell(i, j);

				if (board[i][j] != null)
					this.drawUnit(board[i][j], i, j);
			}
			Console.println("");
		}

		// Draw labels
		Console.resetStyles();
		// COLS
		int rowID = 1;
		for (int i = 1; i <= BOARD_HEIGHT; i += CELL_ROWS) {
			Console.setCursorPosition(i, BOARD_OFFSET - 1);
			Console.print(rowID);
			rowID++;
		}

		// ROWS
		char colID = 'a';
		Console.setCursorPosition(BOARD_HEIGHT + 1, 0);
		for (int i = 1; i < BOARD_WIDTH; i += CELL_COLS) {
			Console.setCursorCol(i + BOARD_OFFSET);
			Console.print(colID);
			colID++;
		}
	}

	public boolean drawUnit(Unit unit, int row, int col) {
		if (!Maths.inRange(row, 0, BOARD_ROWS) || !Maths.inRange(col, 0, BOARD_COLS))
			return false;

		if (board[row][col] != null)
			return false;

		board[row][col] = unit;

		setCursorTo(row, col, 0, Console.alignCenter(CELL_COLS, unit.icon));
		Console.print(unit.icon);
		setCursorTo(row, col, 1, Console.alignCenter(CELL_COLS, String.valueOf(unit.getCount())));
		Console.print(unit.getCount());

		Console.resetStyles();
		return true;
	}

	/**
	 * 
	 * @param cMin inclusive
	 * @param cMax exclusive
	 * @param rMin inclusive
	 * @param rMax exclusive
	 * @return
	 */
	public static Position scanPosition(int cMin, int cMax, int rMin, int rMax) {
		// User input can range from min + 1 to max (1 based index)
		// Position is returned from min to max - 1 (0 based index)
		char firstCol = (char) ('a' + cMin);
		char lastCol = (char) ('a' + cMax - 1);
		// rMin = rMin + 1;

		return (Position) IO.scanAndConvert(
				String.format("[Oszlop: %c - %c Sor: %d - %d]", firstCol, lastCol, rMin + 1, rMax),
				(String in) -> {
					char c = in.charAt(0);
					int r = Integer.parseInt(in.substring(1));

					if (c < firstCol || c > lastCol || r < rMin + 1 || r > rMax)
						throw new Exception();

					return new Position(r - 1, c - 'a');
				}).get(0);
	}

	private void clearCell(int row, int col) {
		for (int i = 0; i < CELL_ROWS; i++) {
			setCursorTo(row, col, i, 0);
			Console.print(" ".repeat(CELL_COLS));
		}
	}

	public void moveUnit(Unit unit, int row, int col) {

	}
}
