package Spells;

import Board.Tile;
import Hero.Hero;
import Units.Unit;
import View.IO;

public class Fireball extends Spell {
	public Fireball(Hero hero) {
		super("Tűzlabda", "☄", 120, 9, 20, hero);
	}

	@Override
	public void cast() {
		Tile tile = IO.scanTile();

		if (tile.hasUnit()) {
			Unit unit = tile.getUnit();
			unit.takeDamage(this.getValue());
		}

		for (Tile neighbour : tile.getNeighbours()) {
			if (!neighbour.hasUnit()) continue;

			Unit unit = neighbour.getUnit();
			unit.takeDamage(this.getValue());
		}

		this.hero.useManna(this.manna);
	}

	@Override
	public void effect(Tile tile) {
		tile.setCursor();
	}
}
