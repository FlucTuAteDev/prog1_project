package Utils;

public class Position {
	public int row, col;

	public Position(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public void set(Position other) {
		this.row = other.row;
		this.col = other.col;
	}

	public void add(Position other) {
		this.row += other.row;
		this.col += other.col;
	}

	public static Position multiply(Position pos, int scalar) {
		return new Position(scalar * pos.row, scalar * pos.col);
	}
}
