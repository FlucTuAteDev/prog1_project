package Board;

import java.util.ArrayList;
import java.util.List;

import Base.Console;
import Base.Console.MoveDirection;
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

	private final RGB lightBg = Colors.LIGHT_GRAY;
	private final RGB darkBg = Colors.DARK_GRAY;

	private final RGB textOnLight = null;
	private final RGB textOnDark = null;

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
			for (int j = 0; j < BOARD_COLS; j++) {
				int screenRow = i * CELL_ROWS + 1;
				int screenCol = j * CELL_COLS + 1;
				Console.setBackground((i + j) % 2 == 0 ? lightBg : darkBg);
				Console.setCursorPosition(screenRow, screenCol);
				Console.print(" ".repeat(CELL_COLS));

				Console.setCursorPosition(screenRow + 1, screenCol);
				Console.print(" ".repeat(CELL_COLS));

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
			Console.setCursorPosition(i, BOARD_WIDTH + 1);
			Console.print(String.format("%2d", rowID));
			rowID++;
		}

		// ROWS
		char colID = 'a';
		Console.setCursorPosition(BOARD_HEIGHT + 1, 0);
		for (int i = 1; i < BOARD_WIDTH; i += CELL_COLS) {
			Console.setCursorCol(i);
			Console.print(colID);
			colID++;
		}
	}

	private void drawUnit(Unit unit, int row, int col) {
		if (!Maths.inRange(row, 0, BOARD_ROWS) || !Maths.inRange(col, 0, BOARD_COLS))
			return;

		int screenRow = row * CELL_ROWS + 1;
		int screenCol = col * CELL_COLS + 1;
		Console.setCursorPosition(screenRow, screenCol);

		RGB bg = (row + col) % 2 == 0 ? lightBg : darkBg;
		RGB fg = (row + col) % 2 == 0 ? textOnLight : textOnDark;
		Console.setBackground(bg);
		Console.setForeground(fg);

		Console.setCursorCol(screenCol + Console.alignCenter(CELL_COLS, unit.icon));
		Console.print(unit.icon);
		Console.moveCursor(MoveDirection.DOWN, 1);
		Console.setCursorCol(screenCol + Console.alignCenter(CELL_COLS, unit.getCount().toString()));
		Console.print(unit.getCount());
		// Console.print();

		Console.resetStyles();
	}

	public void placeUnits() {
		List<Object> values = new ArrayList<>();

		for (var kv : user.getUnits()) {
			Unit unit = kv.getValue();
			// if (unit.getCount().get() == 0) continue;

			char lastChar = 'a' + BOARD_COLS - 1;

			Console.setCursorPosition(BOARD_HEIGHT + 3, 0);
			Console.println(String.format("Válaszd ki, hogy hova rakod: %s (%s, %s db)", unit.name, unit.icon,
					unit.getCount()));
			values = IO.scanAndConvert(String.format("[Sor: %d - %d, Oszlop: %c - %c]", 1, BOARD_ROWS, 'a', lastChar),
					Converters.convertInt(1, BOARD_COLS + 1),
					x -> {
						char r = x.charAt(0);
						if (x.length() != 1 || r < 'a' || r > lastChar)
							throw new Exception();

						return r;
					});

			int row = (int) values.get(0) - 1;
			int col = (int) ((char) values.get(1) - 'a');

			board[row][col] = unit;
			this.drawBoard();
		}
	}
}
