package Hero;

import java.util.List;

import Base.Console;
import Base.Game;
import Board.Board;
import Board.Tile;
import Menu.BasicMenu;
import Menu.InitMenu;
import Menu.Menu;
import Menu.Items.HeaderItem;
import Menu.Items.MenuItem;
import Spells.Spell;
import Units.Unit;
import View.View;
import View.Colors.Colors;
import View.Colors.RGB;

/**
 * A hero that is controlled by the player
 */
public class Player extends Hero {
	Menu<Object> actionMenu = new BasicMenu<>("Lehetőségek:", Game.menuView);
	Menu<Unit> unitAttackableMenu = new BasicMenu<>("Megtámadható egységek:", Game.menuView);
	Menu<Unit> heroAttackableMenu = new BasicMenu<>("Megtámadható egységek:", Game.menuView);
	Menu<Spell> spellMenu = new BasicMenu<>("Választható spellek:", Game.menuView);

	public Player(String name, RGB color, View view) {
		super(name, color, view);
	}

	@Override
	public void init() {
		View initView = new View(1, 1, Console.WIDTH, Console.HEIGHT - 1);
		Menu<Integer> difficultyMenu = new InitMenu<>("Nehézségi szint", initView);
		Menu<Object> mainMenu = new InitMenu<>("Főmenü", initView);
		Menu<Skill> skillMenu = new InitMenu<>("Tulajdonságpontok", initView);
		Menu<Unit> unitMenu = new InitMenu<>("Egységek", initView);
		Menu<Spell> spellMenu = new InitMenu<>("Varázslatok", initView);

		HeaderItem moneyHeader = new HeaderItem("💰(Pénz): %s", this::getMoney);
		HeaderItem spacer = new HeaderItem("");

		// Difficulity
		var difficulityItems = List.of(
			List.of(1300, "Könnyű", Colors.GREEN),
			List.of(1000, "Közepes", Colors.YELLOW),
			List.of(700, "Nehéz", Colors.RED)
		);
		for (var item : difficulityItems) {
			difficultyMenu.addItem(new MenuItem<>((int)item.get(0), mainMenu, (RGB)item.get(2), 
				this::setMoney, 
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
					if (Game.getUnits().stream().allMatch(x -> x.getCount() == 0)) {
						Game.logError("Legalább egy egységet vársárolni kell, mielőtt csatába mész!");
						mainMenu.display();
					}
				}, 
				"Befejezés"));

		// Skills
		skillMenu.addHeader(moneyHeader);
		skillMenu.addHeader(new HeaderItem("💲(Ár): %s", this::getSkillPrice));
		for (Skill skill : this.getSkillValues()) {
			skillMenu.addItem(new MenuItem<Skill>(skill, skillMenu,
				Skill::buy, "%-15s (%2s/%2s)", v -> v.name, Skill::getPoints, v -> Skill.MAX_SKILL));
		}
		skillMenu.addItem(new MenuItem<>(null, mainMenu, Colors.RED, v -> {}, "Vissza"));

		// Spells
		spellMenu.addHeader(moneyHeader);
		spellMenu.addHeader(spacer);
		spellMenu.addHeader(new HeaderItem(Colors.GRAY, "💲: Ár"));
		spellMenu.addHeader(new HeaderItem(Colors.GRAY, "💪: Manna"));
		for (Spell spell : this.getSpellValues()) {
			spellMenu.addItem(new MenuItem<>(spell, spellMenu,
					Spell::buy,
					"%-15s (💲: %3s, 💪: %2s 📜: %-30s) %s",
					v -> v.name, v -> v.price, v -> v.manna, v -> v.desc, v -> v.isActive() ? "✔" : "✖"));
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
		unitMenu.addHeader(new HeaderItem(Colors.GRAY, "🤹: Speciális képesség"));
		for (Unit unit : this.getUnits()) {
			unitMenu.addItem(new MenuItem<>(unit, unitMenu,
					v -> {
						int maxAmount = v.hero.getMoney() / v.price;
						if (maxAmount == 0) {
							Game.logError("Nincs elég pénzed erre az egységre!");
							return;
						}

						int amount = Console.scanInt("Darab", 0, maxAmount);
						v.buy(amount);
					},
					"%-15s (💲: %2s, ⚔: %2s - %2s, ❤: %2s, 🚀: %2s, 🙌: %2s, 🤹: %-25s %3s db)",
					v -> v.name, v -> v.price, v -> v.minDamage, v -> v.maxDamage, v -> v.baseHealth, v -> v.speed,
					v -> v.baseInitiative, v -> v.special , Unit::getCount));
		}
		unitMenu.addItem(new MenuItem<>(null, mainMenu, Colors.RED, v -> {}, "Vissza"));

		Console.clearScreen();
		difficultyMenu.display();
	}

	@Override
	public void placeUnits() {
		// Asks the user where to draw each unit
		for (Unit unit : this.getUnits()) {
			if (unit.getCount() == 0) continue;

			Game.actionView.clear();
			Console.println("Válaszd ki, hogy hova rakod: %s (%s, %d db)", unit.name, unit.icon, unit.getCount());

			Tile tile = Console.scanTile(0, Board.ROWS, this.view.left == 1 ? 0 : Board.COLS - 2, this.view.left == 1 ? 2 : Board.COLS, 
				x -> x.hasUnit() ? "Az adott cellán már tartózkodik egység" : null);

			unit.setTile(tile);
		}
	}

	@Override
	public void takeTurn(Unit unit) {
		if (!this.units.contains(unit))
			throw new IllegalStateException("Can't take turn with another hero's unit");

		// Unit attacks
		for (Unit attackableUnit : unit.attackableUnits()) {
			unitAttackableMenu.addItem(new MenuItem<>(attackableUnit, null,
					Colors.textFromBg(attackableUnit.hero.COLOR),
					attackableUnit.hero.COLOR,
					v -> {
						unit.attack(v);
						usedUnit = true;
					},
					"%s", v -> v.icon));
		}
		unitAttackableMenu.addItem(new MenuItem<>(null, actionMenu, Colors.RED, v -> {}, "Vissza"));
		
		var usableSpells = getActiveSpells().stream().filter(x -> x.manna <= getManna()).toList();
		if (!usedAbility) {
			// Hero attacks
			for (Unit attackableUnit : enemy.getAliveUnits()) {
				heroAttackableMenu.addItem(new MenuItem<>(attackableUnit, null,
					Colors.textFromBg(enemy.COLOR), 
					enemy.COLOR, 
					v -> {
						attack(v);
						usedAbility = true;
					}, "%s", v -> v.icon));
			}
			heroAttackableMenu.addItem(new MenuItem<>(null, actionMenu, Colors.RED, v -> {}, "Vissza"));
			
			// Hero spells
			for (Spell spell : usableSpells) {
				spellMenu.addItem(new MenuItem<>(spell, null,
				v -> {
					v.cast(v.scan());
					usedAbility = true;
				}, 
				"%s - %s", v -> v.icon, v -> v.name));
			}
			spellMenu.addItem(new MenuItem<>(null, actionMenu, Colors.RED, v -> {}, "Vissza"));
		}
		
		// Actions
		actionMenu.addItem(new MenuItem<>(null, null,
			v -> {
				Console.scanTile(
					x -> x.hasUnit() ? "Az adott cellán már tartózkodik egység" : null,
					x -> !unit.move(x) ? "Az egység nem tud a megadott cellára lépni!" : null
				);
				usedUnit = true;
		}, "Egység -> Mozgás"));
		actionMenu.addItem(new MenuItem<>(null, null, v -> {usedUnit = true;}, "Egység -> Várakozás"));

		if (unit.attackableUnits().size() != 0)
			actionMenu.addItem(new MenuItem<>(null, unitAttackableMenu, v -> {}, "Egység -> Támadás"));

		if (!usedAbility) {
			if (enemy.getAliveUnits().size() != 0)
				actionMenu.addItem(new MenuItem<>(null, heroAttackableMenu, v -> {}, "Hős -> Támadás"));
			
			if (usableSpells.size() != 0)
				actionMenu.addItem(new MenuItem<>(null, spellMenu, v -> {}, "Hős -> Varázslás"));
		}

		actionMenu.display();

		actionMenu.clearItems();
		unitAttackableMenu.clearItems();
		heroAttackableMenu.clearItems();
		spellMenu.clearItems();
	}
}
