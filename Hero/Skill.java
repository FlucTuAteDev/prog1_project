package Hero;

public class Skill {
	public static final int MAX_SKILL = 10;
	public static int PRICE = 5;

	public final String name;
	public final double multiplier;

	private int skill;

	public Skill(String name, int value, double multiplier) {
		this.name = name;
		this.skill = value;
		this.multiplier = multiplier;
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
		PRICE = (int) Math.ceil((double) PRICE * 1.1);
		return true;
	}

	public double getValue() {
		return (Math.abs(skill) < 1 ? 1 : 0) + skill * multiplier;
	}
}