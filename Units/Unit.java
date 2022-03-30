package Units;

import java.util.concurrent.atomic.AtomicInteger;

import Hero.Hero;

public abstract class Unit {
	Hero hero;
	AtomicInteger count;
	public final String name;
	public final String icon;

	public final int price;
	public final int minDamage;
	public final int maxDamage;
	public final int health;
	public final int speed;
	public final int initiative;

	public Unit(String name, String icon, int price, int minDamage, int maxDamage, int health, int speed, int initiative) {
		this.name = name;
		this.icon = icon;
		this.count = new AtomicInteger(0);

		this.price = price;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.health = health;
		this.speed = speed;
		this.initiative = initiative;
	}

	public void setHero(Hero hero) {
		if (hero == null) return;
		this.hero = hero;
	}

	public AtomicInteger getCount() {
		return this.count;
	}
	
	public void setCount(int count) {
		if (count < 0) this.count.set(0);
		else this.count.set(count);
	}
}