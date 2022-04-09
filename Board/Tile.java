package Board;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import Base.Console;
import Base.Console.Alignment;
import Units.Unit;
import View.Colors.*;
import View.Drawable;
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
	private Unit unit;
	private Set<Tile> neighbours;
	private String spacer =  " ".repeat(COLS);

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
		view.setCursorPosItion(row * ROWS + 1 + rOffset, col * COLS + 1 + cOffset);
	}
	public void setCursor() {
		setCursor(0, 0); // Default parameter buzis
	}

	public static int distance(Tile a, Tile b) {
		return Math.max(Math.abs(a.row - b.row), Math.abs(a.col - b.col));
	}

	public void draw(String... rows) {
		if (rows.length > ROWS)
			return;

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
