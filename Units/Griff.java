package Units;

import Hero.Hero;

/**
 * Defines how a griff should behave
 */
public class Griff extends Unit {
	public Griff(Hero hero) {
		super("Griff", "🦅", "Végtelen visszatámadás", hero, 15, 4, 10, 30, 7, 15);
	}

	@Override
	public void counterAttack(Unit other) {
		super.counterAttack(other);
		this.canCounter = true;
	}
}
