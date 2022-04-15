package Utils;

/**
 * Useful static functions for math operations
 */
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