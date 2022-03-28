package Hero;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import Spells.Fireball;
import Spells.Resurrection;
import Spells.Spell;
import Spells.Thunderbolt;
import Units.*;

public class Hero {
	public static final int MAX_SKILL = 10;

	private Map<String, Skill> skills = new LinkedHashMap<>();
	private Map<String, Spell> spells = new LinkedHashMap<>();
	private Map<String, Unit> units = new LinkedHashMap<>();

	public AtomicInteger skillPrice = new AtomicInteger(5);

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

		units.put("farmer", new Farmer());
		units.put("archer", new Archer());
		units.put("griff", new Griff());
	}

	private static void incrementPrice(AtomicInteger a) {
		int res = (int)Math.ceil((double)a.get() * 1.1);
		a.set(res);
	}
	
	public AtomicInteger getSkillValue(String skill) { return skills.get(skill).value; }
	public boolean addSkillValue(String skill, int amount) {
		if (amount < 1) return false;

		AtomicInteger skillVal = skills.get(skill).value;
		if (skillVal.get() + amount > MAX_SKILL) return false;

		skillVal.addAndGet(amount);
		incrementPrice(this.skillPrice);
		return true;
	}

	public Set<Entry<String, Skill>> getSkills() { return skills.entrySet(); }
	public Set<Entry<String, Spell>> getSpells() { return spells.entrySet(); }
	public Set<Entry<String, Unit>> getUnits() { return units.entrySet(); }

	public int getManna() { return skills.get("intelligence").value.get() * 10; }

}