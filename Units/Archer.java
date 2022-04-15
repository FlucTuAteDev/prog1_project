package Units;

import java.util.List;

import Hero.Hero;

/**
 * Defines how an archer should behave
 */
public class Archer extends Unit {
	public Archer(Hero hero) {
		super("Íjász", "🏹", hero, 6, 2, 4, 7, 4, 9);
	}

	@Override
	public List<Unit> attackableUnits() {
		// An archer can only attack a unit at a distance if there aren't any units neighbouring it
		// If it has neighbours it can attack those
		var neighbours = super.attackableUnits();
		if (neighbours.size() != 0)
			return neighbours;
		
		Hero enemy = this.hero.getEnemy();
		return enemy.getAliveUnits();
	}
}
