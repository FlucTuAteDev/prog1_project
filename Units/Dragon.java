package Units;

import Hero.Hero;

/**
 * Defines how a dragon should behave
 */
public class Dragon extends Unit {
	public Dragon(Hero hero) {
		super("Sárkány", "🐲", "Végtelen visszatámadás", hero, 20, 5, 12, 25, 6, 17);
	}

	@Override
	public void counterAttack(Unit other) {
		super.counterAttack(other);
		this.canCounter = true;
	}
}
