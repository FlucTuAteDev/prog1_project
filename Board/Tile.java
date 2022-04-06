package Board;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import Base.Console;
import Base.Console.Alignment;
import Units.Unit;
import View.View;

public class Tile {
	public static final int ROWS = 2;
	public static final int COLS = 2 * ROWS;
	public final int row;
	public final int col;
	public final View view;
	private Unit unit;
	private Set<Tile> neighbours;

	public Tile(int row, int col, View view) {
		this.row = row;
		this.col = col;
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

	public void setUnit(Unit unit) {
		if (unit == null) {
			if (this.unit != null)
				this.unit.setTile(null);
		}
		else
			unit.setTile(this);

		this.unit = unit;
	}

	public void draw() {
		if (this.hasUnit()) {
			Console.printAligned(Alignment.CENTER, COLS, "%s", this.unit.icon);
			Console.printAligned(Alignment.CENTER, COLS, "%d", this.unit.getCount());
		}
	}

	// public void draw()

	public static int distance(Tile a, Tile b) {
		return Math.max(Math.abs(a.row - b.row), Math.abs(a.col - b.col));
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
}
