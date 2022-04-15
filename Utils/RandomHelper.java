package Utils;

import java.util.List;
import java.util.Random;

/**
 * A wrapper for {@link Random} with static functions so that I don't need to {@code new Random()} everywhere
 */
public class RandomHelper {
	private static Random random = new Random();

	/**
	 * Gets a random element of the given list
	 * @param <T> Type of the list
	 * @param list
	 */
	public static <T> T getRandomElement(List<T> list) {
		return list.get(random.nextInt(list.size()));
	}

	public static int getInt(int end) {
		return getInt(0, end);
	}
	public static int getInt(int start, int end) {
		return random.nextInt(start, end);
	}
}
