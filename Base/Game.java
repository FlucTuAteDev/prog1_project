package Base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Base.Console.Alignment;
import Board.Board;
import Board.Tile;
import Hero.Hero;
import Hero.AI;
import Hero.Skill;
import Menu.*;
import Menu.Items.HeaderItem;
import Menu.Items.MenuItem;
import Spells.Spell;
import Units.*;
import View.View;
import View.Colors.*;

public class Game {
	public static final View initView = new View(1, 1, Console.WIDTH, Console.HEIGHT - 1);
	public static final View actionView = new View(Board.HEIGHT + 2, 1, Console.WIDTH / 2, Console.HEIGHT - Board.HEIGHT);
	public static final View menuView = new View(actionView.top + 1, 1, Console.WIDTH / 2, Console.HEIGHT - Board.HEIGHT);
	public static final View inputView = new View(Console.HEIGHT - 1, 1, Console.WIDTH / 2, 1);

	public static final View playerView = new View(1, 1, (Console.WIDTH - Board.WIDTH) / 2 - 1, Board.HEIGHT);
	public static final View aiView = new View(1, (Console.WIDTH + Board.WIDTH) / 2 + 2, (Console.WIDTH - Board.WIDTH) / 2 - 1, Board.HEIGHT);
	public static final Hero user = new Hero("Játékos", Colors.DARK_BLUE, playerView);
	public static final Hero ai = new AI("Kompútor", Colors.DARK_GREEN, aiView); // TODO: AI

	public static Board	board = new Board(user, ai);
	public static List<Unit> units = new ArrayList<>();

	public Game() {
		user.addUnit(new Farmer(user));
		user.addUnit(new Archer(user));
		user.addUnit(new Griff(user));
		user.setEnemy(ai);

		ai.addUnit(new Farmer(ai));
		ai.addUnit(new Archer(ai));
		ai.addUnit(new Griff(ai));
		ai.setEnemy(user);

		units.addAll(user.getUnits());
		units.addAll(ai.getUnits());
	}

	private void init() {
		Menu<Integer> difficultyMenu = new InitMenu<>("Nehézségi szint", initView);
		Menu<Object> mainMenu = new InitMenu<>("Főmenü", initView);
		Menu<Skill> skillMenu = new InitMenu<>("Tulajdonságpontok", initView);
		Menu<Unit> unitMenu = new InitMenu<>("Egységek", initView);
		Menu<Spell> spellMenu = new InitMenu<>("Varázslatok", initView);

		HeaderItem moneyHeader = new HeaderItem("💰(Pénz): %s", user::getMoney);
		HeaderItem spacer = new HeaderItem("");

		// Difficulity
		var difficulityItems = List.of(
			List.of(1300, "Könnyű", Colors.GREEN),
			List.of(1000, "Közepes", Colors.YELLOW),
			List.of(700, "Nehéz", Colors.RED)
		);
		for (var item : difficulityItems) {
			difficultyMenu.addItem(new MenuItem<>((int)item.get(0), mainMenu, (RGB)item.get(2), 
				user::setMoney, 
				"%-10s (%4s arany)", v -> item.get(1), v -> v));
		}

		// Main
		mainMenu.addHeader(moneyHeader);
		var mainMenuItems = List.of(
			List.of("Tulajdonságpontok", skillMenu),
			List.of("Varázslatok", spellMenu),
			List.of("Egységek", unitMenu)
		);
		for (var item : mainMenuItems) {
			mainMenu.addItem(new MenuItem<Object>(item.get(0), (Menu<?>)item.get(1),
				v -> {}, 
				"%-20s", v -> v));
		}
		mainMenu.addItem(new MenuItem<Object>(null, null, Colors.RED,
				v -> {
					// If no units were bought
					if (Game.units.stream().allMatch(x -> x.getCount() == 0)) {
						logError("Legalább egy egységet vársárolni kell, mielőtt csatába mész!");
						mainMenu.display();
					}
				}, 
				"Befejezés"));

		// Skills
		skillMenu.addHeader(moneyHeader);
		skillMenu.addHeader(new HeaderItem("💲(Ár): %s", user::getSkillPrice));
		for (var s : user.getSkills()) {
			Skill skill = s.getValue();

			skillMenu.addItem(new MenuItem<Skill>(skill, skillMenu,
				v -> v.addPoints(1), "%-15s (%2s/%2s)", v -> v.name, Skill::getPoints, v -> Skill.MAX_SKILL));
		}
		skillMenu.addItem(new MenuItem<>(null, mainMenu, Colors.RED, v -> {}, "Vissza"));

		// Spells
		spellMenu.addHeader(moneyHeader);
		spellMenu.addHeader(spacer);
		spellMenu.addHeader(new HeaderItem(Colors.GRAY, "💲: Ár"));
		spellMenu.addHeader(new HeaderItem(Colors.GRAY, "💪: Manna"));
		for (var s : user.getSpells()) {
			Spell spell = s.getValue();
			spellMenu.addItem(new MenuItem<>(spell, spellMenu,
					v -> {
						int spellPrice = v.price;
						if (v.isActive() || !user.takeMoney(spellPrice))
							return;

						v.setActive();
					},
					"%-15s (💲: %3s, 💪: %2s) %s",
					v -> v.name, v -> v.price, v -> v.manna, v -> v.isActive() ? "✔" : "✖"));
					
		}
		spellMenu.addItem(new MenuItem<>(null, mainMenu, Colors.RED, v -> {}, "Vissza"));

		// Units
		unitMenu.addHeader(moneyHeader);
		unitMenu.addHeader(spacer);
		unitMenu.addHeader(new HeaderItem(Colors.GRAY, "💲: Ár"));
		unitMenu.addHeader(new HeaderItem(Colors.GRAY, "⚔: Sebzés"));
		unitMenu.addHeader(new HeaderItem(Colors.GRAY, "❤: Életérő"));
		unitMenu.addHeader(new HeaderItem(Colors.GRAY, "🚀: Sebesség"));
		unitMenu.addHeader(new HeaderItem(Colors.GRAY, "🙌: Kezdeményezés"));
		for (Unit unit : user.getUnits()) {
			unitMenu.addItem(new MenuItem<>(unit, unitMenu,
					v -> {
						int maxAmount = user.getMoney() / v.price;
						if (maxAmount == 0)
							return; // TODO: error message

						int amount = Console.scanInt("Darab", 0, maxAmount);
						v.setMaxCount(v.getMaxCount() + amount);
						user.takeMoney(amount * v.price);
					},
					"%-15s (💲: %2s, ⚔: %2s - %2s, ❤: %2s, 🚀: %2s, 🙌: %2s, %3s db)",
					v -> v.name, v -> v.price, v -> v.minDamage, v -> v.maxDamage, v -> v.baseHealth, v -> v.speed,
					v -> v.baseInitiative , Unit::getCount));
		}
		unitMenu.addItem(new MenuItem<>(null, mainMenu, Colors.RED, v -> {}, "Vissza"));

		Console.clearScreen();
		difficultyMenu.display();
	}

	private void placeUnits() {
		board.draw();
		// Asks the user where to draw each unit
		for (Unit unit : user.getUnits()) {
			// DEBUG ONLY COMMENT
			// if (unit.getCount().get() == 0) continue;

			actionView.clear();
			Console.println("Válaszd ki, hogy hova rakod: %s (%s, %d db)", unit.name, unit.icon, unit.getCount());

			Tile tile = Console.scanTile(0, Board.ROWS, 0, 2, 
				x -> x.hasUnit() ? "Az adott cellán már tartózkodik egység" : null);

			unit.setTile(tile);
		}

		Random rand = new Random();
		for (Unit unit : ai.getUnits()) {
			int row, col;
			unit.setMaxCount(50);
			do {
				row = rand.nextInt(Board.ROWS);
				col = rand.nextInt(Board.COLS - 2, Board.COLS);
			} while (board.getTile(row, col).hasUnit());
			unit.setTile(board.getTile(row, col));
		}
	}

	private void placeRandom() {
		board.draw();
		Random rand = new Random();
		
		user.setMoney(9999);
		user.getSpellValues().forEach(x -> x.setActive());
		user.getSkill("magic").addPoints(9);
		user.getSkill("intelligence").addPoints(9);
		// user.getSkill("moral").addPoints(9);

		for (Unit unit : user.getUnits()) {
			int row, col;
			unit.setMaxCount(50);
			do {
				row = rand.nextInt(Board.ROWS);
				col = rand.nextInt(0, 2);
			} while (board.getTile(row, col).hasUnit());
			unit.setTile(board.getTile(row, col));
		}

		for (Unit unit : ai.getUnits()) {
			int row, col;
			unit.setMaxCount(50);
			do {
				row = rand.nextInt(Board.ROWS);
				col = rand.nextInt(Board.COLS - 2, Board.COLS);
			} while (board.getTile(row, col).hasUnit());
			unit.setTile(board.getTile(row, col));
		}
	}

	private void update() {
		// Sort the units by initiative
		Collections.sort(units);
		int round = 0;
		while (true) {
			user.usedAbility = false;
			ai.usedAbility = false; 

			for (Unit unit : units) {
				Hero hero = unit.hero;
				// Endscreen
				if (hero.getAliveUnits().size() == 0 || hero.getEnemy().getAliveUnits().size() == 0) return;

				if (unit.isDead()) continue;
				unit.select();

				user.draw();
				ai.draw();
				
				// Draw round order
				actionView.clear();
				Console.print("%d. kör. Sorrend: ", round + 1);
				for (int i = 0; i < units.size(); i++) {
					Unit next = units.get(i);
					if (next.isDead()) continue;

					if (next == unit) Colors.setBgWithFg(Colors.brighten(next.hero.COLOR, .5));
					else next.hero.setColors();

					Console.print("%s", next);
					Console.resetStyles();
				}	

				hero.takeTurn(unit);

				unit.deselect();
			}

			// Enable all counter attacks
			units.forEach(Unit::enableCounter);
			// End of round
			round++;
		}
	}

	public void endScreen() {
		boolean playerDead = user.getAliveUnits().size() == 0;
		boolean aiDead = ai.getAliveUnits().size() == 0;

		Console.clearScreen();
		Console.setCursorPosition(Console.HEIGHT / 2, 0);
		if (playerDead && aiDead)
			Console.printlnAligned(Alignment.CENTER, Console.WIDTH, "Döntetlen - a felek kiegyenlítettek voltak.");
		else if (playerDead)
			Console.printlnAligned(Alignment.CENTER, Console.WIDTH, "%s nyert - csak a jobb oldal!", ai);
		else if (aiDead)
			Console.printlnAligned(Alignment.CENTER, Console.WIDTH, "%s nyert - csak a bal oldal!", user);
	}

	public void run() {
		// Console.clearScreen();
		// this.init();

		// this.placeUnits();
		this.placeRandom();

		this.update();

		this.endScreen();

		Console.setCursorPosition(Console.HEIGHT, 0);
	}

	public List<Unit> getUnits() {
		return units;
	}

	private static View errorView = new View(Console.HEIGHT, Console.WIDTH / 2 + 1, Console.WIDTH / 2, 1);
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
