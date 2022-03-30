package Utils.Functions;

import Utils.Maths;

public class Converters {
	public static ConverterFunction<String, Object> convertInt(int min, int max) {
		return x -> {
			int parsed = Integer.parseInt(x);
			if (!Maths.inRange(parsed, min, max))
				throw new Exception();

			return parsed;
		};
	}
}
