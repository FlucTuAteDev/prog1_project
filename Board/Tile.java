package Board;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import Base.Console;
import Base.Game;
import Base.Console.Alignment;
import Interfaces.Drawable;
import Units.Unit;
import Utils.ThreadHelper;
import Utils.Vector;
import View.Colors.*;
import View.View;

public class Tile implements Drawable {
	public static final int ROWS = 2;
	public static final int COLS = 2 * ROWS;
	public static final RGB lightBg = Colors.WHITE;
	public static final RGB darkBg = Colors.BLACK;
	public static final RGB lightText = Colors.LIGHT_GRAY;
	public static final RGB darkText = Colors.GRAY;
	public final int row;
	public final int col;
	public final View view;
	public final Vector position;
	private Unit unit;
	private Set<Tile> neighbours;
	private String spacer =  " ".repeat(COLS);

	public Tile(int row, int col, View view) {
		this.row = row;
		this.col = col;
		this.position = new Vector(row, col);
		this.view = view;
		this.neighbours = new HashSet<>();
	}

	public Unit getUnit() {
		return unit;
	}

	public boolean hasUnit() {
		return unit != null;
	}

	public void addNeighbour(Tile neighbour) {
		if (this.equals(neighbour))
			return;

		this.neighbours.add(neighbour);
		neighbour.neighbours.add(this);
	}

	public Tile[] getNeighbours() {
		return neighbours.toArray(Tile[]::new);
	}

	public boolean isNeighbour(Tile other) {
		if (other == null) return false;
		return neighbours.contains(other);
	}

	public void setUnit(Unit unit) {
		if (unit == this.unit) return;
		
		Unit old = this.unit;
		this.unit = unit;


		if (old != null)
			old.setTile(null);
		
		if (unit != null)
			unit.setTile(this);
		
		draw();
	}

	public void setCursor(int rOffset, int cOffset) {
		view.setCursorPosition(row * ROWS + 1 + rOffset, col * COLS + 1 + cOffset);
	}
	public void setCursor() {
		setCursor(0, 0); // Default parameter buzis
	}

	public static int distance(Tile a, Tile b) {
		return Math.max(Math.abs(a.row - b.row), Math.abs(a.col - b.col));
	}

	public static double euclideanDistance(Tile a, Tile b) {
		return Math.sqrt(Math.pow(a.row - b.row, 2) + Math.pow(a.col - b.col, 2));
	}

	public static Vector direction(Tile a, Tile b) {
		return new Vector(a.row - b.row, a.col - b.col);
	}

	/**
	 * Puts {@code rows} with background {@code color} on the tile and holds it there for {@link Game.Constants#EFFECT_TIME EFFECT_TIME} ms
	 * After that it redraws the effected tiles
	 * @param tiles
	 * @param color
	 * @param rows
	 */
	public static void effect(Collection<Tile> tiles, RGB color, String... rows) {
		Colors.setBgWithFg(color);
		for (Tile tile : tiles)
			tile.draw(rows);
		Console.resetStyles();

		ThreadHelper.sleep(Game.Constants.EFFECT_TIME);
		for (Tile tile : tiles)
			tile.draw();
	}

	public void effect(RGB color, String... rows) {
		Tile.effect(List.of(this), color, rows);
	}
	
	/**
	 * Draws {@rows} into the tile
	 * 
	 * @throws IllegalArgumentException if more rows were passed than what the tile can fit
	 * @param rows
	 */
	public void draw(String... rows) {
		if (rows.length > ROWS)
			throw new IllegalArgumentException("Too many rows for this tile");

		for (int i = 0; i < rows.length; i++) {
			setCursor(i, 0);
			Console.printAligned(Alignment.CENTER, COLS, rows[i]);
		}

		for (int i = rows.length; i < ROWS; i++) {
			setCursor(i, 0);
			Console.print(spacer);
		}
	}

	@Override
	public void draw() {
		setCursor();
		if (this.hasUnit()) {
			this.unit.draw();
			return;
		}

		Console.setBackground(bgColor());
		Console.setForeground(fgColor());
		Console.printAligned(Alignment.CENTER, COLS, "%c%d", 'a' + col, row + 1);
		for (int i = 1; i < ROWS; i++) {
			setCursor(i, 0);
			Console.print(spacer);
		}
		Console.resetStyles();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || obj.getClass() != getClass())
			return false;

		Tile other = (Tile) obj;
		if (other.row == this.row && other.col == this.col)
			return true;

		return false;
	}
	@Override
	public int hashCode() {
		return Objects.hash(row, col);
	}

	@Override
	public String toString() {
		return String.format("%c%d", 'a' + col, row + 1);
	}

	private boolean isLight() {
		return (row + col) % 2 == 0;
	}
	private RGB bgColor() {
		return isLight() ? lightBg : darkBg;
	}
	private RGB fgColor() {
		return isLight() ? lightText : darkText;
	}
}
