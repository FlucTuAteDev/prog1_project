package Hero;

import java.util.Collection;
import java.util.LinkedHashMap;
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

	public Hero(RGB color) {
		skills.put("attack", new Skill("Támadás", 1, .1));
		skills.put("defense", new Skill("Védekezés", 1, .05));
		skills.put("magic", new Skill("Varázslat", 1, 1));
		skills.put("intelligence", new Skill("Tudás", 1, 10));
		skills.put("moral", new Skill("Morál", 1, 1));
		skills.put("luck", new Skill("Szerencse", 1, .05));

		spells.put("thunderbolt", new Thunderbolt());
		spells.put("fireball", new Fireball());
		spells.put("resurrect", new Resurrection());

		this.COLOR = color;
	}

	public Skill getSkill(String skill) {
		return skills.get(skill);
	}

	public Unit[] getUnitsFrom(Collection<Unit> units) {
		return units.stream().filter(x -> x.hero == this).toArray(Unit[]::new);
	}

	public Set<Entry<String, Skill>> getSkills() {
		return skills.entrySet();
	}

	public Collection<Skill> getSkillValues() {
		return skills.values();
	}

	public Set<Entry<String, Spell>> getSpells() {
		return spells.entrySet();
	}

	public Collection<Spell> getSpellValues() {
		return spells.values();
	}
}