package Hero;

import Board.Tile;
import Units.Unit;
import Utils.Position;
import View.View;
import View.Colors.RGB;

public class AI extends Hero {

	public AI(String name, RGB color, View view) {
		super(name, color, view);
	}

	@Override
	public void takeTurn(Unit unit) {
		Hero hero = unit.hero;
		if (unit.attackableUnits().size() > 0) {
			Unit target = unit.attackableUnits().stream().min((a, b) -> a.getHealth() - b.getHealth()).get();

			unit.attack(target);
		} else {
			// Move towards lowest hp
			Unit lowestHp = hero.getEnemy().getUnits().stream().min((a, b) -> a.getHealth() - b.getHealth()).get();
			
			int distance = Tile.distance(unit.getTile(), lowestHp.getTile());
			if (distance <= unit.speed) {
				
			}
		}
	}
}
