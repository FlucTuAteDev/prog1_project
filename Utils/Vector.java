package Utils;

public class Vector {
	public int row, col;

	public Vector(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public void set(Vector other) {
		this.row = other.row;
		this.col = other.col;
	}

	public Vector add(Vector other) {
		// this.row += other.row;
		// this.col += other.col;
		return new Vector(this.row + other.row, this.col + other.col);
	}

	public Vector sub(Vector other) {
		return new Vector(this.row - other.row, this.col - other.col);
	}

	public Vector inverse() {
		return new Vector(-this.row, -this.col);
	}

	public Vector times(int scalar) {
		return new Vector(this.row * scalar, this.col * scalar);
	}
	// public static Position multiply(Position pos, int scalar) {
	// 	return new Position(scalar * pos.row, scalar * pos.col);
	// }
}
