package Spells;

import Board.Tile;
import Hero.Hero;
import View.Colors.RGB;

/**
 * Defines how a spell should behave
 */
public abstract class Spell {
	public final String name;
	public final String icon;
	public final RGB color;
	public final int price;
	public final int manna;
	public final int multiplier;
	public final Hero hero;

	private boolean active = false;

	public Spell(String name, String icon, RGB color, int price, int manna, int multiplier, Hero hero) {
		this.name = name;
		this.icon = icon;
		this.color = color;
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

	public boolean buy() {
		if (this.isActive() || !this.hero.takeMoney(this.price))
			return false;

		this.setActive();
		return true;
	}

	/**
	 * Cast the spell on the given {@code tile}
	 */
	public abstract void cast(Tile tile);
	/**
	 * Generates a tile to cast the spell on
	 */
	public abstract Tile generate();
	/**
	 * Scans a tile from the user to cast the spell on
	 */
	public abstract Tile scan();
}
