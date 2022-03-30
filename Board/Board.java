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
			for (int j = 0; j < BOARD_COLS; j++) {
				int screenRow = i * CELL_ROWS + 1;
				int screenCol = j * CELL_COLS + 1;
				Console.setBackground((i + j) % 2 == 0 ? Colors.WHITE : null);
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
		Console.setCursorPosition(BOARD_HEIGHT + 1, 0);
		for (int i = 1; i < BOARD_WIDTH; i += CELL_COLS) {
			Console.setCursorCol(i);
			Console.print(String.format("%2d", colID));
			colID++;
		}
	}

	private void drawUnit(Unit unit, int row, int col) {
		if (!Maths.inRange(row, 0, BOARD_ROWS) || !Maths.inRange(col, 0, BOARD_COLS))
			return;

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

	public void placeUnits() {
		List<Object> values = new ArrayList<>();
		
		for (var kv : user.getUnits()) {
			Unit unit = kv.getValue();
			// if (unit.getCount().get() == 0) continue;
			
			char lastChar = 'a' + BOARD_ROWS - 1;
			
			Console.setCursorPosition(BOARD_HEIGHT + 3, 0);
			Console.println(String.format("Válaszd ki, hogy hova rakod: %s (%s, %s db)", unit.name, unit.icon,
					unit.getCount()));
			values = IO.scanAndConvert(String.format("[%c - %c, %d - %d]", 'a', lastChar, 1, BOARD_COLS),
					x -> {
						char r = x.charAt(0);
						if (x.length() != 1 || r < 'a' || r > lastChar)
							throw new Exception();

						return r;
					},
					x -> {
						int a = Integer.parseInt(x);
						if (!Maths.inRange(a, 1, BOARD_COLS + 1))
							throw new Exception();

						return a;
					});

			int row = (int) ((char) values.get(0) - 'a');
			int col = (int) values.get(1) - 1;

			board[row][col] = unit;
			this.drawBoard();
		}
	}
}
