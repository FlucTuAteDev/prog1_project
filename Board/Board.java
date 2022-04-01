package Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import Base.Console;
import Hero.Hero;
import Units.*;
import Utils.Colors;
import Utils.IO;
import Utils.Maths;
import Utils.RGB;
import Utils.Functions.Converters;

public class Board {
	private static final int BOARD_COLS = 12;
	private static final int BOARD_ROWS = 10;
	private static final int CELL_ROWS = 2;
	private static final int CELL_COLS = CELL_ROWS * 2;
	private static final int BOARD_WIDTH = CELL_COLS * BOARD_COLS;
	private static final int BOARD_HEIGHT = CELL_ROWS * BOARD_ROWS;
	private static final int BOARD_OFFSET = Console.WIDTH / 2 - BOARD_WIDTH / 2;

	private final RGB lightBg = Colors.WHITE;
	private final RGB darkBg = Colors.BLACK;

	private final RGB textOnLight = Colors.BLACK;
	private final RGB textOnDark = Colors.WHITE;

	private final Hero user;
	private final Hero ai;

	SortedSet<Unit> units = new TreeSet<>();
	private Unit[][] board = new Unit[BOARD_ROWS][BOARD_COLS];

	public Board(Hero user) {
		this.user = user;
		this.ai = new Hero(); // TODO: Random generation
		
		// DEBUG ONLY
		for (var u : ai.getUnits()) {
			String name = u.getKey();
			Unit unit = u.getValue();

			unit.setCount(50);
		}
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
		this.drawLabels();
	}

	private void drawLabels() {
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

	private void drawUnit(Unit unit, int row, int col) {
		if (!Maths.inRange(row, 0, BOARD_ROWS) || !Maths.inRange(col, 0, BOARD_COLS))
			return;

		units.add(unit);
		board[row][col] = unit;

		setCursorTo(row, col, 0, Console.alignCenter(CELL_COLS, unit.icon));
		Console.print(unit.icon);
		setCursorTo(row, col, 1, Console.alignCenter(CELL_COLS, String.valueOf(unit.getCount())));
		Console.print(unit.getCount());

		Console.resetStyles();
	}

	public void placeUnits() {
		// Asks the user where to draw each unit
		for (Unit unit : user.getUnitValues()) {
			// if (unit.getCount().get() == 0) continue;

			char lastPickableCol = 'a' + 1;

			Console.setCursorPosition(BOARD_HEIGHT + 3, 0);
			Console.clearLine();
			Console.println(String.format("Válaszd ki, hogy hova rakod: %s (%s, %s db)", unit.name, unit.icon,
					unit.getCount()));

			List<Object> values = IO.scanAndConvert(
					String.format("[Oszlop: %c - %c, Sor: %d - %d]", 'a', lastPickableCol, 1, BOARD_ROWS),
					Converters.convertChar('a', lastPickableCol),
					Converters.convertInt(1, BOARD_COLS + 1));

			int col = (int) ((char) values.get(0) - 'a');
			int row = (int) values.get(1) - 1;

			board[row][col] = unit;
			this.drawUnit(unit, row, col);
		}

		Random rand = new Random();
		for (Unit unit : ai.getUnitValues()) {
			int row, col;
			do {
				row = rand.nextInt(BOARD_ROWS);
				col = rand.nextInt(BOARD_COLS - 2, BOARD_COLS);
			} while (board[row][col] != null);

			board[row][col] = unit;
			this.drawUnit(unit, row, col);
		}
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
