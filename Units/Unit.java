package Units;

import Hero.Hero;

public abstract class Unit implements Comparable<Unit> {
	int count;
	
	public final String name;
	public final Hero hero;
	public final String icon;
	public final int price;
	public final int minDamage;
	public final int maxDamage;
	public final int health;
	public final int speed;
	public final int initiative;

	public Unit(String name, String icon, Hero hero, int price, int minDamage, int maxDamage, int health, int speed, int initiative) {
		this.name = name;
		this.icon = icon;
		this.hero = hero;
		
		this.price = price;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.health = health;
		this.speed = speed;
		this.initiative = initiative;
		
		this.count = 0;
	}

	public int getCount() {
		return this.count;
	}
	
	public void setCount(int count) {
		if (count < 0) this.count = 0;
		else this.count = count;
	}

	@Override
	public int compareTo(Unit other) {
		// Descending order
		return other.initiative - this.initiative;
	}
}