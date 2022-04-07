package Hero;

public class Skill {
	public static final int MAX_SKILL = 10;

	public final String name;
	public final double multiplier;
	public final Hero hero;

	private int points;

	public Skill(String name, double multiplier, Hero hero) {
		this.name = name;
		this.points = 1;
		this.multiplier = multiplier;
		this.hero = hero;
	}

	public int getPoints() {
		return points;
	}

	public boolean addPoints(int amt) {
		if (amt < 1)
			return false;

		int res = this.points + amt;
		if (res > MAX_SKILL)
			return false;

		this.points = res;
		this.hero.incrementSkillPrice();
		return true;
	}

	public double getValue() {
		return (Math.abs(multiplier) < 1 ? 1 : 0) + points * multiplier;
	}
}