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

public class Hero {
	public static final int MAX_SKILL = 10;

	private Map<String, Skill> skills = new LinkedHashMap<>();
	private Map<String, Spell> spells = new LinkedHashMap<>();

	private int skillPrice = 5;

	public Hero() {
		skills.put("attack", new Skill("Támadás", 1));
		skills.put("defense", new Skill("Védekezés", 1));
		skills.put("magic", new Skill("Varázslat", 1));
		skills.put("intelligence", new Skill("Tudás", 1));
		skills.put("moral", new Skill("Morál", 1));
		skills.put("luck", new Skill("Szerencse", 1));

		spells.put("thunderbolt", new Thunderbolt());
		spells.put("fireball", new Fireball());
		spells.put("resurrect", new Resurrection());
	}

	private void incrementPrice() {
		this.skillPrice = (int)Math.ceil((double)this.skillPrice * 1.1);
	}
	
	public int getSkillValue(String skill) { return skills.get(skill).value; }
	public boolean addSkillValue(String skillName, int amount) {
		if (amount < 1) return false;

		Skill skill = skills.get(skillName);
		if (skill.value + amount > MAX_SKILL) return false;

		skill.value += amount;
		incrementPrice();
		return true;
	}

	public Unit[] getUnitsFrom(Collection<Unit> units) {
		return units.stream().filter(x -> x.hero == this).toArray(Unit[]::new);
	}

	public int getSkillPrice() { return skillPrice; }

	public Set<Entry<String, Skill>> getSkills() { return skills.entrySet(); }
	public Collection<Skill> getSkillValues() { return skills.values(); }
	public Set<Entry<String, Spell>> getSpells() { return spells.entrySet(); }
	public Collection<Spell> getSpellValues() { return spells.values(); }

	public int getManna() { return skills.get("intelligence").value * 10; }

}