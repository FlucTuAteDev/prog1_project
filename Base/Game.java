package Base;

import java.util.concurrent.atomic.*;

import Hero.Hero;
import Hero.Skill;
import Menu.*;
import Spells.Spell;
import Units.Unit;
import Utils.*;

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
		Menu spellMenu = new Menu("Varázslatok");
		
		MenuItem back = new MenuItem(white, () -> mainMenu.display(), false, "Vissza");
		HeaderItem headerMoney = new HeaderItem(white, "💰(Pénz): %s", money);
		HeaderItem spacer = new HeaderItem(white, "");

		// Select difficulty
		difficultyMenu.addItem(new MenuItem(
			green, 
			() -> money.set(1300),
			false,
			String.format("%-10s (%4d arany)", "Könnyű", 1300)
		));
		difficultyMenu.addItem(new MenuItem(
			yellow, 
			() -> money.set(1000),
			false,
			String.format("%-10s (%4d arany)", "Közepes", 1000)
		));
		difficultyMenu.addItem(new MenuItem(
			red,
			() -> money.set(700),
			false,
			String.format("%-10s (%4d arany)", "Nehéz", 700)
		));
		difficultyMenu.display();

		// Skillpoint menu
		skillPointMenu.addHeader(headerMoney);
		skillPointMenu.addHeader(new HeaderItem(white, "💲(Ár): %s", hero.skillPrice));
		for (var s : hero.getSkills()) {
			String key = s.getKey();
			Skill skill = s.getValue();

			skillPointMenu.addItem(new MenuItem(
				white, 
				() -> {
					AtomicInteger skillPrice = hero.skillPrice;
					
					if (skillPrice.get() > money.get()) return;
					if (!hero.addSkillValue(key, 1)) return;						
					
					money.addAndGet(-skillPrice.get());
				},
				true,
				// NAME (x/MAX-SKILL)
				String.format("%-15s", skill.name) + "(%2s/" + String.format("%2d)", Hero.MAX_SKILL), 
				hero.getSkillValue(key)
			));
		}
		skillPointMenu.addItem(back);

		// Spell menu
		spellMenu.addHeader(headerMoney);
		spellMenu.addHeader(spacer);
		spellMenu.addHeader(new HeaderItem(gray, "💲: Ár"));
		spellMenu.addHeader(new HeaderItem(gray, "💪: Manna"));
		for (var s : hero.getSpells()) {
			String key = s.getKey();
			Spell spell = s.getValue();

			spellMenu.addItem(new MenuItem(
				white, 
				() -> {}, 
				true, 
				String.format("%-15s (💲: %3d, 💪: %2d)", spell.getName(), spell.getPrice(), spell.getManna())
			));
		}
		spellMenu.addItem(back);

		// Units menu
		Menu unitsMenu = new Menu("Egységek");
		unitsMenu.addHeader(headerMoney);
		unitsMenu.addHeader(spacer);
		unitsMenu.addHeader(new HeaderItem(gray, "💲: Ár"));
		unitsMenu.addHeader(new HeaderItem(gray, "⚔: Sebzés"));
		unitsMenu.addHeader(new HeaderItem(gray, "❤: Életérő"));
		unitsMenu.addHeader(new HeaderItem(gray, "🚀: Sebesség"));
		unitsMenu.addHeader(new HeaderItem(gray, "🙌: Kezdeményezés"));
		for (var s : hero.getUnits()) {
			String key = s.getKey();
			Unit unit = s.getValue();

			unitsMenu.addItem(new MenuItem(
				white, 
				() -> {
					int maxAmount = money.get() / unit.getPrice();
					if (maxAmount == 0) return; // TODO: error message
					
					int amount = IO.scanInt("Darab", 1, maxAmount);
					unit.setCount(unit.getCount().get() + amount);
					
					money.addAndGet(-(amount * unit.getPrice()));
				}, 
				true,
				String.format("%-15s (💲: %2d, ⚔: %2d - %2d, ❤: %2d, 🚀: %2d, 🙌: %2d,",
				unit.getName(), unit.getPrice(), unit.getMinDamage(), unit.getMaxDamage(), unit.getHealth(), unit.getSpeed(), unit.getInitiative())
				+ "%3s db)",
				unit.getCount()
			));
		}
		unitsMenu.addItem(back);

		// Main menu
		mainMenu.addItem(new MenuItem(white, () -> skillPointMenu.display(), false, String.format("%-20s", "Tulajdonságpontok")));
		mainMenu.addItem(new MenuItem(white, () -> spellMenu.display(), false, String.format("%-20s", "Varázslatok")));
		mainMenu.addItem(new MenuItem(white, () -> unitsMenu.display(), false, String.format("%-20s", "Egységek")));
		mainMenu.addItem(new MenuItem(red, () -> start(), false, String.format("%-20s", "Befejezés")));

		mainMenu.display();
	}

	public void start() {
		
	}

	public void run() {
		this.init();
		this.start();
	}
}
