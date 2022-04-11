package Units;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import Base.Console;
import Base.Game;
import Base.Console.Alignment;
import Board.Tile;
import Hero.Hero;
import Spells.Spell;
import View.Drawable;
import View.Colors.Colors;
import View.Colors.RGB;

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

		Game.logMessage("%s elmozgott: %s -> %s",
			this,
			this.tile, tile);

		this.tile.setUnit(null);
		setTile(tile);
		return true;
	}

	private void heal(double amt) {
		this.health = Math.min(this.health + (int)Math.round(amt), this.maxCount * this.baseHealth);
		this.count = (int)Math.ceil(this.health / this.baseHealth);
		draw();
	}
	public void heal(Spell spell) {
		int amt = (int)Math.round(spell.getValue());

		int prevHealth = this.health;
		int prevCount = this.count;
		heal(amt);
		Game.logMessage("%s 🧪 %s: +%d❤ -> +%ddb", 
			hero, this,
			this.health - prevHealth, this.count - prevCount);
	}

	private void takeDamage(int amt) {
		int res = this.health - (int)Math.round(amt);
		if (res < 0) {
			this.health = 0;
			this.count = 0;
			this.tile.setUnit(null);
			Game.logMessage("%s 💀", this);
		} else {
			this.health = res;
			this.count = (int)Math.ceil((double)this.health / this.baseHealth);
		}
		draw();
	}
	public void takeDamage(Unit other) {
		double attack = other.hero.getSkill("attack").getValue();
		double defense = this.hero.getSkill("defense").getValue();
		double luck = other.hero.getSkill("luck").getValue() - 1;

		boolean crit = random.nextInt(0, (int) (1 / luck)) == 0;
		int damage = (int)Math.round(other.getDamage() * attack * defense * (crit ? 2 : 1));

		Game.logMessage("%s ⚔ %s: %s-%d❤ -> -%ddb", 
			other, this,
			crit ? "KRITIKUS " : "",
			damage, damage / this.baseHealth);

		takeDamage(damage);
	}
	public void takeDamage(Hero hero) {
		int damage = hero.getSkill("attack").getPoints() * 10;

		Game.logMessage("%s ⚔ %s: -%d❤ -> -%ddb", 
			hero, this,
			damage, damage / this.baseHealth);

		takeDamage(damage);
	}
	public void takeDamage(Spell spell) {
		int damage = (int)Math.round(spell.getValue());

		Hero hero = spell.hero;
		Game.logMessage("%s 🧪 %s: -%d❤ -> -%ddb", 
			hero, this,
			damage, damage / this.baseHealth);
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

	public boolean isDead() {
		return this.health <= 0;
	}

	public int getInitiative() {
		return this.baseInitiative + (int)this.hero.getSkill("moral").getValue();
	}

	public List<Unit> attackableUnits() {
		return Arrays.stream(tile.getNeighbours())
			.filter(x -> x.hasUnit() && x.getUnit().hero != this.hero)
			.map(x -> x.getUnit()).toList();
	}

	public void highlight() {
		if (this.tile == null) return;

		RGB color = Colors.brighten(this.hero.COLOR, .5);
		Colors.setColors(color);
		this.tile.draw(this.icon, String.valueOf(this.getCount()));
		Console.resetStyles();
	}

	@Override
	public int compareTo(Unit other) {
		// Descending order
		return other.getInitiative() - this.getInitiative();
	}

	@Override
	public void draw() {
		if (this.tile == null) return;

		if (this.isDead()) {
			this.tile.draw();
			return;
		}

		this.hero.setColors();
		this.tile.draw(this.icon, String.valueOf(this.getCount()));
		Console.resetStyles();
	}

	@Override
	public String toString() {
		return Colors.wrapWithColor(" " + this.icon + " ", this.hero.COLOR, this.hero.TEXT_COLOR);
	}
}