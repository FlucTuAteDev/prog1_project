package Units;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import Base.Console;
import Base.Console.Alignment;
import Board.Tile;
import Hero.Hero;
import View.Drawable;

public abstract class Unit implements Comparable<Unit>, Drawable {
	public final String name;
	public final Hero hero;
	public final String icon;
	public final int price;
	public final int minDamage;
	public final int maxDamage;
	public final int baseHealth;
	public final int speed;
	public final int baseInitiative;
	
	private int count;
	private int maxCount;
	private int health;
	
	private Tile tile;
	
	Random random = new Random();
	
	public Unit(String name, String icon, Hero hero, int price, int minDamage, int maxDamage, int baseHealth, int speed,
			int baseInitiative) {
		this.name = name;
		this.icon = icon;
		this.hero = hero;

		this.price = price;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.baseHealth = baseHealth;
		this.speed = speed;
		this.baseInitiative = baseInitiative;

		this.count = 0;
		this.maxCount = 0;
		this.health = 0;
	}

	public boolean canMoveTo(Tile tile) {
		return Tile.distance(this.tile, tile) <= speed;
	}
	public boolean move(Tile tile) {
		if (tile.hasUnit() || !canMoveTo(tile))
			return false;

		this.tile.setUnit(null);
		setTile(tile);
		return true;
	}

	public void heal(double amt) {
		this.health = Math.min(this.health + (int)Math.round(amt), this.maxCount * this.baseHealth);
		this.count = (int)Math.ceil(this.health / this.baseHealth);
		draw();
	}
	public void takeDamage(double amt) {
		int res = this.health - (int)Math.round(amt);
		if (res < 0) {
			this.health = 0;
			this.count = 0;
			return;
		}

		this.health = res;
		this.count = (int)Math.ceil(this.health / this.baseHealth);
		draw();
	}
	public void takeDamage(Unit other) {
		double attack = other.hero.getSkill("attack").getValue();
		double defense = this.hero.getSkill("defense").getValue();
		double luck = other.hero.getSkill("luck").getValue() - 1;

		boolean crit = random.nextInt(0, (int) (1 / luck)) == 0;
		double damage = other.getDamage() * attack * defense * (crit ? 2 : 1);

		takeDamage(damage);
	}
	public void takeDamage(Hero hero) {
		double damage = hero.getSkill("attack").getPoints() * 10;

		takeDamage(damage);
	}
	public int getDamage() {
		if (this.minDamage == this.maxDamage) return this.minDamage;

		return random.nextInt(this.minDamage, this.maxDamage + 1) * this.count;
	}

	public int getCount() {
		return this.count;
	}
	public int getMaxCount() {
		return this.maxCount;
	}
	public void setMaxCount(int count) {
		if (count < 0)
			this.maxCount = 0;
		else
			this.maxCount = count;

		this.health = this.baseHealth * this.maxCount;
		this.count = this.maxCount;
	}

	public Tile getTile() {
		return this.tile;
	}
	public void setTile(Tile tile) {
		if (tile == this.tile) return;

		Tile previous = this.tile;
		this.tile = tile;

		if (previous != null) 
			previous.setUnit(null);

		if (tile != null)
			tile.setUnit(this);
	}

	public int getHealth() {
		return this.health;
	}

	public int getInitiative() {
		return this.baseInitiative + (int)this.hero.getSkill("moral").getValue();
	}

	public List<Unit> attackableUnits() {
		return Arrays.stream(tile.getNeighbours())
			.filter(x -> x.hasUnit() && x.getUnit().hero != this.hero)
			.map(x -> x.getUnit()).toList();
	}

	@Override
	public int compareTo(Unit other) {
		// Descending order
		return other.getInitiative() - this.getInitiative();
	}

	@Override
	public void draw() {
		if (this.tile == null) return;

		this.hero.setColors();
		this.tile.draw(this.icon, String.valueOf(this.getCount()));
		Console.resetStyles();
	}
}