package Hero;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import Spells.Fireball;
import Spells.Resurrection;
import Spells.Spell;
import Spells.Thunderbolt;
import Units.*;
import Utils.*;

public class Hero {
	public final RGB COLOR;

	private Map<String, Skill> skills = new LinkedHashMap<>();
	private Map<String, Spell> spells = new LinkedHashMap<>();
	private List<Unit> units = new ArrayList<>();

	public Hero(RGB color) {
		skills.put("attack", new Skill("Támadás", 1, .1));
		skills.put("defense", new Skill("Védekezés", 1, -.05));
		skills.put("magic", new Skill("Varázslat", 1, 1));
		skills.put("intelligence", new Skill("Tudás", 1, 10));
		skills.put("moral", new Skill("Morál", 1, 1));
		skills.put("luck", new Skill("Szerencse", 1, .05));

		spells.put("fireball", new Fireball(this));
		spells.put("resurrect", new Resurrection(this));
		spells.put("thunderbolt", new Thunderbolt(this));

		this.COLOR = color;
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

	// public Unit[] getUnitsFrom(Collection<Unit> units) {
	// 	return units.stream().filter(x -> x.hero == this).toArray(Unit[]::new);
	// }

}