package Hero;

public class Skill {
	public static final int MAX_SKILL = 10;

	public final String name;
	public final double multiplier;
	public final Hero hero;

	private int skill;

	public Skill(String name, double multiplier, Hero hero) {
		this.name = name;
		this.skill = 1;
		this.multiplier = multiplier;
		this.hero = hero;
	}

	public int getSkill() {
		return skill;
	}

	public boolean addSkill(int amt) {
		if (amt < 1)
			return false;

		int res = this.skill + amt;
		if (res > MAX_SKILL)
			return false;

		this.skill = res;
		this.hero.incrementSkillPrice();
		return true;
	}

	public double getValue() {
		return (Math.abs(multiplier) < 1 ? 1 : 0) + skill * multiplier;
	}
}