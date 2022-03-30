package Utils;

import Base.Console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import Utils.Functions.ConverterFunction;

public class IO {
	static Scanner sc = new Scanner(System.in);

	public static int scanInt(String text, int min, int max) {
		Console.print(text + " [" + min + " - " + max + "]: ");
		int selected = -1;
		while(true) {
			try {
				selected = Integer.parseInt(sc.nextLine());

				// Only quit if input is in the given range
				if (Maths.inRange(selected, min, max + 1))
					break;
				
				throw new Exception();
			} catch (Exception e) {
				Console.moveCursor(Console.MoveDirection.UP, 1);
				Console.clearLine();
				Console.resetColors();
				Console.print("Hibás bemenet! Kérem válasszon újra! [" + min + " - " + max + "]: ");
			} finally {
				// sc.nextLine();
			}
		}

		Console.moveCursor(Console.MoveDirection.UP, 1);
		Console.clearLine();

		return selected;
	}

	/**
	 * Scans n comma separated objects given by the number of converter functions
	 * @param text Text to place before aksing for input
	 * @param converters 
	 * Each function specifies how to convert the n-th scanned element to the desired type.
	 * If the conversion fails the converter should throw an exception.
	 * If it succeeds the converter should return the converted value.
	 * @return 
	 */
	@SafeVarargs
	public static List<Object> scanAndConvert(String text, ConverterFunction<String, Object>... converters) {
		List<Object> res = new ArrayList<>();
		int amt = converters.length;

		Console.print(String.format("%s: ", text));
		while(true) {
			try {
				String current = sc.nextLine();
				// Splits the string at the ','-s, trims them and takes the ones that are not empty
				List<String> vals = Arrays.stream(current.split(","))
					.map(x -> x.trim())
					.filter(x -> !x.isEmpty())
					.toList();

				if (vals.size() != amt) 
					throw new Exception(); 

				for (int i = 0; i < amt; i++)
					// Can throw an exception if the conversion fails
					res.add(converters[i].apply(vals.get(i)));

				break;
			} catch (Exception e) {
				Console.moveCursor(Console.MoveDirection.UP, 1);
				Console.clearLine();
				Console.resetColors();
				Console.print(String.format("Hibás bemenet! %s: ", text));
			}

			res.clear();
		}

		return res;
	}
}  