package Utils;

import Base.Console;
import java.util.Scanner;

public class IO {
	static Scanner sc = new Scanner(System.in);

	public static int scanInt(String text, int min, int max) {
		Console.print(text + " [" + min + " - " + max + "]: ");
		int selected = -1;
		while(true) {
			try {
				selected = Integer.parseInt(sc.nextLine());

				// Only quit if input is in the given range
				if (selected >= min && selected <= max)
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

	public static int[] scanIntPair(String text, int minX, int maxX, int minY, int maxY) {
		Console.print(String.format("%s [%d - %d, %d - %d]", text, minX, maxX, minY, maxY));
		return new int[] {0, 0};
	}
}