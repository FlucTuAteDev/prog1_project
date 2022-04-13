package Units;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import Base.Console;
import Base.Game;
import Board.Tile;
import Hero.Hero;
import View.Drawable;
import View.Colors.Colors;

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
	private boolean selected = false;
	protected boolean canCounter;
	
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

	public void heal(double amt) {
		this.health = Math.min(this.health + (int)Math.round(amt), this.maxCount * this.baseHealth);
		this.count = (int)Math.ceil(this.health / this.baseHealth);
		draw();
	}

	public void takeDamage(int amt) {
		int res = this.health - (int)Math.round(amt);
		if (res < 0) {
			this.health = 0;
			this.count = 0;
			this.tile.setUnit(null);
			Game.logMessage("%s 💀", this);
		} else {
			this.health = res;
			this.count = (int)Math.ceil((double)this.health / this.baseHealth);
			draw();
		}
	}
	public double getDamage(Unit defender) {
		double res = 0;
		if (this.minDamage == this.maxDamage) res = this.minDamage;
		else res = random.nextInt(this.minDamage, this.maxDamage + 1);

		double attack = this.hero.getSkill("attack").getValue();
		double defense = defender.hero.getSkill("defense").getValue();
		res *= this.count * attack * defense;

		return res;
	}

	public void attack(Unit other) {
		double luck = this.hero.getSkill("luck").getValue() - 1;

		boolean crit = random.nextInt(0, (int) (1 / luck)) == 0;
		int damage = (int)Math.round(this.getDamage(other) * (crit ? 2 : 1));

		Game.logMessage("%s ⚔ %s: %s-%d❤ -> -%ddb", 
			this, other,
			crit ? "KRITIKUS " : "",
			damage, damage / other.baseHealth);

		other.takeDamage(damage);
		other.counterAttack(this);
	}
	public void counterAttack(Unit other) {
		if (!this.canCounter) return;
		if (this.isDead()) return;
		if (!this.tile.isNeighbour(other.getTile())) return;

		int damage = (int)Math.round(this.getDamage(other) / 2);

		Game.logMessage("%s visszatámad %s-re: -%d❤️ -> -%ddb",
			this, other,
			damage, damage / this.baseHealth);

		other.takeDamage(damage);
		
		canCounter = false;
	}
	public void enableCounter() {
		this.canCounter = true;
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

	public void select() {
		if (this.tile == null) return;

		this.selected = true;
		draw();
	}
	public void deselect() {
		if (this.tile == null) return;

		this.selected = false;
		draw();
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
			this.setTile(null);
			return;
		}

		if (this.selected) Colors.setBgWithFg(Colors.brighten(this.hero.COLOR, .5));
		else Colors.setBgWithFg(this.hero.COLOR);

		this.tile.draw(this.icon, String.valueOf(this.getCount()));
		Console.resetStyles();
	}

	@Override
	public String toString() {
		return Colors.wrapWithColor(" " + this.icon + " ", this.hero.COLOR, this.hero.TEXT_COLOR);
	}
}