
package Hero;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import Base.Console;
import Base.Console.Alignment;
import Interfaces.Drawable;
import Base.Game;

import Spells.Fireball;
import Spells.Resurrection;
import Spells.Spell;
import Spells.Thunderbolt;
import Units.Unit;
import View.View;
import View.Colors.Colors;
import View.Colors.RGB;

public abstract class Hero implements Drawable {
	public final RGB COLOR;
	public final RGB TEXT_COLOR;
	public final View view;
	public final String name;

	public boolean usedAbility = false;
	public boolean usedUnit = false;
	
	protected Map<String, Skill> skills = new LinkedHashMap<>();
	protected Map<String, Spell> spells = new LinkedHashMap<>();
	protected List<Unit> units = new ArrayList<>();
	protected Hero enemy;
	protected int skillPrice = 5;
	protected int usedManna = 0;
	protected int money;

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

		unit.getTile().effect(Colors.RED, "hős");
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
	public List<Unit> getEnemyUnits() {
		return enemy.units.stream().filter(x -> !x.isDead()).toList();
	}
	public Unit getLowestHpUnit() {
		return this.getAliveUnits().stream().min(Comparator.comparingInt(Unit::getHealth)).get();
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

	
	public abstract void init();
	public abstract void placeUnits();
	public abstract void takeTurn(Unit unit);

}