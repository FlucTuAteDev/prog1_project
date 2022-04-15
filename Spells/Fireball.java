package Spells;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Base.Console;
import Base.Game;
import Board.Tile;
import Hero.Hero;
import Units.Unit;
import View.Colors.Colors;

/**
 * Defines how the fireball spell should behave
 */
public class Fireball extends Spell {
	public Fireball(Hero hero) {
		super("Tűzlabda", "🔥", "3x3-as területen sebez", Colors.DAMAGE, 120, 9, 20, hero);
	}

	@Override
	public Tile scan() {
		return Console.scanTile();
	}

	@Override
	public Tile generate() {
		return this.hero.getEnemy().getLowestHpUnit().getTile();
	}

	@Override
	public void cast(Tile tile) {
		if (!this.hero.useManna(this.manna))
			return;

		List<Tile> affectedTiles = new ArrayList<>(Arrays.asList(tile.getNeighbours()));
		affectedTiles.add(tile);

		for (Tile affectedTile : affectedTiles) {
			// Firendy units take damage as well
			if (affectedTile.hasUnit()) {
				Unit unit = affectedTile.getUnit();
				int damage = (int)Math.round(this.getValue());

				Hero hero = this.hero;
				Game.logMessage("%s %s %s: -%d❤ -> -%ddb", 
					hero, this.icon, unit,
					damage, damage / unit.baseHealth);

				unit.takeDamage(damage);
			}
		}

		Tile.effect(affectedTiles, Colors.RED, this.icon);
		
		// Restore tiles
		affectedTiles.forEach(Tile::draw);
	}
}
