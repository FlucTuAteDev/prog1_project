package Board;

import Base.Console;
import Hero.Hero;
import Units.*;
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
			}
			Console.println("");
		}

		Console.resetStyles();
	}

	public void setColors(Unit unit) {
		Hero hero = unit.hero;

		Console.setBackground(hero.COLOR);
		Console.setForeground(hero.TEXT_COLOR);
	}

	public Tile getTile(int row, int col) {
		return board[row][col];
	}
}
