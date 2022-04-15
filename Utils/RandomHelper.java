package Utils;

import java.util.List;
import java.util.Random;

public class RandomHelper {
	private static Random random = new Random();

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
