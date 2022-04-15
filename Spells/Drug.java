package Spells;

import Base.Game;
import Board.Tile;
import Hero.Hero;
import Units.Unit;
import View.Colors.Colors;

/**
 * Defines how the drug spell should behave
 */
public class Drug extends Spell {
	public Drug(Hero hero) {
		super("Gyógyszer", "💊", "Gyógyítja az összes egységet", Colors.HEAL, 80, 5, 20, hero);
	}

	@Override
	public Tile generate() {
		return null;
	}

	@Override
	public Tile scan() {
		return null;
	}

	@Override
	public void cast(Tile tile) {
		if (!this.hero.useManna(this.manna))
			return;

		int heal = (int)Math.round(this.getValue());

		for (Unit unit : Game.getAliveUnits())
			unit.heal(heal);

		Tile.effect(Game.getAliveUnits().stream().map(Unit::getTile).toList(), this.color, this.icon);

		Game.logMessage("%s %s Minden egység: +%d❤", 
			hero, this.icon, heal);
	}
	
}
