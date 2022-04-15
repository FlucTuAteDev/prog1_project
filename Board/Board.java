package Board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import Base.Console;
import Interfaces.Drawable;
import Utils.Vector;
import View.View;

/**
 * Handles the state of the game board
 */
public class Board implements Drawable {
	public static final int ROWS = 10;
	public static final int CELL_ROWS = 2;
	public static final int HEIGHT = CELL_ROWS * ROWS;
	public static final int COLS = 12;
	public static final int CELL_COLS = CELL_ROWS * 2;
	public static final int WIDTH = CELL_COLS * COLS;
	public static final int BOARD_OFFSET = Console.WIDTH / 2 - WIDTH / 2;
	public static final View view = new View(0, BOARD_OFFSET, WIDTH, HEIGHT);

	private Tile[][] board = new Tile[ROWS][COLS];

	public Board() {
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

	public Tile getTile(int row, int col) {
		return board[row][col];
	}

	public Tile getTile(Vector pos) {
		return board[pos.row][pos.col];
	}

	/**
	 * Determines which tile is closer to the destination
	 * @param tile
	 * @param dest
	 * @return The euclidean distance between {@code tile} and {@code dest}
	 */
	private double heuristic(Tile tile, Tile dest) {
		return Tile.euclideanDistance(tile, dest);
	}

	/**
	 * Finds the shortest path between {@code start} and {@code dest} using the A* algorithm
	 * @param start Start tile
	 * @param dest Destination tile
	 * @return The shortest path as {@code List<Tile>}
	 */
	public List<Tile> findPath(Tile start, Tile dest) {
		// Stores the previous tile of a tile on the shortest known pathó
		Map<Tile, Tile> cameFrom = new HashMap<>();

		// How far we are from the start
		Map<Tile, Integer> gScore = new HashMap<>();
		gScore.put(start, 0);

		// F = G + heuristic
		Map<Tile, Double> fScore = new HashMap<>();
		fScore.put(start, heuristic(start, dest));

		// Queue sorted by the F scores of the tiles
		PriorityQueue<Tile> openSet = new PriorityQueue<>(Comparator.comparingDouble(fScore::get));
		openSet.add(start);
		
		while (!openSet.isEmpty()) {
			// Expand best tile
			Tile current = openSet.poll();

			if (current == dest) {
				List<Tile> path = new ArrayList<>();
				path.add(dest);

				Set<Tile> keys = cameFrom.keySet();
				while (keys.contains(current)) {
					current = cameFrom.get(current);
					path.add(current);
				}

				path.remove(start);
				Collections.reverse(path);

				return path;
				// return reconstructPath(cameFrom, dest);
			}

			for (Tile neighbour : current.getNeighbours()) {
				if (neighbour.hasUnit() && neighbour != dest) continue; // Obstacle

				int tentativeG = gScore.getOrDefault(current, Integer.MAX_VALUE) + 1; // +1 -> all neighbours have 1 cost
				int neighbourG = gScore.getOrDefault(neighbour, Integer.MAX_VALUE);
				if (tentativeG < neighbourG) {
					cameFrom.put(neighbour, current);
					gScore.put(neighbour, tentativeG);
					fScore.put(neighbour, tentativeG + heuristic(neighbour, dest));

					if (!openSet.contains(neighbour))
						openSet.add(neighbour);
				}
			}
		}

		return null;
	}

	@Override
	public void draw() {
		Console.clearScreen();
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				board[i][j].draw();
			}
			Console.println("");
		}

		Console.resetStyles();
	}
}
