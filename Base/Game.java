package Base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Board.Board;
import Board.Tile;
import Hero.Hero;
import Hero.Skill;
import Menu.*;
import Menu.Items.HeaderItem;
import Menu.Items.MenuItem;
import Spells.Spell;
import Units.*;
import View.IO;
import View.View;
import View.Colors.*;

public class Game {
	static int INPUT_HEIGHT = Board.HEIGHT + 1;
	public static View initView = new View(1, 1, Console.WIDTH, Console.HEIGHT);
	public static View belowView = new View(INPUT_HEIGHT, 1, Console.WIDTH, Console.HEIGHT - Board.HEIGHT);
	public static View menuView = new View(INPUT_HEIGHT + 1, 1, Console.WIDTH, Console.HEIGHT - Board.HEIGHT);
	public static View playerView = new View(1, 1, (Console.WIDTH - Board.WIDTH) / 2, Console.HEIGHT - Board.HEIGHT);
	public static View aiView = new View(1, (Console.WIDTH + Board.WIDTH) / 2, (Console.WIDTH - Board.WIDTH) / 2, Console.HEIGHT - Board.HEIGHT);
	public static Hero player = new Hero("Játékos", Colors.BLUE, playerView);
	public static Hero ai = new Hero("AI", Colors.GREEN, aiView); // TODO: AI
	public static Board	board = new Board(player, ai);

	private int money = 0;
	private List<Unit> units = new ArrayList<>();

	public Game() {
		initView = new View(1, 1, Console.WIDTH, Console.HEIGHT);
		belowView = new View(INPUT_HEIGHT, 1, Console.WIDTH, Console.HEIGHT - Board.HEIGHT);
		menuView = new View(INPUT_HEIGHT + 1, 1, Console.WIDTH, Console.HEIGHT - Board.HEIGHT);

		playerView = new View(1, 1, (Console.WIDTH - Board.WIDTH) / 2, Board.HEIGHT);
		aiView = new View(1, (Console.WIDTH + Board.WIDTH) / 2 + 1, (Console.WIDTH - Board.WIDTH) / 2, Board.HEIGHT);
		
		player = new Hero("Játékos", Colors.BLUE, playerView);
		ai = new Hero("AI", Colors.GREEN, aiView); // TODO: AI
		board = new Board(player, ai);

		player.addUnit(new Farmer(player));
		player.addUnit(new Archer(player));
		player.addUnit(new Griff(player));

		ai.addUnit(new Farmer(ai));
		ai.addUnit(new Archer(ai));
		ai.addUnit(new Griff(ai));

		units.addAll(player.getUnits());
		units.addAll(ai.getUnits());
		Collections.sort(units);
	}

	public void init() {
		Menu<Integer> difficultyMenu = new InitMenu<>("Nehézségi szint", initView);
		Menu<Object> mainMenu = new InitMenu<>("Főmenü", initView);
		Menu<Skill> skillMenu = new InitMenu<>("Tulajdonságpontok", initView);
		Menu<Unit> unitMenu = new InitMenu<>("Egységek", initView);
		Menu<Spell> spellMenu = new InitMenu<>("Varázslatok", initView);

		HeaderItem moneyHeader = new HeaderItem("💰(Pénz): %s", () -> money);
		HeaderItem spacer = new HeaderItem("");

		// Difficulity
		var difficulityItems = List.of(
			List.of(1300, "Könnyű", Colors.GREEN),
			List.of(1000, "Közepes", Colors.YELLOW),
			List.of(700, "Nehéz", Colors.RED)
		);
		for (var item : difficulityItems) {
			difficultyMenu.addItem(new MenuItem<>((int)item.get(0), mainMenu, (RGB)item.get(2), 
				v -> money = v, 
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
				v -> {}, 
				"Befejezés"));

		// Skills
		skillMenu.addHeader(moneyHeader);
		skillMenu.addHeader(new HeaderItem("💲(Ár): %s", player::getSkillPrice));
		for (var s : player.getSkills()) {
			Skill skill = s.getValue();

			skillMenu.addItem(new MenuItem<Skill>(skill, skillMenu,
				v -> {
					int skillPrice = v.hero.getSkillPrice();
					if (!this.takeMoney(skillPrice) || !v.addSkill(1))
						return;
				}, "%-15s (%2s/%2s)", v -> v.name, v -> v.getSkill(), v -> Skill.MAX_SKILL));
		}
		skillMenu.addItem(new MenuItem<>(null, mainMenu, Colors.RED, v -> {}, "Vissza"));

		// Spells
		spellMenu.addHeader(moneyHeader);
		spellMenu.addHeader(spacer);
		spellMenu.addHeader(new HeaderItem(Colors.GRAY, "💲: Ár"));
		spellMenu.addHeader(new HeaderItem(Colors.GRAY, "💪: Manna"));
		for (var s : player.getSpells()) {
			Spell spell = s.getValue();
			spellMenu.addItem(new MenuItem<>(spell, spellMenu,
					v -> {
						int spellPrice = v.price;
						if (v.isActive() || !this.takeMoney(spellPrice))
							return;

						v.setActive();
					},
					"%-15s (💲: %3s, 💪: %2s)%s",
					v -> v.name, v -> v.price, v -> v.manna, v -> v.isActive() ? "✅" : "❌"));
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
		for (Unit unit : player.getUnits()) {
			unitMenu.addItem(new MenuItem<>(unit, unitMenu,
					v -> {
						int maxAmount = money / v.price;
						if (maxAmount == 0)
							return; // TODO: error message

						int amount = IO.scanInt("Darab", 1, maxAmount);
						v.setCount(v.getCount() + amount);
						money -= amount * v.price;
					},
					"%-15s (💲: %2s, ⚔: %2s - %2s, ❤: %2s, 🚀: %2s, 🙌: %2s, %3s db)",
					v -> v.name, v -> v.price, v -> v.minDamage, v -> v.maxDamage, v -> v.baseHealth, v -> v.speed,
					v -> v.initiative , v -> v.getCount()));
		}
		unitMenu.addItem(new MenuItem<>(null, mainMenu, Colors.RED, v -> {}, "Vissza"));

		difficultyMenu.display();
	}

	public void placeUnits() {
		board.drawBoard();
		// Asks the user where to draw each unit
		for (Unit unit : player.getUnits()) {
			// DEBUG ONLY COMMENT
			// if (unit.getCount().get() == 0) continue;

			belowView.clear();
			Console.println("Válaszd ki, hogy hova rakod: %s (%s, %d db)", unit.name, unit.icon, unit.getCount());


			Tile tile = IO.scanTile(0, Board.ROWS, 0, 2, 
				x -> x.hasUnit() ? "Az adott cellán már tartózkodik egység" : null);
			board.drawUnit(unit, tile.row, tile.col);
		}

		Random rand = new Random();
		for (Unit unit : ai.getUnits()) {
			int row, col;
			unit.setCount(50);
			do {
				row = rand.nextInt(Board.ROWS);
				col = rand.nextInt(Board.COLS - 2, Board.COLS);
			} while (!board.drawUnit(unit, row, col));
		}
	}

	private void update() {
		Menu<Object> actionMenu = new BasicMenu<>("Lehetőségek:", menuView);
		Menu<Unit> unitAttackableMenu = new BasicMenu<>("Megtámadható egységek:", menuView);
		Menu<Unit> heroAttackableMenu = new BasicMenu<>("Megtámadható egységek:", menuView);
		Menu<Spell> spellMenu = new BasicMenu<>("Választható spellek:", menuView);

		int round = 1;
		while (true) {
			for (Unit unit : this.units) {
				player.draw();
				ai.draw();
				Hero enemy = unit.hero == player ? ai : player;
				
				belowView.clear();
				Console.print("%d. kör. Most következik: ", round);
				board.setColors(unit);
				Console.println("%s (%d)", unit.icon, unit.getCount());
				Console.resetStyles();

				// Unit attacks
				for (Unit attackableUnit : unit.attackableUnits()) {
					unitAttackableMenu.addItem(new MenuItem<>(attackableUnit, null,
							Colors.textFromBg(attackableUnit.hero.COLOR),
							attackableUnit.hero.COLOR,
							v -> {
								v.attack(attackableUnit);
								board.redrawUnit(attackableUnit);
							},
							"%s", v -> v.icon));
				}
				unitAttackableMenu.addItem(new MenuItem<>(null, actionMenu, Colors.RED, v -> {}, "Vissza"));
				
				// Hero attacks
				for (Unit attackableUnit : enemy.getUnits()) {
					heroAttackableMenu.addItem(new MenuItem<>(attackableUnit, null,
						Colors.textFromBg(enemy.COLOR), 
						enemy.COLOR, 
						v -> {
							v.hero.attack(attackableUnit);
							board.redrawUnit(attackableUnit);
						}, "%s", v -> v.icon));
				}
				heroAttackableMenu.addItem(new MenuItem<>(null, actionMenu, Colors.RED, v -> {}, "Vissza"));
				
				// Hero spells
				for (Spell spell : unit.hero.getSpellValues()) {
					if (!spell.isActive()) continue;
					
					spellMenu.addItem(new MenuItem<>(spell, null,
					v -> v.cast(), 
					"%s", v -> v.name));
				}
				spellMenu.addItem(new MenuItem<>(null, actionMenu, Colors.RED, v -> {}, "Vissza"));
				
				// Actions
				if (unit.attackableUnits().size() != 0)
					actionMenu.addItem(new MenuItem<>(null, unitAttackableMenu, v -> {}, "Egység -> Támadás"));

				actionMenu.addItem(new MenuItem<>(null, null,
					v -> {
						IO.scanTile(
							x -> x.hasUnit() ? "Az adott cellán már tartózkodik egység" : null,
							x -> !board.moveUnit(unit, x.row, x.col) ? "Az egység nem tud a megadott cellára lépni!" : null
						);
				}, "Egység -> Mozgás"));
				actionMenu.addItem(new MenuItem<>(null, null, v -> {}, "Egység -> Várakozás"));
				actionMenu.addItem(new MenuItem<>(null, heroAttackableMenu, v -> {}, "Hős -> Támadás"));
				
				if (enemy.getSpells().size() != 0)
					actionMenu.addItem(new MenuItem<>(null, spellMenu, v -> {}, "Hős -> Varázslás"));

				actionMenu.display();

				actionMenu.clearItems();
				unitAttackableMenu.clearItems();
				heroAttackableMenu.clearItems();
				spellMenu.clearItems();
			}
			// End of round
			round++;
		}
	}

	public void run() {
		this.init();

		this.placeUnits();

		this.update();

		Console.setCursorPosition(Console.HEIGHT, 0);
	}

	private boolean takeMoney(int amt) {
		if (amt > this.money) return false;
		
		this.money -= amt;
		return true;
	}
}
