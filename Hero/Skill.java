package Hero;

import Base.Game;

/**
 * Handles the hero's skill points
 */
public class Skill {
	public static final int MAX_SKILL = 10;

	public final String name;
	public final double multiplier;
	public final Hero hero;

	private int points;

	public Skill(String name, double multiplier, Hero hero) {
		this.name = name;
		this.multiplier = multiplier;
		this.hero = hero;

		this.points = 1;
	}

	public int getPoints() {
		return points;
	}

	public double getValue() {
		return (Math.abs(multiplier) < 1 ? 1 : 0) + points * multiplier;
	}

	public boolean buy() {
		if (this.points + 1 > MAX_SKILL) {
			Game.logError("Ebből a képességből már nem vehetsz többet!");
			return false;
		}

		if (!this.hero.takeMoney(this.hero.getSkillPrice())) {
			Game.logError("Nincs elég pénzed több képességre!");
			return false;
		}

		this.points++;
		this.hero.incrementSkillPrice();
		return true;
	}
}