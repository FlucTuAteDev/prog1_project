package Board;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import Units.Unit;

public class Tile {
	public final int row;
	public final int col;
	private Unit unit;
	private Set<Tile> neighbours;

	public Tile(int row, int col) {
		this.row = row;
		this.col = col;
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
