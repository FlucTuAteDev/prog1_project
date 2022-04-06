package Spells;

import Base.Game;
import Board.Tile;
import Hero.Hero;
import View.IO;

public class Fireball extends Spell {
	public Fireball(Hero hero) {
		super("Tűzlabda", 120, 9, 20, hero);
	}

	@Override
	public void cast() {
		Tile tile = IO.scanTile();

		if (tile.hasUnit()) {
			tile.getUnit().takeDamage(this.getValue());
			Game.board.redrawUnit(tile.getUnit());
		}

		for (Tile neighbour : tile.getNeighbours()) {
			if (!neighbour.hasUnit()) continue;

			neighbour.getUnit().takeDamage(this.getValue());
			Game.board.redrawUnit(neighbour.getUnit());
		}
	}
}
