package Spells;

import Base.Console;
import Board.Tile;
import Hero.Hero;
import View.Colors.Colors;
import View.Colors.RGB;

public abstract class Spell {
	public final String name;
	public final String icon;
	public final RGB color;
	public final int price;
	public final int manna;
	public final int multiplier;
	public final Hero hero;

	private boolean active = false;
	protected static final int EFFECT_TIME = 1000;

	public Spell(String name, String icon, RGB color, int price, int manna, int multiplier, Hero hero) {
		this.name = name;
		this.icon = icon;
		this.color = color;
		this.price = price;
		this.manna = manna;
		this.multiplier = multiplier;
		this.hero = hero;
	}

	public int osszed(int a, int b) {
		return a + b;
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

	public void effect(Tile tile) {
		tile.setCursor();
		Console.setBackground(this.color);
		Console.setForeground(Colors.textFromBg(this.color));
		tile.draw(this.icon);
		Console.resetStyles();
	}
}
