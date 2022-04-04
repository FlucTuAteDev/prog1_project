package Utils;

import Base.Console;
import Base.Game;
import Board.Board;
import Board.Tile;
import Units.Unit;
import Utils.Functions.ConverterFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class IO {
	static Scanner sc = new Scanner(System.in);

	/**
	 * Scans n space separated objects given by the number of converter functions
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

		Console.clearLine();
		Console.print("%s: ", text);
		while(true) {
			try {
				String current = sc.nextLine();

				// Splits the string at the delimiter, trims them and takes the ones that are not empty
				List<String> vals = Arrays.stream(current.split(" "))
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
				Console.resetStyles();
				Console.print("%s %s: ", e.getMessage(), text);
			}
			res.clear();
		}

		return res;
	}

	public static Tile scanTile(int rMin, int rMax, int cMin, int cMax) {
		// User input can range from min + 1 to max (1 based index)
		// Position is returned from min to max - 1 (0 based index)
		char firstCol = (char) ('a' + cMin);
		char lastCol = (char) ('a' + cMax - 1);

		return (Tile) scanAndConvert(
				String.format("Cella [%c%d - %c%d]", firstCol, rMin + 1, lastCol, rMax),
				(String in) -> {
					char c = in.charAt(0);
					int r = Integer.parseInt(in.substring(1));

					if (c < firstCol || c > lastCol || r < rMin + 1 || r > rMax)
						throw new Exception("Hibás bemenet!");

					return Game.board.getTile(r - 1, c - 'a');
				}).get(0);
	}

	public static Tile scanTile() {
		return scanTile(0, Board.ROWS, 0, Board.COLS);
	}

	// public static Unit scanUnit(Collection<Unit> units) {

	// }
}  