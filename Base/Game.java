package Base;

import java.util.concurrent.atomic.*;

import Board.Board;
import Hero.Hero;
import Hero.Skill;
import Menu.*;
import Spells.Spell;
import Units.Unit;
import Utils.*;
import Utils.Functions.Converters;

public class Game {
	RGB white = new RGB(255, 255, 255);
	RGB gray = new RGB(150, 150, 150);
	RGB green = new RGB(0, 255, 0);
	RGB yellow = new RGB(255, 255, 0);
	RGB red = new RGB(255, 0, 0);

	private AtomicInteger money = new AtomicInteger(0);
	private Hero hero = new Hero();

	public void init() {
		Menu difficultyMenu = new Menu("Nehézségi szint");
		Menu mainMenu = new Menu("Főmenü");
		Menu skillPointMenu = new Menu("Tulajdonságpontok");
		Menu unitsMenu = new Menu("Egységek");
		Menu spellMenu = new Menu("Varázslatok");

		MenuItem back = new MenuItem(red, () -> mainMenu.display(), false, "Vissza");
		HeaderItem headerMoney = new HeaderItem(white, "💰(Pénz): %s", money);
		HeaderItem spacer = new HeaderItem(white, "");

		// Select difficulty
		difficultyMenu.addItem(new MenuItem(
				green,
				() -> money.set(1300),
				false,
				String.format("%-10s (%4d arany)", "Könnyű", 1300)));
		difficultyMenu.addItem(new MenuItem(
				yellow,
				() -> money.set(1000),
				false,
				String.format("%-10s (%4d arany)", "Közepes", 1000)));
		difficultyMenu.addItem(new MenuItem(
				red,
				() -> money.set(700),
				false,
				String.format("%-10s (%4d arany)", "Nehéz", 700)));

		// Main menu
		// items
		mainMenu.addItem(new MenuItem(
				white,
				() -> skillPointMenu.display(),
				false,
				String.format("%-20s", "Tulajdonságpontok")));
		mainMenu.addItem(new MenuItem(
				white,
				() -> spellMenu.display(),
				false,
				String.format("%-20s", "Varázslatok")));
		mainMenu.addItem(new MenuItem(
				white,
				() -> unitsMenu.display(),
				false,
				String.format("%-20s", "Egységek")));
		mainMenu.addItem(new MenuItem(
				red,
				() -> update(), // TODO: Check if any units are present
				false,
				String.format("%-20s", "Befejezés")));

		// Skillpoint menu
		// headers
		skillPointMenu.addHeader(headerMoney);
		skillPointMenu.addHeader(new HeaderItem(white, "💲(Ár): %s", hero.skillPrice));

		// items
		for (var s : hero.getSkills()) {
			String key = s.getKey();
			Skill skill = s.getValue();

			skillPointMenu.addItem(new MenuItem(
					white,
					() -> {
						AtomicInteger skillPrice = hero.skillPrice;

						if (skillPrice.get() > money.get())
							return;
						if (!hero.addSkillValue(key, 1))
							return;

						money.addAndGet(-skillPrice.get());
					},
					true,
					// NAME (x/MAX-SKILL)
					String.format("%-15s", skill.name) + "(%2s/" + String.format("%2d)", Hero.MAX_SKILL),
					hero.getSkillValue(key)));
		}
		skillPointMenu.addItem(back);

		// Spell menu
		// headers
		spellMenu.addHeader(headerMoney);
		spellMenu.addHeader(spacer);
		spellMenu.addHeader(new HeaderItem(gray, "💲: Ár"));
		spellMenu.addHeader(new HeaderItem(gray, "💪: Manna"));

		// items
		for (var s : hero.getSpells()) {
			Spell spell = s.getValue();

			spellMenu.addItem(new MenuItem(
					white,
					() -> {
					}, // TODO
					true,
					String.format("%-15s (💲: %3d, 💪: %2d)", spell.getName(), spell.getPrice(), spell.getManna())));
		}
		spellMenu.addItem(back);

		// Units menu
		// headers
		unitsMenu.addHeader(headerMoney);
		unitsMenu.addHeader(spacer);
		unitsMenu.addHeader(new HeaderItem(gray, "💲: Ár"));
		unitsMenu.addHeader(new HeaderItem(gray, "⚔: Sebzés"));
		unitsMenu.addHeader(new HeaderItem(gray, "❤: Életérő"));
		unitsMenu.addHeader(new HeaderItem(gray, "🚀: Sebesség"));
		unitsMenu.addHeader(new HeaderItem(gray, "🙌: Kezdeményezés"));

		// items
		for (var s : hero.getUnits()) {
			Unit unit = s.getValue();

			unitsMenu.addItem(new MenuItem(
					white,
					() -> {
						int maxAmount = money.get() / unit.price;
						if (maxAmount == 0)
							return; // TODO: error message

						// int amount = IO.scanInt("Darab", 1, maxAmount);
						int amount = (int) IO.scanAndConvert(String.format("Darab [%d - %d]", 1, maxAmount),
								Converters.convertInt(1, maxAmount + 1)).get(0);

						unit.setCount(unit.getCount().get() + amount);

						money.addAndGet(-(amount * unit.price));
					},
					true,
					String.format("%-15s (💲: %2d, ⚔: %2d - %2d, ❤: %2d, 🚀: %2d, 🙌: %2d, ",
							unit.name, unit.price, unit.minDamage, unit.maxDamage, unit.health, unit.speed,
							unit.initiative)
							+ "%3s db)",
					unit.getCount()));
		}
		unitsMenu.addItem(back);

		difficultyMenu.display();
		mainMenu.display();
	}

	private Board board = new Board(hero);

	private void update() {
		board.drawBoard();
		board.placeUnits();

		Console.setCursorPosition(30, 0); // DEBUG
	}

	public void run() {
		this.init();
	}
}
