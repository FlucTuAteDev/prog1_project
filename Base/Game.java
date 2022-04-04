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
import Utils.*;
import Utils.Functions.Converters;

public class Game {
	private int money = 0;
	private List<Unit> units = new ArrayList<>();
	private static final int INPUT_HEIGHT = Board.BOARD_HEIGHT + 1;
	
	public static final Hero player = new Hero(Colors.BLUE);
	public static final Hero ai = new Hero(Colors.GREEN); // TODO: AI
	public static final Board board = new Board(player, ai);

	public Game() {
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
		Menu difficultyMenu = new InitMenu("Nehézségi szint");
		Menu mainMenu = new InitMenu("Főmenü");
		Menu skillPointMenu = new InitMenu("Tulajdonságpontok");
		Menu unitsMenu = new InitMenu("Egységek");
		Menu spellMenu = new InitMenu("Varázslatok");

		MenuItem back = new MenuItem(Colors.RED, () -> mainMenu.display(), false, "Vissza");
		HeaderItem headerMoney = new HeaderItem("💰(Pénz): %s", () -> money);
		HeaderItem spacer = new HeaderItem("");

		// Select difficulty
		difficultyMenu.addItem(new MenuItem(
				Colors.GREEN,
				() -> money = 1300,
				false,
				String.format("%-10s (%4d arany)", "Könnyű", 1300)));
		difficultyMenu.addItem(new MenuItem(
				Colors.YELLOW,
				() -> money = 1000,
				false,
				String.format("%-10s (%4d arany)", "Közepes", 1000)));
		difficultyMenu.addItem(new MenuItem(
				Colors.RED,
				() -> money = 700,
				false,
				String.format("%-10s (%4d arany)", "Nehéz", 700)));

		// Main menu
		// items
		mainMenu.addItem(new MenuItem(
				() -> skillPointMenu.display(),
				false,
				String.format("%-20s", "Tulajdonságpontok")));
		mainMenu.addItem(new MenuItem(
				() -> spellMenu.display(),
				false,
				String.format("%-20s", "Varázslatok")));
		mainMenu.addItem(new MenuItem(
				() -> unitsMenu.display(),
				false,
				String.format("%-20s", "Egységek")));
		mainMenu.addItem(new MenuItem(
				Colors.RED,
				() -> {
				}, // TODO: Check if any units are present
				false,
				String.format("%-20s", "Befejezés")));

		// Skillpoint menu
		// headers
		skillPointMenu.addHeader(headerMoney);
		skillPointMenu.addHeader(new HeaderItem("💲(Ár): %s", () -> Skill.PRICE));

		// items
		for (var s : player.getSkills()) {
			String key = s.getKey();
			Skill skill = s.getValue();

			skillPointMenu.addItem(new MenuItem(
					() -> {
						if (Skill.PRICE > money || !skill.addSkill(1))
							return;

						money -= Skill.PRICE;
					},
					true,
					// NAME (x/MAX-SKILL)
					String.format("%-15s", skill.name) + "(%2s/" + String.format("%2d)", Skill.MAX_SKILL),
					() -> player.getSkill(key).getSkill()));
		}
		skillPointMenu.addItem(back);

		// Spell menu
		// headers
		spellMenu.addHeader(headerMoney);
		spellMenu.addHeader(spacer);
		spellMenu.addHeader(new HeaderItem(Colors.GRAY, "💲: Ár"));
		spellMenu.addHeader(new HeaderItem(Colors.GRAY, "💪: Manna"));

		// items
		for (var s : player.getSpells()) {
			Spell spell = s.getValue();
			spellMenu.addItem(new MenuItem(
					() -> {
						int spellPrice = spell.price;
						if (spell.isActive() || money - spellPrice < 0)
							return;

						spell.setActive();
						money -= spellPrice;
					},
					true,
					String.format("%-15s (💲: %3d, 💪: %2d)", spell.name, spell.price, spell.manna)
							+ "%s",
					() -> spell.isActive() ? '✅' : '❌'));
		}
		spellMenu.addItem(back);

		// Units menu
		// headers
		unitsMenu.addHeader(headerMoney);
		unitsMenu.addHeader(spacer);
		unitsMenu.addHeader(new HeaderItem(Colors.GRAY, "💲: Ár"));
		unitsMenu.addHeader(new HeaderItem(Colors.GRAY, "⚔: Sebzés"));
		unitsMenu.addHeader(new HeaderItem(Colors.GRAY, "❤: Életérő"));
		unitsMenu.addHeader(new HeaderItem(Colors.GRAY, "🚀: Sebesség"));
		unitsMenu.addHeader(new HeaderItem(Colors.GRAY, "🙌: Kezdeményezés"));

		// items
		for (Unit unit : player.getUnits()) {
			unitsMenu.addItem(new MenuItem(
					() -> {
						int maxAmount = money / unit.price;
						if (maxAmount == 0)
							return; // TODO - maybe: error message

						int amount = (int) IO.scanAndConvert(
								String.format("Darab [%d - %d]", 1, maxAmount),
								Converters.convertInt(1, maxAmount + 1)).get(0);

						unit.setCount(unit.getCount() + amount);

						money -= amount * unit.price;
					},
					true,
					String.format("%-15s (💲: %2d, ⚔: %2d - %2d, ❤: %2d, 🚀: %2d, 🙌: %2d, ",
							unit.name, unit.price, unit.minDamage, unit.maxDamage, unit.baseHealth, unit.speed,
							unit.initiative) + "%3s db)",
					unit::getCount));
		}

		unitsMenu.addItem(back);

		difficultyMenu.display();
		mainMenu.display();
	}

	public void placeUnits() {
		board.drawBoard();
		// Asks the user where to draw each unit
		for (Unit unit : player.getUnits()) {
			// DEBUG ONLY COMMENT
			// if (unit.getCount().get() == 0) continue;

			Console.setCursorPosition(INPUT_HEIGHT, 0);
			Console.clearLine();
			Console.println("Válaszd ki, hogy hova rakod: %s (%s, %d db)", unit.name, unit.icon, unit.getCount());

			Tile tile = IO.scanTile(0, Board.ROWS, 0, Board.COLS);

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
		Console.setCursorPosition(INPUT_HEIGHT, 0);
		Console.clearBelow();
		Menu actionMenu = new BasicMenu("Lépés");
		Menu unitAttackableMenu = new BasicMenu("Megtámadható egységek");
		Menu heroAttackableMenu = new BasicMenu("Megtámadható egységek");
		Menu spellMenu = new BasicMenu("Választható spellek");

		while (true) {
			for (Unit unit : this.units) {
				Console.setCursorPosition(INPUT_HEIGHT, 0);
				Console.clearBelow();

				Console.print("Most következik: ");
				board.setColors(unit);
				Console.println("%s (%d)", unit.icon, unit.getCount());
				Console.resetStyles();

				// List units that the current unit can attack
				for (Unit attackableUnit : unit.attackableUnits()) {
					unitAttackableMenu.addItem(new MenuItem(
							Colors.textFromBg(attackableUnit.hero.COLOR),
							attackableUnit.hero.COLOR,
							() -> {
								unit.attack(attackableUnit);
								board.redrawUnit(attackableUnit);
							},
							false,
							"%s", () -> attackableUnit.icon));
				}
				unitAttackableMenu.addItem(new MenuItem(
						Colors.RED,
						() -> {
							Console.setCursorPosition(INPUT_HEIGHT + 1, 0);
							Console.clearBelow();
							actionMenu.display();
						}, false, "Vissza"));
				
				// List of units that the current hero can attack
				Hero enemy = unit.hero == player ? ai : player;
				for (Unit attackableUnit : enemy.getUnits()) {
					heroAttackableMenu.addItem(new MenuItem(
						Colors.textFromBg(enemy.COLOR), 
						enemy.COLOR, 
						() -> {
							unit.hero.attack(attackableUnit);
							board.redrawUnit(attackableUnit);
						}, false, String.format("%s", attackableUnit.icon)));
				}

				for (Spell spell : unit.hero.getSpellValues()) {
					if (!spell.isActive()) continue;

					spellMenu.addItem(new MenuItem(
						() -> {
							Console.setCursorPosition(INPUT_HEIGHT + 1, 0);
							Console.clearBelow();
							spell.cast();
						}, 
						false, 
						spell.name));
				}
						
				actionMenu.addItem(new MenuItem(() -> {
					Console.setCursorPosition(INPUT_HEIGHT + 1, 0);
					Console.clearBelow();
					unitAttackableMenu.display();
				}, false, "Egység -> Támadás"));
				
				actionMenu.addItem(new MenuItem(() -> {
					boolean validMove = false;
					do {
						Tile movePos = IO.scanTile();

						validMove = board.moveUnit(unit, movePos.row, movePos.col);
						if (!validMove) {
							Console.setCursorPosition(INPUT_HEIGHT + 1, 0);
							Console.clearBelow();
							Console.setForeground(Colors.RED);
							Console.println("Nem lehet oda lépni!");
							Console.resetStyles();
						}
					} while (!validMove);
				}, false, "Egység -> Mozgás"));
				actionMenu.addItem(new MenuItem(() -> {}, false, "Egység -> Várakozás"));
				actionMenu.addItem(new MenuItem(() -> heroAttackableMenu.display(), false, "Hős -> Támadás"));
				actionMenu.addItem(new MenuItem(() -> {
					Console.setCursorPosition(INPUT_HEIGHT + 1, 0);
					Console.clearBelow();

					spellMenu.display();
				}, false, "Hős -> Varázslás"));

				Console.setCursorPosition(INPUT_HEIGHT + 1, 0);
				Console.clearBelow();
				actionMenu.display();

				actionMenu.clearItems();
				unitAttackableMenu.clearItems();
				heroAttackableMenu.clearItems();
				spellMenu.clearItems();
			}

			// End of round
		}
	}

	public void run() {
		this.init();

		this.placeUnits();

		this.update();

		Console.setCursorPosition(Console.HEIGHT, 0);
	}
}
