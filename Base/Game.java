package Base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Base.Console.Alignment;
import Board.Board;
import Hero.Hero;
import Hero.AI;
import Hero.Player;
import Menu.InitMenu;
import Menu.Menu;
import Menu.Items.MenuItem;
import Units.*;
import View.View;
import View.Colors.*;

/**
 * Handles the flow of the game
 */
public class Game {
	public static final View actionView = new View(Board.HEIGHT + 2, 1, Console.WIDTH / 2, Console.HEIGHT - Board.HEIGHT);
	public static final View menuView = new View(actionView.top + 1, 1, Console.WIDTH / 2, Console.HEIGHT - Board.HEIGHT);
	public static final View inputView = new View(Console.HEIGHT - 1, 1, Console.WIDTH / 2, 1);

	public static final View player1View = new View(1, 1, (Console.WIDTH - Board.WIDTH) / 2 - 1, Board.HEIGHT);
	public static final View player2View = new View(1, (Console.WIDTH + Board.WIDTH) / 2 + 2, (Console.WIDTH - Board.WIDTH) / 2 - 1, Board.HEIGHT);
	public static Hero player1 = new Player("Játékos", Colors.DARK_BLUE, player1View);
	public static Hero player2;

	public static Board	board = new Board();
	private static List<Unit> units = new ArrayList<>();

	public static class Constants {
		public static final int EFFECT_TIME = 1000;
		public static final int MOVE_TIME = 300;
		public static final int AI_MONEY = 1000;
		public static final int TURN_DELAY = 1000;
	}

	public Game() {
		// Select gamemode
		Menu<Integer> gamemodeMenu = new InitMenu<>("Játékmód", new View(1, 1, Console.WIDTH, Console.HEIGHT));
		gamemodeMenu.addItem(new MenuItem<Integer>(0, null, v -> {}, "%-20s", v -> "Játékos vs Játékos"));
		gamemodeMenu.addItem(new MenuItem<Integer>(1, null, v -> {}, "%-20s", v -> "Játékos vs Gép"));

		int mode = gamemodeMenu.display();
		if (mode == 0) player2 = new Player("Játékos2", Colors.DARK_GREEN, player2View);
		else if (mode == 1) player2 = new AI("Kompútor", Colors.DARK_GREEN, player2View);

		player1.addUnit(new Farmer(player1));
		player1.addUnit(new Archer(player1));
		player1.addUnit(new Griff(player1));
		player1.addUnit(new Dragon(player1));
		player1.setEnemy(player2);
		
		player2.addUnit(new Farmer(player2));
		player2.addUnit(new Archer(player2));
		player2.addUnit(new Griff(player2));
		player2.addUnit(new Dragon(player2));
		player2.setEnemy(player1);

		units.addAll(player1.getUnits());
		units.addAll(player2.getUnits());
	}

	/**
	 * Runs the game loop
	 */
	private void update() {
		// Sort the units by initiative
		Collections.sort(units);
		int round = 0;
		while (true) {
			player1.usedAbility = false;
			player2.usedAbility = false; 

			for (Unit unit : units) {
				if (unit.isDead()) continue;
				
				Hero hero = unit.hero;
				while (!hero.usedUnit) {
					// Endscreen
					if (hero.getAliveUnits().size() == 0 || hero.getEnemy().getAliveUnits().size() == 0) return;

					unit.select();

					player1.draw();
					player2.draw();
					
					// Draw round order
					actionView.clear();
					Console.print("%d. kör. Sorrend: ", round + 1);
					for (int i = 0; i < units.size(); i++) {
						Unit next = units.get(i);
						if (next.isDead()) continue;

						if (next == unit) Colors.setBgWithFg(Colors.brighten(next.hero.COLOR, .5));
						else Colors.setBgWithFg(next.hero.COLOR);

						Console.print(" %s ", next.icon);
						Console.resetStyles();
					}

					hero.takeTurn(unit);

					unit.deselect();
				}

				hero.usedUnit = false;
			}

			// Enable all counter attacks
			units.forEach(Unit::enableCounter);
			// End of round
			round++;
		}
	}

	public void endScreen() {
		boolean playerDead = player1.getAliveUnits().size() == 0;
		boolean aiDead = player2.getAliveUnits().size() == 0;

		Console.clearScreen();
		Console.setCursorPosition(Console.HEIGHT / 2, 0);
		if (playerDead && aiDead)
			Console.printlnAligned(Alignment.CENTER, Console.WIDTH, "Döntetlen - a felek kiegyenlítettek voltak.");
		else if (playerDead)
			Console.printlnAligned(Alignment.CENTER, Console.WIDTH, "%s nyert - csak a jobb oldal!", player2);
		else if (aiDead)
			Console.printlnAligned(Alignment.CENTER, Console.WIDTH, "%s nyert - csak a bal oldal!", player1);
	}

	public void run() {
		Console.clearScreen();

		player1.init();
		player2.init();

		board.draw();
		player1.placeUnits();
		player2.placeUnits();

		this.update();

		this.endScreen();

		Console.setCursorPosition(Console.HEIGHT, 0);
	}

	public static List<Unit> getUnits() {
		return units;
	}
	public static List<Unit> getAliveUnits() {
		return units.stream().filter(x -> !x.isDead()).toList();
	}

	private static View errorView = new View(Console.HEIGHT, Console.WIDTH / 2 + 1, Console.WIDTH / 2, 1);
	/**
	 * Prints the given error message to {@link Base.Game#errorView errorView}
	 * @param format
	 * @param args
	 */
	public static void logError(String format, Object... args) {
		errorView.clear();
		Console.setForeground(Colors.RED);
		errorView.printlnAligned(Alignment.RIGHT, format, args);
		Console.resetStyles();
	}
	public static void clearError() {
		Console.saveCursor();
		errorView.clear();
		Console.restoreCursor();
	}

	static View messageView = new View(Board.HEIGHT + 2, Console.WIDTH / 2 + 1, Console.WIDTH / 2, Console.HEIGHT - Board.HEIGHT - 2);
	private static List<String> messages = new ArrayList<>();
	/**
	 * Prints the given message to {@link #messageView messageView}
	 * Scrolls the {@link #messageView messageView} in case of an overflow
	 * @param format
	 * @param args
	 */
	public static void logMessage(String format, Object... args) {
		// If the messages overflow remove the first element
		if (messages.size() == messageView.height) 
			messages.remove(0);

		messages.add(String.format(format, args));

		messageView.clear();
		for (String msg : messages)
			messageView.println(msg);
	}
}
