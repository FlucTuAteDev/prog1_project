package Utils;

import Base.Game;

public class ThreadHelper {
	// Nincs kedvem minden alkalommal try catch-be írni, kösz helo
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			Game.logError("Hiba történt a szál alvása közben ¯\\_(ツ)_/¯");
			System.exit(1);
		}
	}
}
