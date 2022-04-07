package Hero;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import Base.Console;
import Base.Console.Alignment;
import Spells.Fireball;
import Spells.Resurrection;
import Spells.Spell;
import Spells.Thunderbolt;
import Units.*;
import Utils.*;
import View.Drawable;
import View.View;
import View.Colors.Colors;
import View.Colors.RGB;

public class Hero implements Drawable {
	public final RGB COLOR;
	public final RGB TEXT_COLOR;

	private Map<String, Skill> skills = new LinkedHashMap<>();
	private Map<String, Spell> spells = new LinkedHashMap<>();
	private List<Unit> units = new ArrayList<>();
	private int skillPrice = 5;
	public final View view;
	public final String name;

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
		unit.takeDamage(this);
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

	public List<Unit> getUnits() {
		return units;
	}
	public void addUnit(Unit unit) {
		this.units.add(unit);
	}
	public void addUnits(Collection<Unit> units) {
		this.units.addAll(units);
	}

	public void incrementSkillPrice() {
		this.skillPrice = (int)Math.ceil(1.1 * this.skillPrice);
	}
	public int getSkillPrice() {
		return this.skillPrice;
	}

	@Override
	public void draw() {
		view.clear();
		Console.printAligned(Alignment.CENTER, view.width, Colors.wrapWithColor(" " + name + " ", this.COLOR, this.TEXT_COLOR)); 
	}

	// public Unit[] getUnitsFrom(Collection<Unit> units) {
	// 	return units.stream().filter(x -> x.hero == this).toArray(Unit[]::new);
	// }

}