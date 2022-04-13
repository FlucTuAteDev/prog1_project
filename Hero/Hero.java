
package Hero;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import Base.Console;
import Base.Game;
import Base.Console.Alignment;
import Menu.Menu;
import Menu.Items.MenuItem;
import Menu.BasicMenu;
import Spells.Fireball;
import Spells.Resurrection;
import Spells.Spell;
import Spells.Thunderbolt;
import Units.*;
import View.Drawable;
import View.View;
import View.Colors.Colors;
import View.Colors.RGB;

public class Hero implements Drawable {
	public final RGB COLOR;
	public final RGB TEXT_COLOR;
	public final View view;
	public final String name;

	public boolean usedAbility = false;

	private Map<String, Skill> skills = new LinkedHashMap<>();
	private Map<String, Spell> spells = new LinkedHashMap<>();
	private List<Unit> units = new ArrayList<>();
	private int skillPrice = 5;
	private int usedManna = 0;
	private int money;

	private Hero enemy;

	public Hero(String name, RGB color, View view) {
		this.name = name; 
		this.COLOR = color;
		this.TEXT_COLOR = Colors.textFromBg(color);
		this.view = view;

		skills.put("attack", new Skill("Támadás", .1, this));
		skills.put("defense", new Skill("Védekezés", -.05, this));
		skills.put("magic", new Skill("Varázslat", 1, this));
		skills.put("intelligence", new Skill("Tudás", 10, this));
		skills.put("moral", new Skill("Morál", 1, this));
		skills.put("luck", new Skill("Szerencse", .05, this));

		spells.put("fireball", new Fireball(this));
		spells.put("resurrect", new Resurrection(this));
		spells.put("thunderbolt", new Thunderbolt(this));
	}

	public void attack(Unit unit) {
		int damage = this.getSkill("attack").getPoints() * 10;

		Game.logMessage("%s ⚔ %s: -%d❤ -> -%ddb", 
			this, unit,
			damage, damage / unit.baseHealth);

		unit.takeDamage(damage);
	}

	public int getMoney() {
		return this.money;
	}
	public void setMoney(int amt) {
		this.money = amt;
	}
	public boolean takeMoney(int amt) {
		if (amt > this.money) return false;

		this.money -= amt;
		return true;
	}

	public Set<Entry<String, Skill>> getSkills() {
		return skills.entrySet();
	}
	public Collection<Skill> getSkillValues() {
		return skills.values();
	}
	public Skill getSkill(String skill) {
		return skills.get(skill);
	}
	
	public Set<Entry<String, Spell>> getSpells() {
		return spells.entrySet();
	}
	public Collection<Spell> getSpellValues() {
		return spells.values();
	}
	public List<Spell> getActiveSpells() {
		return spells.values().stream().filter(x -> x.isActive()).toList();
	}

	public List<Unit> getUnits() {
		return units;
	}
	public List<Unit> getAliveUnits() {
		return units.stream().filter(x -> !x.isDead()).toList();
	}
	public void addUnit(Unit unit) {
		this.units.add(unit);
		Collections.sort(this.units);
	}
	public void addUnits(Collection<Unit> units) {
		this.units.addAll(units);
		Collections.sort(this.units);
	}

	public void incrementSkillPrice() {
		this.skillPrice = (int)Math.ceil(1.1 * this.skillPrice);
	}
	public int getSkillPrice() {
		return this.skillPrice;
	}

	public int getManna() {
		return (int)getSkill("intelligence").getValue() - usedManna;
	}
	public boolean useManna(int amt) {
		int res = getManna() - amt;
		if (res < 0) return false;

		usedManna += amt;
		Console.saveCursor();
		draw();
		Console.restoreCursor();
		return true;
	}

	public void setColors() {
		Console.setBackground(this.COLOR);
		Console.setForeground(this.TEXT_COLOR);
	}

	public void setEnemy(Hero enemy) {
		this.enemy = enemy;
	}
	public Hero getEnemy() {
		return this.enemy;
	}

	@Override
	public void draw() {
		view.clear();
		view.printlnAligned(Alignment.CENTER, Colors.wrapWithColor(" %s - %d manna ", this.COLOR, this.TEXT_COLOR), this.name, this.getManna());
		view.println("");

		for (Unit unit : units) {
			view.printlnAligned(Alignment.CENTER, 
				Colors.wrapWithColor(" %-10s %s(%2d/%2d) ", this.COLOR, this.TEXT_COLOR), 
				unit.name, unit.icon, unit.getCount(), unit.getMaxCount());
		}
		
		view.println("");
		
		for (Skill skill : skills.values()) {
			view.printlnAligned(Alignment.CENTER, 
			"%-10s - %2d/%2d", 
			skill.name, skill.getPoints(), Skill.MAX_SKILL);
		}

		view.println("");
		
		for (Spell spell : spells.values().stream().filter(x -> x.isActive()).toList()) {
			view.printlnAligned(Alignment.CENTER, 
			"%-15s %3s",
			spell.name, spell.icon);
		}
		// view.println("", args);
	}

	@Override
	public String toString() {
		return Colors.wrapWithColor(" " + this.name + " ", COLOR, TEXT_COLOR);
	}

	Menu<Object> actionMenu = new BasicMenu<>("Lehetőségek:", Game.menuView);
	Menu<Unit> unitAttackableMenu = new BasicMenu<>("Megtámadható egységek:", Game.menuView);
	Menu<Unit> heroAttackableMenu = new BasicMenu<>("Megtámadható egységek:", Game.menuView);
	Menu<Spell> spellMenu = new BasicMenu<>("Választható spellek:", Game.menuView);
	public void takeTurn(Unit unit) {
		if (!this.units.contains(unit))
			throw new RuntimeException("Can't take turn with another hero's unit");

		// Unit attacks
		for (Unit attackableUnit : unit.attackableUnits()) {
			unitAttackableMenu.addItem(new MenuItem<>(attackableUnit, null,
					Colors.textFromBg(attackableUnit.hero.COLOR),
					attackableUnit.hero.COLOR,
					v -> unit.attack(v),
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
					v.cast();
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
		}, "Egység -> Mozgás"));
		actionMenu.addItem(new MenuItem<>(null, null, v -> {}, "Egység -> Várakozás"));

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

	// public Unit[] getUnitsFrom(Collection<Unit> units) {
	// 	return units.stream().filter(x -> x.hero == this).toArray(Unit[]::new);
	// }

}