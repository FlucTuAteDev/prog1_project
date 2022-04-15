package Spells;

import java.util.Comparator;

import Base.Game;
import Board.Tile;
import Hero.Hero;
import Menu.*;
import Menu.Items.MenuItem;
import Units.Unit;
import View.Colors.Colors;

/**
 * Defines how the resurrection spell should behave
 */
public class Resurrection extends Spell {
	public Resurrection(Hero hero) {
		super("Feltámasztás", "💉", "Gyógyít egy egységet", Colors.HEAL, 120, 6, 50, hero);
	}

	@Override
	public Tile generate() {
		Unit unit = this.hero.getAliveUnits().stream()
			.filter(x -> x.getHealth() != x.baseHealth * x.getMaxCount())
			.min(Comparator.comparingInt(Unit::getHealth))
			.orElse(null);

		return unit != null ? unit.getTile() : null;
	}

	@Override
	public Tile scan() {
		Menu<Unit> menu = new BasicMenu<>("Feltámasztható egységek: ", Game.menuView);

		for (Unit unit : this.hero.getAliveUnits()) {
			menu.addItem(new MenuItem<>(unit, null,
				Colors.textFromBg(this.hero.COLOR), 
				this.hero.COLOR,
				v -> {}, "%s", v -> v.icon));
		}
		
		return menu.display().getTile();
	}

	@Override
	public void cast(Tile tile) {
		if (!this.hero.useManna(this.manna))
			return;

		Unit unit = tile.getUnit();
		int heal = (int)Math.round(this.getValue());

		int prevCount = unit.getCount();
		unit.heal(heal);
		unit.getTile().effect(Colors.GREEN, this.icon);

		Game.logMessage("%s %s %s: +%d❤ -> +%ddb", 
			hero, this.icon, unit,
			heal, unit.getCount() - prevCount);
	}
}
