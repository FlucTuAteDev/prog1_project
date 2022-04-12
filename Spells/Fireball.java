package Spells;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Base.Console;
import Board.Tile;
import Hero.Hero;
import Units.Unit;
import Utils.ThreadHelper;
import View.Colors.Colors;

public class Fireball extends Spell {
	public Fireball(Hero hero) {
		super("Tűzlabda", "🔥", Colors.DAMAGE, 120, 9, 20, hero);
	}

	@Override
	public void cast() {
		if (!this.hero.useManna(this.manna))
			return;

		Tile tile = Console.scanTile();
		List<Tile> affectedTiles = new ArrayList<>(Arrays.asList(tile.getNeighbours()));
		affectedTiles.add(tile);

		for (Tile affectedTile : affectedTiles) {
			// Firendy units take damage as well
			if (affectedTile.hasUnit()) {
				Unit unit = affectedTile.getUnit();
				this.use(unit);
			}

			this.effect(affectedTile);
		}

		ThreadHelper.sleep(Spell.EFFECT_TIME);
		
		// Restore tiles
		affectedTiles.forEach(Tile::draw);
	}

	@Override
	public void effect(Tile tile) {
		tile.setCursor();
		Console.setBackground(Colors.DAMAGE);
		Console.setForeground(Colors.textFromBg(Colors.DAMAGE));
		tile.draw(this.icon);
		Console.resetStyles();
	}
}
