package Spells;

import Hero.Hero;

public abstract class Spell {
	public final String name;
	public final int price;
	public final int manna;
	public final int multiplier;
	public final Hero hero;

	private boolean active = false;

	public Spell(String name, int price, int manna, int multiplier, Hero hero) {
		this.name = name;
		this.price = price;
		this.manna = manna;
		this.multiplier = multiplier;
		this.hero = hero;
	}

	public boolean isActive() {
		return this.active;
	}
	public void setActive() {
		this.active = true;
	}

	public double getValue() {
		return multiplier * hero.getSkill("magic").getValue();
	}

	public abstract void cast();
}
