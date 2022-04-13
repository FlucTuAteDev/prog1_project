package Hero;

import Base.Game;
import Board.Tile;
import Units.Unit;
import Utils.Vector;
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
			Unit target = hero.getEnemy().getAliveUnits().stream().min((a, b) -> a.getHealth() - b.getHealth()).get();
			
			Vector direction = Tile.direction(target.getTile(), unit.getTile());
			Vector step = new Vector(
				(int)Math.signum(direction.row) * Math.min(Math.abs(direction.row), unit.speed),
				(int)Math.signum(direction.col) * Math.min(Math.abs(direction.col), unit.speed)
			);

			// If the target unit is outside the range of the current unit then
			// step to the closest cell to the unit that it can reach
			int dist = Tile.distance(unit.getTile(), target.getTile());
			if (dist > unit.speed) {
				Tile dest = Game.board.getTile(step.add(unit.getTile().position));
				unit.move(dest);
			// Otherwise move to the nearest spot that isn't the target unit
			} else {
				Vector inverse = direction.inverse();
				Tile dest = Game.board.getTile(target.getTile().position.add(new Vector((int)Math.signum(inverse.row), (int)Math.signum(inverse.col))));
				unit.move(dest);
			}
		}
	}
}
