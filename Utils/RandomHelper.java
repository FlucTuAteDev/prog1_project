package Utils;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RandomHelper {
	private static Random random = new Random();

	public static <T> T getRandomElement(List<T> list) {
		return list.get(random.nextInt(list.size()));
	}
}
