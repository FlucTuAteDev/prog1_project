package Units;

import java.util.concurrent.atomic.AtomicInteger;

import Hero.Hero;

public abstract class Unit {
	Hero hero;
	String name;
	AtomicInteger count;
	int price;
	int minDamage;
	int maxDamage;
	int health;
	int speed;
	int initiative;

	public Unit(String name, int price, int minDamage, int maxDamage, int health, int speed, int initiative) {
		this.count = new AtomicInteger(0);
		this.name = name;
		this.price = price;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.health = health;
		this.speed = speed;
		this.initiative = initiative;
	}
	public String getName() {
		return name;
	}
	public int getPrice() {
		return price;
	}
	public int getMinDamage() {
		return minDamage;
	}
	public int getMaxDamage() {
		return maxDamage;
	}
	public int getHealth() {
		return health;
	}
	public int getSpeed() {
		return speed;
	}
	public int getInitiative() {
		return initiative;
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