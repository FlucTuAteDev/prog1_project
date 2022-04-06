package View;

import Base.Console;
import Base.Game;
import Board.Board;
import Board.Tile;
import Utils.Maths;
import Utils.Functions.ConverterFunction;
import View.Colors.Colors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class IO {
	static Scanner sc = new Scanner(System.in);

	/**
	 * Scans n space separated objects given by the number of converter functions
	 * 
	 * @param text       Text to place before aksing for input
	 * @param converters
	 *                   Each function specifies how to convert the n-th scanned
	 *                   element to the desired type.
	 *                   If the conversion fails the converter should throw an
	 *                   exception.
	 *                   If it succeeds the converter should return the converted
	 *                   value.
	 * @return
	 */
	@SafeVarargs
	public static List<Object> scanAndConvert(String text, ConverterFunction<String, Object>... converters) {
		List<Object> res = new ArrayList<>();
		int amt = converters.length;

		Console.clearLine();
		while (true) {
			Console.print("%s: ", text);
			try {
				String current = sc.nextLine();

				// Splits the string at the delimiter, trims them and takes the ones that are
				// not empty
				List<String> vals = Arrays.stream(current.split(" "))
						.map(x -> x.trim())
						.filter(x -> !x.isEmpty())
						.toList();

				if (vals.size() != amt)
					throw new Exception("Nem megfelelő mennyiségű bemenet!");

				for (int i = 0; i < amt; i++)
					// Can throw an exception if the conversion fails
					res.add(converters[i].apply(vals.get(i)));
				break;
			} catch (Exception e) {
				Console.setForeground(Colors.RED);

				Console.clearLine(); // If there was an error message before
				Console.print("%s", e.getMessage());
				Console.resetStyles();

				Console.moveCursor(Console.MoveDirection.UP, 1);
				Console.clearLine();
			}
			res.clear();
		}

		return res;
	}

	@SafeVarargs
	public static Tile scanTile(int rMin, int rMax, int cMin, int cMax, Function<Tile, String>... filters) {
		// User input can range from min + 1 to max (1 based index)
		// Position is returned from min to max - 1 (0 based index)
		char firstCol = (char) ('a' + cMin);
		char lastCol = (char) ('a' + cMax - 1);

		return (Tile) scanAndConvert(
				String.format("Cella [%c%d - %c%d]", firstCol, rMin + 1, lastCol, rMax),
				(String in) -> {
					char col = 'a';
					int row = 0;
					try {
						col = in.charAt(0);
						row = Integer.parseInt(in.substring(1));

						if (!Character.isLetter(col))
							throw new Exception();
					} catch (Exception e) {
						throw new Exception("A bemenet nem egy cella!");
					}

					if (col < firstCol || col > lastCol || row < rMin + 1 || row > rMax)
						throw new Exception("A cella a határokon kívül esik!");

					Tile tile = Game.board.getTile(row - 1, col - 'a');
					for (var filter : filters) {
						String curr = filter.apply(tile);
						if (curr != null)
							throw new Exception(curr);
					}

					return tile;
				}).get(0);
	}

	@SafeVarargs
	public static Tile scanTile(Function<Tile, String>... filters) {
		return scanTile(0, Board.ROWS, 0, Board.COLS, filters);
	}

	public static int scanInt(String text, int min, int max) {
		return (int) IO.scanAndConvert(String.format("%s [%d - %d]", text, min, max),
				x -> {
					int parsed = 0;
					try {
						parsed = Integer.parseInt(x);
					} catch (NumberFormatException e) {
						throw new Exception("A bemenet nem egy szám!");
					}

					if (!Maths.inRange(parsed, min, max + 1))
						throw new Exception("A szám a határokon kívül esik!");

					return parsed;
				}).get(0);
	}

	// public static Unit scanUnit(Collection<Unit> units) {

	// }
}