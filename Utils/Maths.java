package Utils;

public final class Maths {
	/**
	 * Checks if the value is in the given range
	 * @param value 
	 * @param min (inclusive)
	 * @param max (exclusive)
	 */
	public static boolean inRange(int value, int min, int max) {
		return value >= min && value < max;
	}
}