package Units;

import java.util.List;

import Base.Game;
import Hero.Hero;

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
		
		Hero enemy = this.hero == Game.player ? Game.ai : Game.player;
		return enemy.getAliveUnits();
	}
}
