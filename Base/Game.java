package Base;

import java.util.PriorityQueue;
import java.util.Random;

import Board.Board;
import Hero.Hero;
import Hero.Skill;
import Menu.*;
import Spells.Spell;
import Units.*;
import Utils.*;
import Utils.Functions.Converters;

public class Game {
	private int money = 0;
	private Hero player = new Hero();
	private Hero ai = new Hero(); // TODO: AI
	private PriorityQueue<Unit> units = new PriorityQueue<>();

	private Board board = new Board(player, ai);

	public Game() {
		units.add(new Farmer(player));
		units.add(new Farmer(ai));
		units.add(new Archer(player));
		units.add(new Archer(ai));
		units.add(new Griff(player));
		units.add(new Griff(ai));
	}

	public void init() {
		Menu difficultyMenu = new Menu("Nehézségi szint");
		Menu mainMenu = new Menu("Főmenü");
		Menu skillPointMenu = new Menu("Tulajdonságpontok");
		Menu unitsMenu = new Menu("Egységek");
		Menu spellMenu = new Menu("Varázslatok");

		MenuItem back = new MenuItem(Colors.RED, () -> mainMenu.display(), false, "Vissza");
		HeaderItem headerMoney = new HeaderItem(Colors.WHITE, "💰(Pénz): %s", () -> money);
		HeaderItem spacer = new HeaderItem(Colors.WHITE, "");

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
				Colors.WHITE,
				() -> skillPointMenu.display(),
				false,
				String.format("%-20s", "Tulajdonságpontok")));
		mainMenu.addItem(new MenuItem(
				Colors.WHITE,
				() -> spellMenu.display(),
				false,
				String.format("%-20s", "Varázslatok")));
		mainMenu.addItem(new MenuItem(
				Colors.WHITE,
				() -> unitsMenu.display(),
				false,
				String.format("%-20s", "Egységek")));
		mainMenu.addItem(new MenuItem(
				Colors.RED,
				() -> {}, // TODO: Check if any units are present
				false,
				String.format("%-20s", "Befejezés")));

		// Skillpoint menu
		// headers
		skillPointMenu.addHeader(headerMoney);
		skillPointMenu.addHeader(new HeaderItem(Colors.WHITE, "💲(Ár): %s", player::getSkillPrice));

		// items
		for (var s : player.getSkills()) {
			String key = s.getKey();
			Skill skill = s.getValue();

			skillPointMenu.addItem(new MenuItem(
					Colors.WHITE,
					() -> {
						int skillPrice = player.getSkillPrice();

						if (skillPrice > money || !player.addSkillValue(key, 1))
							return;

						money -= skillPrice;
					},
					true,
					// NAME (x/MAX-SKILL)
					String.format("%-15s", skill.name) + "(%2s/" + String.format("%2d)", Hero.MAX_SKILL),
					() -> player.getSkillValue(key)));
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
					Colors.WHITE,
					() -> {
						int spellPrice = spell.getPrice();
						if (spell.isActive() || money - spellPrice < 0)
							return;

						spell.setActive();
						money -= spellPrice;
					},
					true,
					String.format("%-15s (💲: %3d, 💪: %2d)", spell.getName(), spell.getPrice(), spell.getManna())
							+ " %s",
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
		for (Unit unit : this.units.stream().filter(x -> x.hero == player).toArray(Unit[]::new)) {
			unitsMenu.addItem(new MenuItem(
					Colors.WHITE,
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
							unit.name, unit.price, unit.minDamage, unit.maxDamage, unit.health, unit.speed,
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
		for (Unit unit : player.getUnitsFrom(units)) {
			// DEBUG ONLY COMMENT
			// if (unit.getCount().get() == 0) continue;

			Console.setCursorPosition(Board.BOARD_HEIGHT + 3, 0);
			Console.clearLine();
			Console.println(String.format("Válaszd ki, hogy hova rakod: %s (%s, %d db)", unit.name, unit.icon, unit.getCount()));

			Position pos = Board.scanPosition(0, 2, 0, Board.BOARD_ROWS);

			board.drawUnit(unit, pos.row, pos.col);
		}

		// Random rand = new Random();
		// for (Unit unit : ai.getUnitValues()) {
		// 	int row, col;
		// 	do {
		// 		row = rand.nextInt(BOARD_ROWS);
		// 		col = rand.nextInt(BOARD_COLS - 2, BOARD_COLS);
		// 	} while (board[row][col] != null);

		// 	board[row][col] = unit;
		// 	this.drawUnit(unit, row, col);
		// }
	}

	private void update() {
	}
	
	public void run() {
		this.init();
		
		this.placeUnits();

		this.update();

		Console.setCursorPosition(30, 0); // DEBUG
	}
}
