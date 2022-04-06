package Board;

import Base.Console;
import Hero.Hero;
import Units.*;
import Utils.*;
import View.View;

public class Board {
	public static final int ROWS = 10;
	public static final int CELL_ROWS = 2;
	public static final int HEIGHT = CELL_ROWS * ROWS;
	public static final int COLS = 12;
	public static final int CELL_COLS = CELL_ROWS * 2;
	public static final int WIDTH = CELL_COLS * COLS;
	public static final int BOARD_OFFSET = Console.WIDTH / 2 - WIDTH / 2;
	public static final View view = new View(0, BOARD_OFFSET, WIDTH, HEIGHT);

	private final RGB lightBg = Colors.WHITE;
	private final RGB darkBg = Colors.BLACK;

	private final Hero player;
	private final Hero ai;

	private Tile[][] board = new Tile[ROWS][COLS];

	public Board(Hero player, Hero ai) {
		this.player = player;
		this.ai = ai;

		// Generate tiles
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				board[i][j] = new Tile(i, j, view);
			}
		}

		// Add neighbours
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				for (int k = Math.max(i - 1, 0); k <= Math.min(i + 1, ROWS - 1); k++) {
					for (int l = Math.max(j - 1, 0); l <= Math.min(j + 1, COLS - 1); l++) {
						board[i][j].addNeighbour(board[k][l]);
					}	
				}
			}
		}
	}

	public void drawBoard() {
		Console.clearScreen();
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				board[i][j].draw();
				clearCell(i, j);

				if (board[i][j].hasUnit())
					this.drawUnit(board[i][j].getUnit(), i, j);
			}
			Console.println("");
		}

		Console.resetStyles();
	}

	public boolean drawUnit(Unit unit, int row, int col) {
		if (!isValidPos(row, col))
			return false;

		board[row][col].setUnit(unit);

		redrawUnit(unit);

		return true;
	}

	public void redrawUnit(Unit unit) {
		Hero hero = unit.hero;
		Tile tile = unit.getTile();
		Console.setBackground(hero.COLOR);
		Console.setForeground(Colors.textFromBg(hero.COLOR));

		setCursorTo(tile.row, tile.col);
		Console.printAligned(Console.Alignment.CENTER, CELL_COLS, unit.icon);
		setCursorTo(tile.row, tile.col, 1, 0);
		Console.printAligned(Console.Alignment.CENTER, CELL_COLS, "%d", unit.getCount());
		Console.resetStyles();
	}

	/**
	 * 
	 * @param cMin inclusive
	 * @param cMax exclusive
	 * @param rMin inclusive
	 * @param rMax exclusive
	 * @return
	 */
	// public Tile scanPosition(int cMin, int cMax, int rMin, int rMax) {
	// 	// User input can range from min + 1 to max (1 based index)
	// 	// Position is returned from min to max - 1 (0 based index)
	// 	char firstCol = (char) ('a' + cMin);
	// 	char lastCol = (char) ('a' + cMax - 1);

	// 	return (Tile) IO.scanAndConvert(
	// 			String.format("Cella [%c%d - %c%d]", firstCol, rMin + 1, lastCol, rMax + 1),
	// 			(String in) -> {
	// 				char c = in.charAt(0);
	// 				int r = Integer.parseInt(in.substring(1));

	// 				if (c < firstCol || c > lastCol || r < rMin + 1 || r > rMax)
	// 					throw new Exception("Hibás bemenet!");

	// 				return board[r - 1][c - 'a'];
	// 			}).get(0);
	// }

	// public Tile scanPosition() {
	// 	return scanPosition(0, BOARD_COLS, 0, BOARD_ROWS);
	// }

	public void setColors(Unit unit) {
		Hero hero = unit.hero;

		Console.setBackground(hero.COLOR);
		Console.setForeground(Colors.textFromBg(hero.COLOR));
	}

	/**
	 * Moves the unit to the given position
	 * s
	 * @return If the move was successful
	 */
	public boolean moveUnit(Unit unit, int row, int col) {
		Tile to = board[row][col];
		if (!isValidPos(row, col) || !unit.canMoveTo(to))
			return false;
		
		Tile tile = unit.getTile();
		clearCell(tile.row, tile.col);
		drawUnit(unit, row, col);

		return true;
	}

	public Tile getTile(int row, int col) {
		return board[row][col];
	}

	private void setCursorTo(int row, int col) {
		setCursorTo(row, col, 0, 0);
	}

	private void setCursorTo(int row, int col, int rOffset, int cOffset) {
		int screenRow = row * CELL_ROWS + 1 + rOffset;
		int screenCol = col * CELL_COLS + 1 + cOffset + BOARD_OFFSET;

		Console.setCursorPosition(screenRow, screenCol);
	}

	private boolean isValidPos(int row, int col) {
		if (!Maths.inRange(row, 0, ROWS) || !Maths.inRange(col, 0, COLS))
			return false;

		if (board[row][col].hasUnit())
			return false;

		return true;
	}

	private void clearCell(int row, int col) {
		board[row][col].setUnit(null);

		Console.setBackground(isLight(row, col) ? lightBg : darkBg);
		Console.setForeground(isLight(row, col) ? Colors.LIGHT_GRAY : Colors.GRAY);
		setCursorTo(row, col, 0, 0);
		Console.printAligned(Console.Alignment.CENTER, CELL_COLS, "%c%d", 'a' + col, row + 1);
		

		for (int i = 1; i < CELL_ROWS; i++) {
			setCursorTo(row, col, i, 0);
			Console.print(" ".repeat(CELL_COLS));
		}
	}

	private boolean isLight(int row, int col) {
		return (row + col) % 2 == 0;
	}
}
