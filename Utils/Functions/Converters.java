package Utils.Functions;

import Utils.Maths;

/**
 * Useful converters for {@link Base.Console#scanAndConvert(String, View.View, ConverterFunction, java.util.function.Function...) scanAndConvert}
 */
public class Converters {
	public static ConverterFunction<String, Object> convertInt(int min, int max) {
		return x -> {
			int parsed = Integer.parseInt(x);
			if (!Maths.inRange(parsed, min, max))
				throw new IndexOutOfBoundsException();

			return parsed;
		};
	}

	public static ConverterFunction<String, Object> convertChar(char min, char max) {
		return x -> {
			char r = x.charAt(0);
			if (x.length() != 1 || r < min || r > max)
				throw new Exception();

			return r;
		};
	}
}
