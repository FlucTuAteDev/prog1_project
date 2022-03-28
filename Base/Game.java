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

	AtomicInteger money = new AtomicInteger(0);

	public void init() {
		Hero hero = new Hero();

		// Select difficulty
		Menu difficultyMenu = new Menu("Nehézségi szint");
		difficultyMenu.addItem(new MenuItem("Könnyű (1300 arany)", green, false,
				() -> money.set(1300)));
		difficultyMenu.addItem(new MenuItem("Közepes (1000 arany)", yellow, false,
				() -> money.set(1000)));
		difficultyMenu.addItem(new MenuItem("Nehéz (700 arany)", red, false,
				() -> money.set(700)));
		difficultyMenu.display();

		Menu mainMenu = new Menu("Főmenü");

		MenuItem back = new MenuItem("Vissza", white, false, () -> mainMenu.display());
		MenuItem headerMoney = new MenuItem("💰(Pénz): %s", white, false, null, money);
		MenuItem spacer = new MenuItem("", white, false, null);
		// Skillpoint menu
		Menu skillPointMenu = new Menu("Tulajdonságpontok");
		skillPointMenu.addHeader(headerMoney);
		skillPointMenu.addHeader(new MenuItem("💲(Ár): %s", white, false, null, hero.skillPrice));
		for (var s : hero.getSkills()) {
			String key = s.getKey();
			Skill skill = s.getValue();

			skillPointMenu.addItem(new MenuItem(skill.name + " (%s/10)", white, true,
					() -> {
						AtomicInteger skillPrice = hero.skillPrice;

						if (skillPrice.get() > money.get()) return;
						if (!hero.addSkillValue(key, 1)) return;						

						money.addAndGet(-skillPrice.get());
					},
					hero.getSkillValue(key)));
		}
		skillPointMenu.addItem(back);

		// Spell menu
		Menu spellMenu = new Menu("Varázslatok");
		spellMenu.addHeader(headerMoney);
		spellMenu.addHeader(spacer);
		spellMenu.addHeader(new MenuItem("💲: Ár", gray, false, null));
		spellMenu.addHeader(new MenuItem("💪: Manna", gray, false, null));
		for (var s : hero.getSpells()) {
			String key = s.getKey();
			Spell spell = s.getValue();

			spellMenu.addItem(new MenuItem(
				spell.getName() + " (💲: %d, 💪: %d)", 
				white, 
				true, 
				() -> {}, 
				spell.getPrice(), spell.getManna()));
		}
		spellMenu.addItem(back);

		// Units menu
		Menu unitsMenu = new Menu("Egységek");
		unitsMenu.addHeader(headerMoney);
		unitsMenu.addHeader(spacer);
		unitsMenu.addHeader(new MenuItem("💲: Ár", gray, false, null));
		unitsMenu.addHeader(new MenuItem("⚔: Sebzés", gray, false, null));
		unitsMenu.addHeader(new MenuItem("❤: Életérő", gray, false, null));
		unitsMenu.addHeader(new MenuItem("🚀: Sebesség", gray, false, null));
		unitsMenu.addHeader(new MenuItem("🙌: Kezdeményezés", gray, false, null));
		for (var s : hero.getUnits()) {
			String key = s.getKey();
			Unit unit = s.getValue();

			unitsMenu.addItem(new MenuItem(
				unit.getName() + " (💲: %d, ⚔: %d-%d, ❤: %d, 🚀: %d, 🙌: %d, %s db)", 
				white, 
				true, 
				() -> {
					int maxAmount = money.get() / unit.getPrice();
					if (maxAmount == 0) return; // TODO: error message

					int amount = IO.scanInt("Darab", 1, maxAmount);
					unit.setCount(unit.getCount().get() + amount);

					money.addAndGet(-(amount * unit.getPrice()));
				}, 
				unit.getPrice(), unit.getMinDamage(), unit.getMaxDamage(), unit.getHealth(), unit.getSpeed(), unit.getInitiative(), unit.getCount()));
		}
		unitsMenu.addItem(back);

		// Main menu
		mainMenu.addItem(new MenuItem("Tulajdonságpontok", white, false, () -> skillPointMenu.display()));
		mainMenu.addItem(new MenuItem("Varázslatok", white, false, () -> spellMenu.display()));
		mainMenu.addItem(new MenuItem("Egységek", white, false, () -> unitsMenu.display()));

		mainMenu.display();
	}

	public void drawBoard() {
		
	}

	public void run() {
		this.init();
		this.drawBoard();
	}
}
